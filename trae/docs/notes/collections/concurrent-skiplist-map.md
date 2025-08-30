# ConcurrentSkipListMap 源码深度解析

## 概述

`ConcurrentSkipListMap` 是 Java 并发包中提供的线程安全的有序 Map 实现，基于跳表（Skip List）数据结构。它在保证线程安全的同时，提供了高效的有序访问和范围查询能力。

## 核心特性

### 1. 跳表数据结构

跳表是一种概率性数据结构，通过多层索引来实现快速查找：

```
层级 3:     1 ---------> 7 ---------> 19
层级 2:     1 -----> 4 -> 7 -----> 12 -> 19 -> 25
层级 1:     1 -> 3 -> 4 -> 7 -> 9 -> 12 -> 19 -> 21 -> 25
层级 0:     1 -> 3 -> 4 -> 7 -> 9 -> 12 -> 19 -> 21 -> 25 -> 26
```

**跳表优势：**
- 查找时间复杂度：O(log n)
- 插入时间复杂度：O(log n)
- 删除时间复杂度：O(log n)
- 空间复杂度：O(n)
- 支持范围查询
- 天然有序

### 2. 线程安全机制

`ConcurrentSkipListMap` 使用 CAS（Compare-And-Swap）操作来保证线程安全：

```java
// 核心节点结构（简化）
static final class Node<K,V> {
    final K key;
    volatile Object value;
    volatile Node<K,V> next;
    
    // CAS 操作
    boolean casValue(Object cmp, Object val) {
        return UNSAFE.compareAndSwapObject(this, valueOffset, cmp, val);
    }
    
    boolean casNext(Node<K,V> cmp, Node<K,V> val) {
        return UNSAFE.compareAndSwapObject(this, nextOffset, cmp, val);
    }
}
```

**线程安全特点：**
- 无锁设计，使用 CAS 操作
- 读操作无需加锁
- 写操作通过 CAS 保证原子性
- 支持高并发读写

### 3. 有序性保证

`ConcurrentSkipListMap` 通过比较器或自然排序来维护键的有序性：

```java
// 自然排序
ConcurrentSkipListMap<Integer, String> naturalOrder = new ConcurrentSkipListMap<>();

// 自定义比较器
ConcurrentSkipListMap<Integer, String> reverseOrder = 
    new ConcurrentSkipListMap<>(Collections.reverseOrder());

// 复杂对象排序
ConcurrentSkipListMap<Student, String> studentMap = new ConcurrentSkipListMap<>();
```

## 核心操作分析

### 1. 查找操作（get）

```java
public V get(Object key) {
    return doGet(key);
}

private V doGet(Object key) {
    if (key == null)
        throw new NullPointerException();
    Comparator<? super K> cmp = comparator;
    outer: for (;;) {
        for (Node<K,V> b = findPredecessor(key, cmp), n = b.next;;) {
            Object v; int c;
            if (n == null)
                break outer;
            Node<K,V> f = n.next;
            if (n != b.next)                // 不一致读，重试
                break;
            if ((v = n.value) == null) {    // n 被删除
                n.helpDelete(b, f);
                break;
            }
            if (b.value == null || v == n) // b 被删除
                break;
            if ((c = cpr(cmp, key, n.key)) == 0) {
                @SuppressWarnings("unchecked") V vv = (V)v;
                return vv;
            }
            if (c < 0)
                break outer;
            b = n;
            n = f;
        }
    }
    return null;
}
```

**查找流程：**
1. 从最高层开始查找
2. 在每一层中向右移动，直到找到大于等于目标键的节点
3. 向下移动到下一层
4. 重复直到找到目标或确定不存在

### 2. 插入操作（put）

```java
public V put(K key, V value) {
    if (value == null)
        throw new NullPointerException();
    return doPut(key, value, false);
}

private V doPut(K key, V value, boolean onlyIfAbsent) {
    Node<K,V> z;             // 新节点
    if (key == null)
        throw new NullPointerException();
    Comparator<? super K> cmp = comparator;
    outer: for (;;) {
        for (Node<K,V> b = findPredecessor(key, cmp), n = b.next;;) {
            if (n != null) {
                Object v; int c;
                Node<K,V> f = n.next;
                if (n != b.next)               // 不一致读
                    break;
                if ((v = n.value) == null) {   // n 被删除
                    n.helpDelete(b, f);
                    break;
                }
                if (b.value == null || v == n) // b 被删除
                    break;
                if ((c = cpr(cmp, key, n.key)) > 0) {
                    b = n;
                    n = f;
                    continue;
                }
                if (c == 0) {
                    if (onlyIfAbsent || n.casValue(v, value)) {
                        @SuppressWarnings("unchecked") V vv = (V)v;
                        return vv;
                    }
                    break; // 重试
                }
                // c < 0; 找到插入位置
            }

            z = new Node<K,V>(key, value, n);
            if (!b.casNext(n, z))
                break;         // 重试
            break outer;
        }
    }

    int rnd = ThreadLocalRandom.nextSecondarySeed();
    if ((rnd & 0x80000001) == 0) { // 测试最低位和最高位
        int level = 1, max;
        while (((rnd >>>= 1) & 1) != 0)
            ++level;
        Index<K,V> idx = null;
        HeadIndex<K,V> h = head;
        if (level <= (max = h.level)) {
            for (int i = 1; i <= level; ++i)
                idx = new Index<K,V>(z, idx, null);
        }
        else { // 尝试增长
            level = max + 1; // 最多增长一层
            @SuppressWarnings("unchecked")Index<K,V>[] idxs =
                (Index<K,V>[])new Index<?,?>[level+1];
            for (int i = 1; i <= level; ++i)
                idxs[i] = idx = new Index<K,V>(z, idx, null);
            for (;;) {
                h = head;
                int oldLevel = h.level;
                if (level <= oldLevel) // 丢失竞争
                    break;
                HeadIndex<K,V> newh = h;
                Node<K,V> oldbase = h.node;
                for (int j = oldLevel+1; j <= level; ++j)
                    newh = new HeadIndex<K,V>(oldbase, newh, idxs[j], j);
                if (casHead(h, newh)) {
                    h = newh;
                    idx = idxs[level = oldLevel];
                    break;
                }
            }
        }
        // 找到插入位置并插入索引
        splice: for (int insertionLevel = level;;) {
            int j = h.level;
            for (Index<K,V> q = h, r = q.right, t = idx;;) {
                if (q == null || t == null)
                    break splice;
                if (r != null) {
                    Node<K,V> n = r.node;
                    // 比较键
                    int c = cpr(cmp, key, n.key);
                    if (n.value == null) {
                        if (!q.unlink(r))
                            break;
                        r = q.right;
                        continue;
                    }
                    if (c > 0) {
                        q = r;
                        r = r.right;
                        continue;
                    }
                }

                if (j == insertionLevel) {
                    if (!q.link(r, t))
                        break; // 重试
                    if (t.node.value == null) {
                        findNode(key);
                        break splice;
                    }
                    if (--insertionLevel == 0)
                        break splice;
                }

                if (--j >= insertionLevel && j < level)
                    t = t.down;
                q = q.down;
                r = q.right;
            }
        }
    }
    return null;
}
```

**插入流程：**
1. 查找插入位置
2. 创建新节点并插入到底层链表
3. 随机决定是否创建索引层
4. 在各个索引层插入索引节点

### 3. 删除操作（remove）

删除操作分为两个阶段：
1. **标记删除**：将节点的值设置为 null
2. **物理删除**：从链表中移除节点

```java
public V remove(Object key) {
    return doRemove(key, null);
}

final V doRemove(Object key, Object value) {
    if (key == null)
        throw new NullPointerException();
    Comparator<? super K> cmp = comparator;
    outer: for (;;) {
        for (Node<K,V> b = findPredecessor(key, cmp), n = b.next;;) {
            Object v; int c;
            if (n == null)
                break outer;
            Node<K,V> f = n.next;
            if (n != b.next)                    // 不一致读
                break;
            if ((v = n.value) == null) {        // 已被删除
                n.helpDelete(b, f);
                break;
            }
            if (b.value == null || v == n)      // b 被删除
                break;
            if ((c = cpr(cmp, key, n.key)) < 0)
                break outer;
            if (c > 0) {
                b = n;
                n = f;
                continue;
            }
            if (value != null && !value.equals(v))
                break outer;
            if (!n.casValue(v, null))
                break;
            if (!n.appendMarker(f) || !b.casNext(n, f))
                findNode(key);                  // 重试删除
            else {
                findPredecessor(key, cmp);      // 清理索引
                if (head.right == null)
                    tryReduceLevel();
            }
            @SuppressWarnings("unchecked") V vv = (V)v;
            return vv;
        }
    }
    return null;
}
```

## 范围查询功能

### 1. 子映射（SubMap）

```java
// 范围查询 [fromKey, toKey]
NavigableMap<K,V> subMap = map.subMap(fromKey, true, toKey, true);

// 头部映射 < toKey
NavigableMap<K,V> headMap = map.headMap(toKey, false);

// 尾部映射 >= fromKey
NavigableMap<K,V> tailMap = map.tailMap(fromKey, true);
```

### 2. 边界查询

```java
// 最小/最大键
K firstKey = map.firstKey();
K lastKey = map.lastKey();

// 查找最接近的键
K lowerKey = map.lowerKey(key);     // < key
K floorKey = map.floorKey(key);     // <= key
K ceilingKey = map.ceilingKey(key); // >= key
K higherKey = map.higherKey(key);   // > key
```

### 3. 范围查询实现原理

```java
public NavigableMap<K,V> subMap(K fromKey, boolean fromInclusive,
                                K toKey, boolean toInclusive) {
    if (fromKey == null || toKey == null)
        throw new NullPointerException();
    return new SubMap<K,V>(this, fromKey, fromInclusive,
                           toKey, toInclusive, false);
}

// SubMap 内部类实现
static final class SubMap<K,V> extends AbstractMap<K,V>
    implements NavigableMap<K,V>, java.io.Serializable {
    
    private final ConcurrentSkipListMap<K,V> m;
    private final K lo, hi;
    private final boolean loInclusive, hiInclusive;
    private final boolean isDescending;
    
    // 检查键是否在范围内
    private boolean inBounds(Object key) {
        return !tooLow(key) && !tooHigh(key);
    }
    
    private boolean tooLow(Object key) {
        int c;
        return (lo != null && ((c = m.compare(key, lo)) < 0 ||
                               (c == 0 && !loInclusive)));
    }
    
    private boolean tooHigh(Object key) {
        int c;
        return (hi != null && ((c = m.compare(key, hi)) > 0 ||
                               (c == 0 && !hiInclusive)));
    }
}
```

## 性能特性分析

### 1. 时间复杂度

| 操作 | 平均时间复杂度 | 最坏时间复杂度 |
|------|----------------|----------------|
| 查找 | O(log n) | O(n) |
| 插入 | O(log n) | O(n) |
| 删除 | O(log n) | O(n) |
| 范围查询 | O(log n + k) | O(n) |

其中 k 是结果集大小。

### 2. 空间复杂度

- **基本空间**：O(n)
- **索引空间**：平均 O(n)，最坏 O(n log n)
- **总空间复杂度**：O(n)

### 3. 并发性能

```java
// 并发读取：无锁，高性能
String value = map.get(key);

// 并发写入：CAS 操作，避免锁竞争
map.put(key, value);

// 并发范围查询：读操作，无锁
NavigableMap<K,V> subMap = map.subMap(startKey, endKey);
```

**并发优势：**
- 读操作完全无锁
- 写操作使用 CAS，避免重量级锁
- 支持高并发读写
- 无锁死风险

## 与其他 Map 实现对比

### 1. ConcurrentSkipListMap vs TreeMap

| 特性 | ConcurrentSkipListMap | TreeMap |
|------|----------------------|----------|
| 线程安全 | 是（无锁） | 否 |
| 有序性 | 是 | 是 |
| 查找性能 | O(log n) | O(log n) |
| 插入性能 | O(log n) | O(log n) |
| 并发读 | 高性能 | 需要同步 |
| 并发写 | 支持 | 需要同步 |
| 内存使用 | 较高（索引层） | 较低 |

### 2. ConcurrentSkipListMap vs ConcurrentHashMap

| 特性 | ConcurrentSkipListMap | ConcurrentHashMap |
|------|----------------------|-------------------|
| 线程安全 | 是 | 是 |
| 有序性 | 是 | 否 |
| 查找性能 | O(log n) | O(1) 平均 |
| 范围查询 | 支持 | 不支持 |
| 内存使用 | 较高 | 较低 |
| 适用场景 | 需要有序+并发 | 高性能哈希表 |

### 3. 选择建议

```java
// 需要有序且线程安全
ConcurrentSkipListMap<K,V> orderedConcurrentMap = new ConcurrentSkipListMap<>();

// 只需要线程安全，不需要有序
ConcurrentHashMap<K,V> concurrentMap = new ConcurrentHashMap<>();

// 需要有序但不需要线程安全
TreeMap<K,V> orderedMap = new TreeMap<>();

// 需要有序且偶尔需要线程安全
Map<K,V> synchronizedMap = Collections.synchronizedMap(new TreeMap<>());
```

## 最佳实践

### 1. 合理选择比较器

```java
// 自然排序（推荐用于简单类型）
ConcurrentSkipListMap<Integer, String> naturalOrder = new ConcurrentSkipListMap<>();

// 自定义比较器（复杂对象）
ConcurrentSkipListMap<Student, String> customOrder = new ConcurrentSkipListMap<>(
    Comparator.comparing(Student::getScore).reversed()
              .thenComparing(Student::getId)
);

// 避免比较器抛出异常
Comparator<String> safeComparator = (s1, s2) -> {
    if (s1 == null && s2 == null) return 0;
    if (s1 == null) return -1;
    if (s2 == null) return 1;
    return s1.compareTo(s2);
};
```

### 2. 高效的范围查询

```java
// 使用 subMap 进行范围查询
NavigableMap<Integer, String> range = map.subMap(startKey, true, endKey, true);

// 避免在大范围上进行频繁的 size() 调用
int count = 0;
for (Map.Entry<Integer, String> entry : range.entrySet()) {
    // 处理条目
    count++;
    if (count > MAX_PROCESS_COUNT) break;
}

// 使用流式处理大范围查询
range.entrySet().stream()
     .limit(1000)
     .forEach(this::processEntry);
```

### 3. 并发访问优化

```java
// 读多写少场景：直接使用
String value = map.get(key);

// 写操作：使用 CAS 友好的方法
map.compute(key, (k, v) -> v == null ? newValue : updateValue(v));
map.putIfAbsent(key, defaultValue);

// 批量操作：考虑使用 putAll
Map<Integer, String> batch = new HashMap<>();
// ... 填充 batch
map.putAll(batch);
```

### 4. 内存使用优化

```java
// 预估容量，避免频繁扩容（虽然跳表不像哈希表那样扩容）
// 但可以通过合理的数据分布来优化性能

// 定期清理不需要的数据
map.entrySet().removeIf(entry -> isExpired(entry.getValue()));

// 使用弱引用或软引用作为值（如果适用）
ConcurrentSkipListMap<String, WeakReference<LargeObject>> cacheMap = 
    new ConcurrentSkipListMap<>();
```

## 常见陷阱与注意事项

### 1. 比较器一致性

```java
// 错误：比较器不一致
class InconsistentComparator implements Comparator<Integer> {
    private Random random = new Random();
    
    @Override
    public int compare(Integer o1, Integer o2) {
        // 错误：非确定性比较
        return random.nextBoolean() ? o1.compareTo(o2) : o2.compareTo(o1);
    }
}

// 正确：一致的比较器
Comparator<Student> consistentComparator = 
    Comparator.comparing(Student::getScore)
              .thenComparing(Student::getId);
```

### 2. null 值处理

```java
// ConcurrentSkipListMap 不允许 null 键
try {
    map.put(null, "value"); // 抛出 NullPointerException
} catch (NullPointerException e) {
    // 处理异常
}

// 但允许 null 值
map.put(1, null); // 正常

// 检查 null 值
if (map.containsKey(key)) {
    String value = map.get(key); // 可能为 null
    if (value != null) {
        // 处理非 null 值
    }
}
```

### 3. 迭代器使用

```java
// ConcurrentSkipListMap 的迭代器是弱一致性的
for (Map.Entry<Integer, String> entry : map.entrySet()) {
    // 迭代过程中的并发修改不会抛出 ConcurrentModificationException
    // 但可能看不到最新的修改
    processEntry(entry);
}

// 如果需要强一致性，使用快照
Map<Integer, String> snapshot = new TreeMap<>(map);
for (Map.Entry<Integer, String> entry : snapshot.entrySet()) {
    processEntry(entry);
}
```

### 4. 性能考虑

```java
// 避免频繁的边界查询
// 错误：每次都查询边界
for (int i = 0; i < 1000; i++) {
    Integer firstKey = map.firstKey(); // 每次都查询
    // 处理逻辑
}

// 正确：缓存边界值
Integer firstKey = map.firstKey();
for (int i = 0; i < 1000; i++) {
    // 使用缓存的值
    // 处理逻辑
}

// 大范围查询时考虑分页
Integer currentKey = startKey;
while (currentKey != null && currentKey.compareTo(endKey) <= 0) {
    NavigableMap<Integer, String> page = map.subMap(
        currentKey, true, 
        Math.min(currentKey + PAGE_SIZE, endKey), true
    );
    
    processPage(page);
    
    currentKey = map.higherKey(currentKey + PAGE_SIZE);
}
```

## 示例代码位置

- **演示类**：`src/main/java/com/trae/study/collections/concurrent/ConcurrentSkipListMapDemo.java`
- **单元测试**：`src/test/java/com/trae/study/collections/concurrent/ConcurrentSkipListMapDemoTest.java`
- **性能基准**：包含在演示类的 `benchmarkConcurrentSkipListMap()` 方法中

## 总结

`ConcurrentSkipListMap` 是一个功能强大的并发有序 Map 实现，适用于需要线程安全且要求有序访问的场景。其基于跳表的设计提供了良好的性能特性和并发能力，但相比普通的哈希表实现会有一定的内存开销。在选择使用时，需要根据具体的业务需求权衡有序性、并发性和性能之间的关系。