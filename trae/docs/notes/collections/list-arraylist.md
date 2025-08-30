# ArrayList 源码深度解析

## 概述

ArrayList 是 Java 集合框架中最常用的动态数组实现，基于可变长度数组，提供了快速的随机访问能力。本文档深入分析 ArrayList 的源码实现和核心机制。

## 核心特性

### 1. 基本结构

```java
public class ArrayList<E> extends AbstractList<E>
        implements List<E>, RandomAccess, Cloneable, java.io.Serializable {
    
    // 默认初始容量
    private static final int DEFAULT_CAPACITY = 10;
    
    // 存储元素的数组
    transient Object[] elementData;
    
    // 实际元素个数
    private int size;
    
    // 修改次数（用于 fail-fast）
    protected transient int modCount = 0;
}
```

**关键点：**
- `elementData`：实际存储元素的数组，使用 `Object[]` 类型
- `size`：当前列表中的元素个数，不等于数组长度
- `modCount`：继承自 `AbstractList`，用于实现 fail-fast 机制
- 实现了 `RandomAccess` 标记接口，表示支持快速随机访问

### 2. 扩容机制（Growth Mechanism）

#### 扩容触发条件
```java
public boolean add(E e) {
    ensureCapacityInternal(size + 1);  // 确保容量足够
    elementData[size++] = e;
    return true;
}

private void ensureCapacityInternal(int minCapacity) {
    if (elementData == DEFAULTCAPACITY_EMPTY_ELEMENTDATA) {
        minCapacity = Math.max(DEFAULT_CAPACITY, minCapacity);
    }
    ensureExplicitCapacity(minCapacity);
}

private void ensureExplicitCapacity(int minCapacity) {
    modCount++; // 修改计数器递增
    if (minCapacity - elementData.length > 0)
        grow(minCapacity); // 触发扩容
}
```

#### 扩容算法
```java
private void grow(int minCapacity) {
    int oldCapacity = elementData.length;
    int newCapacity = oldCapacity + (oldCapacity >> 1); // 扩容 1.5 倍
    
    if (newCapacity - minCapacity < 0)
        newCapacity = minCapacity;
    if (newCapacity - MAX_ARRAY_SIZE > 0)
        newCapacity = hugeCapacity(minCapacity);
    
    // 复制到新数组
    elementData = Arrays.copyOf(elementData, newCapacity);
}
```

**扩容策略分析：**
1. **扩容因子**：1.5倍（`oldCapacity + (oldCapacity >> 1)`）
2. **最小容量保证**：确保新容量不小于所需的最小容量
3. **最大容量限制**：`Integer.MAX_VALUE - 8`（预留 VM 头部空间）
4. **数组复制**：使用 `Arrays.copyOf()` 进行高效复制

#### 扩容性能影响
- **时间复杂度**：单次扩容 O(n)，但摊还复杂度为 O(1)
- **空间开销**：可能存在 25%-50% 的空间浪费
- **优化建议**：预知大小时使用 `ensureCapacity()` 或指定初始容量

### 3. Fail-Fast 机制

#### 实现原理
```java
public Iterator<E> iterator() {
    return new Itr();
}

private class Itr implements Iterator<E> {
    int expectedModCount = modCount; // 记录创建时的修改次数
    
    public E next() {
        checkForComodification();
        // ... 获取元素逻辑
    }
    
    final void checkForComodification() {
        if (modCount != expectedModCount)
            throw new ConcurrentModificationException();
    }
}
```

**关键机制：**
1. **modCount 计数器**：每次结构性修改（add、remove）都会递增
2. **expectedModCount**：迭代器创建时记录的 modCount 值
3. **并发检测**：每次迭代操作都检查 modCount 是否变化
4. **异常抛出**：检测到并发修改时抛出 `ConcurrentModificationException`

#### 安全的迭代修改
```java
// 错误方式：直接修改集合
for (String item : list) {
    if (condition) {
        list.remove(item); // 抛出 ConcurrentModificationException
    }
}

// 正确方式：使用迭代器
Iterator<String> iterator = list.iterator();
while (iterator.hasNext()) {
    String item = iterator.next();
    if (condition) {
        iterator.remove(); // 安全删除
    }
}
```

### 4. SubList 视图机制

#### 实现原理
```java
public List<E> subList(int fromIndex, int toIndex) {
    subListRangeCheck(fromIndex, toIndex, size);
    return new SubList(this, 0, fromIndex, toIndex);
}

private class SubList extends AbstractList<E> implements RandomAccess {
    private final AbstractList<E> parent;
    private final int parentOffset;
    private final int offset;
    int size;
    
    // SubList 的操作直接作用于父列表的 elementData
    public E set(int index, E e) {
        rangeCheck(index);
        checkForComodification();
        E oldValue = ArrayList.this.elementData(offset + index);
        ArrayList.this.elementData[offset + index] = e;
        return oldValue;
    }
}
```

**视图特性：**
1. **共享存储**：SubList 与原列表共享同一个 `elementData` 数组
2. **索引映射**：SubList 的索引通过 `offset` 映射到原列表
3. **双向影响**：修改 SubList 会影响原列表，反之亦然
4. **结构性修改检测**：SubList 也有自己的 modCount 检测机制

### 5. 性能特性分析

#### 时间复杂度
| 操作 | 平均情况 | 最坏情况 | 说明 |
|------|----------|----------|------|
| get(index) | O(1) | O(1) | 直接数组访问 |
| set(index, element) | O(1) | O(1) | 直接数组赋值 |
| add(element) | O(1) | O(n) | 尾部添加，可能触发扩容 |
| add(index, element) | O(n) | O(n) | 需要移动后续元素 |
| remove(index) | O(n) | O(n) | 需要移动后续元素 |
| contains(element) | O(n) | O(n) | 线性查找 |
| indexOf(element) | O(n) | O(n) | 线性查找 |

#### 空间复杂度
- **存储开销**：O(n) + 额外的容量缓冲
- **扩容浪费**：最多 50% 的空间浪费（刚扩容后）
- **内存局部性**：连续内存布局，缓存友好

## 源码关键方法解析

### 1. 构造方法

```java
// 默认构造（延迟初始化）
public ArrayList() {
    this.elementData = DEFAULTCAPACITY_EMPTY_ELEMENTDATA;
}

// 指定初始容量
public ArrayList(int initialCapacity) {
    if (initialCapacity > 0) {
        this.elementData = new Object[initialCapacity];
    } else if (initialCapacity == 0) {
        this.elementData = EMPTY_ELEMENTDATA;
    } else {
        throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
    }
}

// 从集合构造
public ArrayList(Collection<? extends E> c) {
    elementData = c.toArray();
    if ((size = elementData.length) != 0) {
        if (elementData.getClass() != Object[].class)
            elementData = Arrays.copyOf(elementData, size, Object[].class);
    } else {
        this.elementData = EMPTY_ELEMENTDATA;
    }
}
```

### 2. 添加元素

```java
// 尾部添加
public boolean add(E e) {
    ensureCapacityInternal(size + 1);
    elementData[size++] = e;
    return true;
}

// 指定位置插入
public void add(int index, E element) {
    rangeCheckForAdd(index);
    ensureCapacityInternal(size + 1);
    
    // 移动后续元素
    System.arraycopy(elementData, index, elementData, index + 1, size - index);
    elementData[index] = element;
    size++;
}
```

### 3. 删除元素

```java
// 按索引删除
public E remove(int index) {
    rangeCheck(index);
    modCount++;
    
    E oldValue = elementData(index);
    int numMoved = size - index - 1;
    if (numMoved > 0)
        System.arraycopy(elementData, index+1, elementData, index, numMoved);
    
    elementData[--size] = null; // 清除引用，帮助 GC
    return oldValue;
}

// 按对象删除
public boolean remove(Object o) {
    if (o == null) {
        for (int index = 0; index < size; index++)
            if (elementData[index] == null) {
                fastRemove(index);
                return true;
            }
    } else {
        for (int index = 0; index < size; index++)
            if (o.equals(elementData[index])) {
                fastRemove(index);
                return true;
            }
    }
    return false;
}
```

## 最佳实践

### 1. 容量预估
```java
// 好的做法：预估容量
List<String> list = new ArrayList<>(expectedSize);

// 或者使用 ensureCapacity
List<String> list = new ArrayList<>();
list.ensureCapacity(expectedSize);
```

### 2. 迭代安全
```java
// 使用迭代器进行安全删除
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    if (shouldRemove(it.next())) {
        it.remove();
    }
}

// 或者反向遍历
for (int i = list.size() - 1; i >= 0; i--) {
    if (shouldRemove(list.get(i))) {
        list.remove(i);
    }
}
```

### 3. 性能优化
```java
// 批量操作优于单个操作
list.addAll(otherCollection); // 优于多次 add()

// 使用 removeAll 或 retainAll
list.removeAll(toRemove); // 优于逐个 remove()

// 合理使用 subList
List<String> subList = list.subList(start, end);
subList.clear(); // 高效删除范围内元素
```

## 与其他 List 实现的对比

| 特性 | ArrayList | LinkedList | Vector |
|------|-----------|------------|--------|
| 随机访问 | O(1) | O(n) | O(1) |
| 插入/删除（头部） | O(n) | O(1) | O(n) |
| 插入/删除（尾部） | O(1)* | O(1) | O(1)* |
| 内存开销 | 低 | 高（节点开销） | 低 |
| 线程安全 | 否 | 否 | 是 |
| 扩容策略 | 1.5倍 | 不适用 | 2倍 |

*摊还复杂度

## 常见陷阱与注意事项

### 1. 容量浪费
```java
// 问题：可能浪费大量内存
List<String> list = new ArrayList<>();
for (int i = 0; i < 1000000; i++) {
    list.add("item" + i);
}
// 此时容量可能是 1677721，浪费约 67% 空间

// 解决：及时 trim
list.trimToSize(); // 将容量调整为实际大小
```

### 2. SubList 陷阱
```java
// 危险：SubList 持有原列表引用，可能导致内存泄漏
List<String> hugelist = loadHugeData();
List<String> small = hugelist.subList(0, 10);
// hugelist 无法被 GC，即使只需要 small

// 解决：创建独立副本
List<String> small = new ArrayList<>(hugelist.subList(0, 10));
```

### 3. 并发修改
```java
// 问题：并发修改导致数据不一致
List<String> list = new ArrayList<>();
// 多线程同时修改 list

// 解决方案1：使用同步包装
List<String> syncList = Collections.synchronizedList(new ArrayList<>());

// 解决方案2：使用并发集合
List<String> concurrentList = new CopyOnWriteArrayList<>();

// 解决方案3：外部同步
synchronized(list) {
    // 操作 list
}
```

## 示例代码位置

- **演示代码**：`src/main/java/com/study/collections/list/ArrayListSourceDemo.java`
- **单元测试**：`src/test/java/com/study/collections/list/ArrayListSourceDemoTest.java`
- **性能基准**：`src/test/java/com/study/benchmark/ListAddBenchmarkStopWatchTest.java`

## 总结

ArrayList 是基于动态数组的高效实现，具有以下核心特点：

1. **优势**：
   - 快速随机访问（O(1)）
   - 内存局部性好，缓存友好
   - 尾部插入效率高（摊还 O(1)）
   - 实现简单，使用广泛

2. **劣势**：
   - 中间插入/删除效率低（O(n)）
   - 可能存在内存浪费
   - 非线程安全

3. **适用场景**：
   - 频繁随机访问
   - 主要在尾部添加元素
   - 读多写少的场景
   - 对内存局部性有要求的场景

理解 ArrayList 的内部机制有助于在实际开发中做出正确的选择和优化决策。