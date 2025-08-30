# HashMap 源码分析与核心机制

## 概述

HashMap 是 Java 集合框架中最重要的 Map 实现之一，基于哈希表实现，提供 O(1) 的平均时间复杂度进行查找、插入和删除操作。本文档深入分析 HashMap 的核心机制和源码实现。

## 核心数据结构

### 基本结构

```java
public class HashMap<K,V> extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable {
    
    // 默认初始容量 16
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    
    // 最大容量
    static final int MAXIMUM_CAPACITY = 1 << 30;
    
    // 默认负载因子 0.75
    static final float DEFAULT_LOAD_FACTOR = 0.75f;
    
    // 树化阈值：链表长度超过8时转换为红黑树
    static final int TREEIFY_THRESHOLD = 8;
    
    // 退化阈值：红黑树节点少于6时转换回链表
    static final int UNTREEIFY_THRESHOLD = 6;
    
    // 最小树化容量：只有当容量>=64时才会树化
    static final int MIN_TREEIFY_CAPACITY = 64;
    
    // 哈希桶数组
    transient Node<K,V>[] table;
    
    // 键值对数量
    transient int size;
    
    // 结构修改次数（fail-fast机制）
    transient int modCount;
    
    // 扩容阈值 = capacity * loadFactor
    int threshold;
    
    // 负载因子
    final float loadFactor;
}
```

### Node 节点结构

```java
static class Node<K,V> implements Map.Entry<K,V> {
    final int hash;    // 哈希值
    final K key;       // 键
    V value;           // 值
    Node<K,V> next;    // 下一个节点（链表）
}
```

## 核心机制详解

### 1. 哈希扰动函数

#### 扰动函数实现

```java
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

#### 扰动原理

- **目的**：减少哈希冲突，提高分布均匀性
- **方法**：将 hashCode 的高16位与低16位进行异或运算
- **效果**：让高位也参与到桶索引的计算中

#### 桶索引计算

```java
// 计算桶索引：(n-1) & hash
int index = (table.length - 1) & hash(key);
```

**为什么使用 `(n-1) & hash` 而不是 `hash % n`？**

1. **性能优势**：位运算比取模运算快
2. **前提条件**：容量必须是2的幂次方
3. **等价性**：当 n 为2的幂次方时，`(n-1) & hash` 等价于 `hash % n`

### 2. Put 操作流程

#### 完整流程图

```
put(key, value)
    ↓
计算 hash(key)
    ↓
计算桶索引 index = (n-1) & hash
    ↓
桶为空？
├─ 是 → 直接插入新节点
└─ 否 → 遍历链表/树
    ├─ 找到相同key → 更新value
    └─ 未找到 → 插入到链表尾部
        ↓
    链表长度 >= 8 且 容量 >= 64？
    ├─ 是 → 树化（链表转红黑树）
    └─ 否 → 保持链表
        ↓
    size++ > threshold？
    ├─ 是 → 扩容 resize()
    └─ 否 → 结束
```

#### 关键代码逻辑

```java
public V put(K key, V value) {
    return putVal(hash(key), key, value, false, true);
}

final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {
    Node<K,V>[] tab; Node<K,V> p; int n, i;
    
    // 1. 初始化或扩容
    if ((tab = table) == null || (n = tab.length) == 0)
        n = (tab = resize()).length;
    
    // 2. 桶为空，直接插入
    if ((p = tab[i = (n - 1) & hash]) == null)
        tab[i] = newNode(hash, key, value, null);
    else {
        Node<K,V> e; K k;
        
        // 3. 第一个节点就是目标
        if (p.hash == hash && ((k = p.key) == key || (key != null && key.equals(k))))
            e = p;
        // 4. 红黑树节点
        else if (p instanceof TreeNode)
            e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
        // 5. 链表节点
        else {
            for (int binCount = 0; ; ++binCount) {
                if ((e = p.next) == null) {
                    p.next = newNode(hash, key, value, null);
                    // 链表长度达到阈值，考虑树化
                    if (binCount >= TREEIFY_THRESHOLD - 1)
                        treeifyBin(tab, hash);
                    break;
                }
                if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k))))
                    break;
                p = e;
            }
        }
        
        // 6. 更新已存在的键
        if (e != null) {
            V oldValue = e.value;
            if (!onlyIfAbsent || oldValue == null)
                e.value = value;
            afterNodeAccess(e);
            return oldValue;
        }
    }
    
    ++modCount;
    // 7. 检查是否需要扩容
    if (++size > threshold)
        resize();
    afterNodeInsertion(evict);
    return null;
}
```

### 3. 扩容机制（Resize）

#### 扩容触发条件

- **时机**：`size > threshold`（即 `size > capacity * loadFactor`）
- **新容量**：`newCapacity = oldCapacity << 1`（扩大为原来的2倍）
- **新阈值**：`newThreshold = newCapacity * loadFactor`

#### 扩容过程

```java
final Node<K,V>[] resize() {
    Node<K,V>[] oldTab = table;
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    int oldThr = threshold;
    int newCap, newThr = 0;
    
    // 1. 计算新容量和新阈值
    if (oldCap > 0) {
        if (oldCap >= MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return oldTab;
        }
        else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY && oldCap >= DEFAULT_INITIAL_CAPACITY)
            newThr = oldThr << 1; // 阈值也翻倍
    }
    // ... 其他初始化情况
    
    // 2. 创建新数组
    Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
    table = newTab;
    
    // 3. 重新哈希（rehash）
    if (oldTab != null) {
        for (int j = 0; j < oldCap; ++j) {
            Node<K,V> e;
            if ((e = oldTab[j]) != null) {
                oldTab[j] = null;
                
                // 单个节点
                if (e.next == null)
                    newTab[e.hash & (newCap - 1)] = e;
                // 红黑树
                else if (e instanceof TreeNode)
                    ((TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                // 链表
                else {
                    // 优化：将链表分为两部分
                    Node<K,V> loHead = null, loTail = null;
                    Node<K,V> hiHead = null, hiTail = null;
                    Node<K,V> next;
                    
                    do {
                        next = e.next;
                        // 根据 (e.hash & oldCap) 分组
                        if ((e.hash & oldCap) == 0) {
                            // 低位链表：索引不变
                            if (loTail == null) loHead = e;
                            else loTail.next = e;
                            loTail = e;
                        } else {
                            // 高位链表：索引 = 原索引 + oldCap
                            if (hiTail == null) hiHead = e;
                            else hiTail.next = e;
                            hiTail = e;
                        }
                    } while ((e = next) != null);
                    
                    // 放置到新数组
                    if (loTail != null) {
                        loTail.next = null;
                        newTab[j] = loHead;
                    }
                    if (hiTail != null) {
                        hiTail.next = null;
                        newTab[j + oldCap] = hiHead;
                    }
                }
            }
        }
    }
    return newTab;
}
```

#### 扩容优化：链表分组

**核心思想**：利用 `(e.hash & oldCap)` 的结果将原链表分为两组

- **结果为0**：新索引 = 原索引
- **结果为1**：新索引 = 原索引 + oldCap

**优势**：避免重新计算每个节点的哈希值，提高扩容效率

### 4. 树化与退化

#### 树化条件

1. **链表长度** >= 8（`TREEIFY_THRESHOLD`）
2. **数组容量** >= 64（`MIN_TREEIFY_CAPACITY`）

**注意**：如果链表长度 >= 8 但容量 < 64，会优先扩容而不是树化

#### 树化过程

```java
final void treeifyBin(Node<K,V>[] tab, int hash) {
    int n, index; Node<K,V> e;
    
    // 容量不足时优先扩容
    if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)
        resize();
    // 执行树化
    else if ((e = tab[index = (n - 1) & hash]) != null) {
        TreeNode<K,V> hd = null, tl = null;
        
        // 1. 将链表节点转换为树节点
        do {
            TreeNode<K,V> p = replacementTreeNode(e, null);
            if (tl == null) hd = p;
            else {
                p.prev = tl;
                tl.next = p;
            }
            tl = p;
        } while ((e = e.next) != null);
        
        // 2. 构建红黑树
        if ((tab[index] = hd) != null)
            hd.treeify(tab);
    }
}
```

#### 退化条件

- **删除操作**：当红黑树节点数量 <= 6（`UNTREEIFY_THRESHOLD`）时退化为链表
- **扩容操作**：扩容时如果某个桶的树节点数量过少也会退化

### 5. 负载因子与性能

#### 负载因子的影响

| 负载因子 | 空间利用率 | 时间复杂度 | 冲突概率 |
|---------|-----------|-----------|----------|
| 0.5     | 低        | 接近O(1)   | 很低     |
| 0.75    | 中等      | 接近O(1)   | 低       |
| 1.0     | 高        | O(1)~O(n) | 中等     |
| 1.5     | 很高      | 趋向O(n)   | 高       |

#### 为什么选择 0.75？

- **空间与时间的平衡**：既不浪费太多空间，也不会有太多冲突
- **数学依据**：泊松分布计算表明，0.75是一个较优的平衡点
- **实践验证**：大量实际应用证明这是一个合适的值

### 6. Fail-Fast 机制

#### 实现原理

```java
// 每次结构性修改都会增加 modCount
++modCount;

// 迭代器检查
final class HashIterator {
    int expectedModCount = modCount;
    
    final Node<K,V> nextNode() {
        if (modCount != expectedModCount)
            throw new ConcurrentModificationException();
        // ...
    }
}
```

#### 触发条件

- **结构性修改**：put、remove、clear 等改变 size 的操作
- **并发修改**：迭代过程中其他线程修改 Map
- **自修改**：迭代过程中通过 Map 接口（而非迭代器）修改

## 性能分析

### 时间复杂度

| 操作 | 平均情况 | 最坏情况 | 说明 |
|------|---------|---------|------|
| get  | O(1)    | O(log n) | 最坏情况是红黑树查找 |
| put  | O(1)    | O(log n) | 包含可能的扩容和树化 |
| remove | O(1)  | O(log n) | 包含可能的退化 |

### 空间复杂度

- **基本空间**：O(n)，n 为键值对数量
- **额外空间**：O(capacity)，capacity 为数组容量
- **空间利用率**：平均 75%（负载因子 0.75）

### 性能优化建议

1. **合理设置初始容量**：避免频繁扩容
   ```java
   // 预知大小时设置合适的初始容量
   Map<String, String> map = new HashMap<>(expectedSize * 4 / 3 + 1);
   ```

2. **选择合适的负载因子**：默认 0.75 通常是最佳选择

3. **避免哈希冲突**：
   - 实现高质量的 hashCode() 方法
   - 避免使用容易冲突的键类型

4. **减少装箱拆箱**：使用基本类型的专用 Map（如 TIntObjectHashMap）

## 与其他 Map 实现的对比

### HashMap vs LinkedHashMap

| 特性 | HashMap | LinkedHashMap |
|------|---------|---------------|
| 插入顺序 | 不保证 | 保证 |
| 访问顺序 | 不支持 | 可选支持 |
| 内存开销 | 较低 | 较高（额外链表） |
| 迭代性能 | O(capacity) | O(size) |

### HashMap vs TreeMap

| 特性 | HashMap | TreeMap |
|------|---------|----------|
| 排序 | 无序 | 有序 |
| 时间复杂度 | O(1) | O(log n) |
| 空间复杂度 | O(n) | O(n) |
| 键要求 | hashCode/equals | Comparable/Comparator |

### HashMap vs ConcurrentHashMap

| 特性 | HashMap | ConcurrentHashMap |
|------|---------|-------------------|
| 线程安全 | 否 | 是 |
| 性能 | 高 | 中等 |
| 锁机制 | 无 | 分段锁/CAS |
| null值 | 支持 | 不支持 |

## 常见陷阱与注意事项

### 1. 哈希冲突导致的性能退化

```java
// 错误：所有对象都有相同的哈希值
class BadKey {
    @Override
    public int hashCode() {
        return 1; // 所有实例都返回相同哈希值
    }
}

// 正确：实现高质量的哈希函数
class GoodKey {
    private final String name;
    private final int id;
    
    @Override
    public int hashCode() {
        return Objects.hash(name, id);
    }
}
```

### 2. 并发修改异常

```java
// 错误：迭代时修改
Map<String, String> map = new HashMap<>();
for (String key : map.keySet()) {
    if (someCondition(key)) {
        map.remove(key); // 抛出 ConcurrentModificationException
    }
}

// 正确：使用迭代器删除
Iterator<String> iterator = map.keySet().iterator();
while (iterator.hasNext()) {
    String key = iterator.next();
    if (someCondition(key)) {
        iterator.remove(); // 安全删除
    }
}
```

### 3. 容量设置不当

```java
// 低效：频繁扩容
Map<String, String> map = new HashMap<>(); // 默认容量16
for (int i = 0; i < 1000; i++) {
    map.put("key" + i, "value" + i); // 会触发多次扩容
}

// 高效：预设合适容量
Map<String, String> map = new HashMap<>(1500); // 1000 / 0.75 ≈ 1334
for (int i = 0; i < 1000; i++) {
    map.put("key" + i, "value" + i); // 避免扩容
}
```

### 4. 键对象的可变性

```java
// 危险：可变键对象
class MutableKey {
    private String value;
    
    public void setValue(String value) {
        this.value = value; // 修改后哈希值可能改变
    }
    
    @Override
    public int hashCode() {
        return value.hashCode();
    }
}

// 安全：不可变键对象
class ImmutableKey {
    private final String value;
    
    public ImmutableKey(String value) {
        this.value = value;
    }
    
    // 没有setter方法，保证不可变
}
```

## 最佳实践

### 1. 初始化

```java
// 已知大小时预设容量
int expectedSize = 1000;
Map<String, String> map = new HashMap<>(expectedSize * 4 / 3 + 1);

// 使用工厂方法（Java 9+）
Map<String, String> map = Map.of("key1", "value1", "key2", "value2");
```

### 2. 键设计

```java
// 实现高质量的 hashCode 和 equals
@Override
public int hashCode() {
    return Objects.hash(field1, field2, field3);
}

@Override
public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    MyKey myKey = (MyKey) obj;
    return Objects.equals(field1, myKey.field1) &&
           Objects.equals(field2, myKey.field2) &&
           Objects.equals(field3, myKey.field3);
}
```

### 3. 安全迭代

```java
// 使用 removeIf 方法（Java 8+）
map.entrySet().removeIf(entry -> someCondition(entry.getKey()));

// 或者收集要删除的键
List<String> keysToRemove = map.keySet().stream()
    .filter(this::shouldRemove)
    .collect(Collectors.toList());
keysToRemove.forEach(map::remove);
```

### 4. 性能监控

```java
// 监控负载因子
public double getLoadFactor(HashMap<?, ?> map) {
    try {
        Field sizeField = HashMap.class.getDeclaredField("size");
        Field tableField = HashMap.class.getDeclaredField("table");
        sizeField.setAccessible(true);
        tableField.setAccessible(true);
        
        int size = (Integer) sizeField.get(map);
        Object[] table = (Object[]) tableField.get(map);
        int capacity = table == null ? 0 : table.length;
        
        return capacity == 0 ? 0 : (double) size / capacity;
    } catch (Exception e) {
        return -1; // 无法获取
    }
}
```

## 示例代码位置

- **源码演示**：`src/main/java/com/study/collections/map/HashMapSourceDemo.java`
- **单元测试**：`src/test/java/com/study/collections/map/HashMapSourceDemoTest.java`
- **性能测试**：参考基准测试模块

## 扩展阅读

1. **红黑树原理**：了解树化后的数据结构
2. **一致性哈希**：分布式环境下的哈希策略
3. **ConcurrentHashMap**：并发安全的 Map 实现
4. **内存布局**：HashMap 在 JVM 中的内存占用分析

---

*本文档基于 JDK 8+ 的 HashMap 实现，不同版本可能存在细微差异。*