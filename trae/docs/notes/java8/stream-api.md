# Java 8 Stream API 深度解析

## 概述

Stream API 是 Java 8 引入的最重要特性之一，它提供了一种声明式的数据处理方式，让集合操作更加简洁、可读和高效。Stream 不是数据结构，而是数据源的视图，支持函数式编程风格的操作。

## 核心概念

### 1. Stream 的特性

- **不存储数据**：Stream 不是数据结构，不存储元素，只是数据源的视图
- **函数式编程**：操作不会修改原始数据源
- **惰性求值**：中间操作是惰性的，只有遇到终止操作才会执行
- **可能无限**：Stream 可以是无限的，配合 limit() 等操作使用
- **一次性消费**：Stream 只能被消费一次，消费后不能重复使用

### 2. Stream 操作分类

```
Stream 操作
├── 中间操作 (Intermediate Operations)
│   ├── 无状态操作：filter, map, flatMap, peek
│   └── 有状态操作：distinct, sorted, limit, skip
└── 终止操作 (Terminal Operations)
    ├── 非短路操作：forEach, collect, reduce, count
    └── 短路操作：anyMatch, allMatch, noneMatch, findFirst, findAny
```

## Stream 创建方式

### 1. 从集合创建
```java
List<String> list = Arrays.asList("a", "b", "c");
Stream<String> stream = list.stream();           // 顺序流
Stream<String> parallelStream = list.parallelStream(); // 并行流
```

### 2. 从数组创建
```java
String[] array = {"x", "y", "z"};
Stream<String> stream = Arrays.stream(array);
```

### 3. 使用 Stream.of()
```java
Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5);
```

### 4. 无限流
```java
// 生成无限流
Stream<String> generateStream = Stream.generate(() -> "Hello");

// 迭代无限流
Stream<Integer> iterateStream = Stream.iterate(0, n -> n + 2);
```

### 5. 数值流
```java
IntStream intStream = IntStream.range(1, 10);      // 1-9
IntStream intStreamClosed = IntStream.rangeClosed(1, 10); // 1-10
LongStream longStream = LongStream.of(1L, 2L, 3L);
DoubleStream doubleStream = DoubleStream.of(1.0, 2.0, 3.0);
```

## 中间操作详解

### 1. 过滤操作

#### filter(Predicate<T> predicate)
根据条件过滤元素
```java
List<Integer> evenNumbers = numbers.stream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());
```

#### distinct()
去除重复元素（基于 equals() 方法）
```java
List<Integer> uniqueNumbers = numbers.stream()
    .distinct()
    .collect(Collectors.toList());
```

### 2. 映射操作

#### map(Function<T, R> mapper)
将元素转换为另一种类型
```java
List<String> upperCaseNames = names.stream()
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

#### flatMap(Function<T, Stream<R>> mapper)
将嵌套结构扁平化
```java
List<String> allWords = sentences.stream()
    .flatMap(sentence -> Arrays.stream(sentence.split(" ")))
    .collect(Collectors.toList());
```

#### mapToInt/mapToLong/mapToDouble
转换为原始类型流，避免装箱拆箱
```java
int sum = students.stream()
    .mapToInt(Student::getAge)
    .sum();
```

### 3. 排序操作

#### sorted()
自然排序
```java
List<String> sortedNames = names.stream()
    .sorted()
    .collect(Collectors.toList());
```

#### sorted(Comparator<T> comparator)
自定义排序
```java
List<Student> sortedStudents = students.stream()
    .sorted(Comparator.comparing(Student::getScore).reversed())
    .collect(Collectors.toList());
```

### 4. 限制操作

#### limit(long maxSize)
限制元素数量
```java
List<Integer> firstFive = numbers.stream()
    .limit(5)
    .collect(Collectors.toList());
```

#### skip(long n)
跳过前 n 个元素
```java
List<Integer> afterSkip = numbers.stream()
    .skip(5)
    .collect(Collectors.toList());
```

### 5. 调试操作

#### peek(Consumer<T> action)
对每个元素执行操作，不改变流
```java
List<String> result = names.stream()
    .peek(name -> System.out.println("Processing: " + name))
    .map(String::toUpperCase)
    .collect(Collectors.toList());
```

## 终止操作详解

### 1. 收集操作

#### collect(Collector<T, A, R> collector)
将流元素收集到集合或其他数据结构
```java
// 收集到 List
List<String> list = stream.collect(Collectors.toList());

// 收集到 Set
Set<String> set = stream.collect(Collectors.toSet());

// 收集到 Map
Map<String, Integer> map = students.stream()
    .collect(Collectors.toMap(Student::getName, Student::getAge));
```

### 2. 归约操作

#### reduce(BinaryOperator<T> accumulator)
将流元素组合成单个结果
```java
// 求和
Optional<Integer> sum = numbers.stream()
    .reduce((a, b) -> a + b);

// 带初始值的归约
Integer sum = numbers.stream()
    .reduce(0, (a, b) -> a + b);

// 找最大值
Optional<Integer> max = numbers.stream()
    .reduce(Integer::max);
```

### 3. 查找操作

#### findFirst() / findAny()
查找元素
```java
Optional<String> first = names.stream()
    .filter(name -> name.startsWith("A"))
    .findFirst();

Optional<String> any = names.parallelStream()
    .filter(name -> name.startsWith("A"))
    .findAny();
```

### 4. 匹配操作

#### anyMatch / allMatch / noneMatch
条件匹配
```java
boolean hasAdult = students.stream()
    .anyMatch(student -> student.getAge() >= 18);

boolean allPassed = students.stream()
    .allMatch(student -> student.getScore() >= 60);

boolean noneFailure = students.stream()
    .noneMatch(student -> student.getScore() < 60);
```

### 5. 统计操作

#### count() / min() / max()
基本统计
```java
long count = students.stream()
    .filter(student -> student.getAge() >= 18)
    .count();

Optional<Student> youngest = students.stream()
    .min(Comparator.comparing(Student::getAge));

Optional<Student> topStudent = students.stream()
    .max(Comparator.comparing(Student::getScore));
```

## 高级 Collectors

### 1. 分组操作

#### groupingBy
按条件分组
```java
// 按专业分组
Map<String, List<Student>> studentsByMajor = students.stream()
    .collect(Collectors.groupingBy(Student::getMajor));

// 按专业分组并统计人数
Map<String, Long> countByMajor = students.stream()
    .collect(Collectors.groupingBy(
        Student::getMajor,
        Collectors.counting()
    ));

// 按专业分组并计算平均分
Map<String, Double> avgScoreByMajor = students.stream()
    .collect(Collectors.groupingBy(
        Student::getMajor,
        Collectors.averagingDouble(Student::getScore)
    ));
```

### 2. 分区操作

#### partitioningBy
按布尔条件分区
```java
Map<Boolean, List<Student>> partitioned = students.stream()
    .collect(Collectors.partitioningBy(student -> student.getScore() >= 85));

List<Student> highScoreStudents = partitioned.get(true);
List<Student> regularStudents = partitioned.get(false);
```

### 3. 字符串操作

#### joining
字符串连接
```java
String allNames = students.stream()
    .map(Student::getName)
    .collect(Collectors.joining(", "));

String namesWithBrackets = students.stream()
    .map(Student::getName)
    .collect(Collectors.joining(", ", "[", "]"));
```

### 4. 统计信息

#### summarizingInt/Double/Long
获取统计摘要
```java
DoubleSummaryStatistics scoreStats = students.stream()
    .collect(Collectors.summarizingDouble(Student::getScore));

System.out.println("Count: " + scoreStats.getCount());
System.out.println("Sum: " + scoreStats.getSum());
System.out.println("Average: " + scoreStats.getAverage());
System.out.println("Min: " + scoreStats.getMin());
System.out.println("Max: " + scoreStats.getMax());
```

## 并行流 (Parallel Streams)

### 1. 创建并行流
```java
// 从集合创建
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
Stream<Integer> parallelStream = numbers.parallelStream();

// 从顺序流转换
Stream<Integer> parallelStream2 = numbers.stream().parallel();
```

### 2. 并行流的优势与注意事项

**优势：**
- 自动利用多核 CPU
- 适合 CPU 密集型操作
- 大数据集处理性能提升明显

**注意事项：**
- 线程安全问题
- 创建线程的开销
- 不适合 I/O 密集型操作
- 元素顺序可能改变

```java
// 线程安全的收集
List<Integer> safeResult = numbers.parallelStream()
    .filter(n -> n % 2 == 0)
    .collect(Collectors.toList());

// 避免共享可变状态
List<Integer> unsafeList = new ArrayList<>(); // 不安全
numbers.parallelStream()
    .filter(n -> n % 2 == 0)
    .forEach(unsafeList::add); // 可能导致数据丢失
```

### 3. 性能考虑

```java
// 适合并行的场景
long sum = IntStream.range(1, 1_000_000)
    .parallel()
    .filter(n -> n % 2 == 0)
    .mapToLong(n -> n * n)
    .sum();

// 不适合并行的场景（数据量小）
List<Integer> small = Arrays.asList(1, 2, 3, 4, 5);
int smallSum = small.stream() // 顺序流更好
    .mapToInt(Integer::intValue)
    .sum();
```

## 性能优化建议

### 1. 选择合适的数据结构
```java
// ArrayList 适合随机访问
List<Integer> arrayList = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));

// LinkedList 适合频繁插入删除
List<Integer> linkedList = new LinkedList<>(Arrays.asList(1, 2, 3, 4, 5));
```

### 2. 使用原始类型流
```java
// 避免装箱拆箱
int sum = numbers.stream()
    .mapToInt(Integer::intValue)
    .sum();

// 而不是
int sum2 = numbers.stream()
    .reduce(0, Integer::sum); // 涉及装箱拆箱
```

### 3. 合理使用并行流
```java
// 数据量大且 CPU 密集型操作适合并行
long result = IntStream.range(1, 10_000_000)
    .parallel()
    .filter(n -> isPrime(n))
    .count();

// 简单操作或小数据量使用顺序流
List<String> names = students.stream()
    .map(Student::getName)
    .collect(Collectors.toList());
```

### 4. 避免不必要的中间操作
```java
// 好的做法
List<String> result = students.stream()
    .filter(s -> s.getScore() >= 85)
    .map(Student::getName)
    .collect(Collectors.toList());

// 避免多次遍历
List<Student> highScoreStudents = students.stream()
    .filter(s -> s.getScore() >= 85)
    .collect(Collectors.toList());
List<String> names = highScoreStudents.stream()
    .map(Student::getName)
    .collect(Collectors.toList());
```

## 常见陷阱与最佳实践

### 1. Stream 只能使用一次
```java
Stream<String> stream = names.stream();
long count = stream.count(); // 第一次使用
// stream.forEach(System.out::println); // 错误！Stream 已被消费

// 正确做法：重新创建 Stream
names.stream().forEach(System.out::println);
```

### 2. 惰性求值的理解
```java
// 中间操作不会立即执行
Stream<String> stream = names.stream()
    .filter(name -> {
        System.out.println("Filtering: " + name);
        return name.startsWith("A");
    })
    .map(name -> {
        System.out.println("Mapping: " + name);
        return name.toUpperCase();
    });

// 只有遇到终止操作才会执行
List<String> result = stream.collect(Collectors.toList());
```

### 3. 并行流的线程安全
```java
// 错误：共享可变状态
List<Integer> list = new ArrayList<>();
numbers.parallelStream()
    .forEach(list::add); // 线程不安全

// 正确：使用线程安全的收集器
List<Integer> safeList = numbers.parallelStream()
    .collect(Collectors.toList());

// 或使用同步集合
List<Integer> syncList = Collections.synchronizedList(new ArrayList<>());
numbers.parallelStream()
    .forEach(syncList::add);
```

### 4. 性能测试
```java
// 始终进行性能测试来验证并行流的效果
public void benchmarkStreamPerformance() {
    List<Integer> data = IntStream.range(1, 1_000_000)
        .boxed()
        .collect(Collectors.toList());
    
    // 顺序流
    long start = System.currentTimeMillis();
    long sequentialSum = data.stream()
        .filter(n -> n % 2 == 0)
        .mapToLong(n -> n * n)
        .sum();
    long sequentialTime = System.currentTimeMillis() - start;
    
    // 并行流
    start = System.currentTimeMillis();
    long parallelSum = data.parallelStream()
        .filter(n -> n % 2 == 0)
        .mapToLong(n -> n * n)
        .sum();
    long parallelTime = System.currentTimeMillis() - start;
    
    System.out.println("Sequential: " + sequentialTime + "ms");
    System.out.println("Parallel: " + parallelTime + "ms");
    System.out.println("Speedup: " + (double)sequentialTime / parallelTime);
}
```

## 与传统循环的对比

### 1. 可读性对比
```java
// 传统方式
List<String> result = new ArrayList<>();
for (Student student : students) {
    if (student.getScore() >= 85) {
        result.add(student.getName().toUpperCase());
    }
}

// Stream 方式
List<String> result = students.stream()
    .filter(student -> student.getScore() >= 85)
    .map(student -> student.getName().toUpperCase())
    .collect(Collectors.toList());
```

### 2. 性能对比
- **小数据集**：传统循环通常更快
- **大数据集**：Stream（特别是并行流）可能更快
- **复杂操作**：Stream 的函数式组合更优雅

### 3. 调试对比
```java
// Stream 调试
List<String> result = students.stream()
    .peek(s -> System.out.println("Original: " + s))
    .filter(student -> student.getScore() >= 85)
    .peek(s -> System.out.println("After filter: " + s))
    .map(student -> student.getName().toUpperCase())
    .peek(name -> System.out.println("After map: " + name))
    .collect(Collectors.toList());
```

## 示例代码位置

- **主要演示类**：`src/main/java/com/trae/study/java8/StreamApiDemo.java`
- **单元测试**：`src/test/java/com/trae/study/java8/StreamApiDemoTest.java`
- **性能基准测试**：包含在 `StreamApiDemo.benchmarkStreamPerformance()` 方法中

## 总结

Stream API 是 Java 8 的核心特性，它：

1. **提升代码可读性**：声明式编程风格更直观
2. **支持函数式编程**：不可变性、无副作用
3. **内置并行支持**：轻松利用多核优势
4. **丰富的操作符**：filter、map、reduce 等满足各种需求
5. **惰性求值**：提高性能，支持无限流

掌握 Stream API 不仅能写出更优雅的代码，还能在处理大数据集时获得更好的性能。但要注意合理使用，在简单场景下传统循环可能更直接高效。