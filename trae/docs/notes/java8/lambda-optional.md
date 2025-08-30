# Java 8 核心特性：Lambda、Optional 深度解析

## 概述

Java 8 是 Java 历史上最重要的版本之一，引入了函数式编程特性，彻底改变了 Java 的编程范式。本文档深入分析 Lambda 表达式、Stream API 和 Optional 的核心机制和最佳实践。

## Lambda 表达式

### 核心概念

Lambda 表达式是 Java 8 引入的函数式编程特性，允许将函数作为参数传递，简化代码编写。

#### 语法结构
```java
// 基本语法：(参数列表) -> { 方法体 }
(String s) -> s.length()           // 单参数，单表达式
(x, y) -> x + y                    // 多参数，单表达式
() -> System.out.println("Hello")  // 无参数
(String s) -> {                    // 多语句
    System.out.println(s);
    return s.length();
}
```

#### 类型推断
- **参数类型推断**：编译器可以根据上下文推断参数类型
- **目标类型推断**：根据赋值上下文确定 Lambda 的目标类型
- **返回类型推断**：根据方法体自动推断返回类型

### 函数式接口

#### 内置函数式接口

| 接口 | 方法签名 | 用途 | 示例 |
|------|----------|------|------|
| `Function<T,R>` | `R apply(T t)` | 转换函数 | `String::length` |
| `Predicate<T>` | `boolean test(T t)` | 条件判断 | `s -> s.isEmpty()` |
| `Consumer<T>` | `void accept(T t)` | 消费操作 | `System.out::println` |
| `Supplier<T>` | `T get()` | 供应商 | `() -> new ArrayList<>()` |
| `UnaryOperator<T>` | `T apply(T t)` | 一元操作 | `String::toUpperCase` |
| `BinaryOperator<T>` | `T apply(T t1, T t2)` | 二元操作 | `Integer::sum` |

#### 函数式接口组合
```java
// Function 组合
Function<String, String> trim = String::trim;
Function<String, String> upper = String::toUpperCase;
Function<String, String> trimAndUpper = trim.andThen(upper);

// Predicate 组合
Predicate<String> notEmpty = s -> !s.isEmpty();
Predicate<String> notBlank = s -> !s.trim().isEmpty();
Predicate<String> valid = notEmpty.and(notBlank);
```

### 方法引用

#### 四种类型
1. **静态方法引用**：`ClassName::staticMethod`
2. **实例方法引用**：`instance::instanceMethod`
3. **类实例方法引用**：`ClassName::instanceMethod`
4. **构造器引用**：`ClassName::new`

#### 使用场景
```java
// 静态方法引用
list.stream().map(Integer::parseInt);

// 实例方法引用
StringBuilder sb = new StringBuilder();
list.forEach(sb::append);

// 类实例方法引用
list.stream().map(String::toUpperCase);

// 构造器引用
list.stream().collect(toCollection(ArrayList::new));
```

### 变量捕获

#### 有效 final 规则
- Lambda 表达式只能访问 **有效 final** 的局部变量
- 实例变量和静态变量可以自由访问和修改
- 捕获的变量在 Lambda 创建后不能被修改

```java
int count = 0; // 有效 final
list.forEach(item -> {
    System.out.println(count + ": " + item); // 可以访问
    // count++; // 编译错误：不能修改
});
```

## Stream API

### 核心特性

#### 1. 不存储数据
- Stream 不是数据结构，不存储元素
- 只是数据源的视图，支持函数式操作

#### 2. 函数式编程
- 操作不会修改原始数据源
- 支持链式调用，代码简洁

#### 3. 惰性求值
- 中间操作是惰性的，只有遇到终止操作才执行
- 支持短路操作，提高性能

#### 4. 一次性消费
- Stream 只能被消费一次
- 消费后需要重新创建

### 操作分类

#### 中间操作（Intermediate Operations）

| 操作 | 说明 | 示例 |
|------|------|------|
| `filter()` | 过滤元素 | `stream.filter(x -> x > 0)` |
| `map()` | 元素转换 | `stream.map(String::toUpperCase)` |
| `flatMap()` | 扁平化映射 | `stream.flatMap(Collection::stream)` |
| `distinct()` | 去重 | `stream.distinct()` |
| `sorted()` | 排序 | `stream.sorted(Comparator.comparing(User::getAge))` |
| `limit()` | 限制数量 | `stream.limit(10)` |
| `skip()` | 跳过元素 | `stream.skip(5)` |
| `peek()` | 调试查看 | `stream.peek(System.out::println)` |

#### 终止操作（Terminal Operations）

| 操作 | 说明 | 示例 |
|------|------|------|
| `forEach()` | 遍历 | `stream.forEach(System.out::println)` |
| `collect()` | 收集结果 | `stream.collect(toList())` |
| `reduce()` | 归约 | `stream.reduce(0, Integer::sum)` |
| `count()` | 计数 | `stream.count()` |
| `anyMatch()` | 任意匹配 | `stream.anyMatch(x -> x > 0)` |
| `allMatch()` | 全部匹配 | `stream.allMatch(x -> x > 0)` |
| `noneMatch()` | 无匹配 | `stream.noneMatch(x -> x < 0)` |
| `findFirst()` | 查找第一个 | `stream.findFirst()` |
| `findAny()` | 查找任意一个 | `stream.findAny()` |
| `min()/max()` | 最值 | `stream.max(Comparator.naturalOrder())` |

### 高级 Collectors

#### 分组操作
```java
// 按年龄分组
Map<Integer, List<User>> byAge = users.stream()
    .collect(groupingBy(User::getAge));

// 多级分组
Map<String, Map<Integer, List<User>>> byDeptAndAge = users.stream()
    .collect(groupingBy(User::getDepartment, 
                       groupingBy(User::getAge)));

// 分组后统计
Map<String, Long> countByDept = users.stream()
    .collect(groupingBy(User::getDepartment, counting()));
```

#### 分区操作
```java
// 按条件分区
Map<Boolean, List<User>> partitioned = users.stream()
    .collect(partitioningBy(user -> user.getAge() >= 18));
```

#### 字符串连接
```java
// 简单连接
String names = users.stream()
    .map(User::getName)
    .collect(joining(", "));

// 带前缀后缀
String formatted = users.stream()
    .map(User::getName)
    .collect(joining(", ", "[", "]"));
```

### 并行流

#### 使用场景
- **CPU 密集型任务**：计算量大的操作
- **大数据集**：元素数量足够多（通常 > 1000）
- **无状态操作**：操作之间无依赖关系

#### 注意事项
```java
// 线程安全问题
List<String> result = new ArrayList<>(); // 非线程安全
list.parallelStream().forEach(result::add); // 可能出现问题

// 正确做法
List<String> result = list.parallelStream()
    .collect(toList()); // 使用 collect
```

#### 性能考虑
- **Fork/Join 框架开销**：小数据集可能比串行慢
- **数据源特性**：ArrayList 比 LinkedList 更适合并行
- **操作复杂度**：简单操作并行化收益有限

## Optional

### 设计目标

#### 解决 NullPointerException
- 明确表示值可能不存在
- 编译时强制处理空值情况
- 提供函数式的空值处理方式

#### 不是银弹
- 不能完全消除 NPE
- 有一定的性能开销
- 不适用于所有场景

### 创建方式

```java
// 空 Optional
Optional<String> empty = Optional.empty();

// 非空值（不能为 null）
Optional<String> nonNull = Optional.of("Hello");

// 可能为空的值
Optional<String> nullable = Optional.ofNullable(getValue());
```

### 核心操作

#### 检查和获取
```java
// 检查是否有值
if (optional.isPresent()) {
    String value = optional.get(); // 不推荐直接使用 get()
}

// Java 11+ 支持
if (optional.isEmpty()) {
    // 处理空值情况
}
```

#### 安全获取
```java
// 提供默认值
String value = optional.orElse("default");

// 懒加载默认值
String value = optional.orElseGet(() -> computeDefault());

// 抛出异常
String value = optional.orElseThrow(() -> new IllegalStateException("No value"));
```

#### 函数式操作
```java
// 转换
Optional<Integer> length = optional.map(String::length);

// 扁平化映射
Optional<String> result = user.flatMap(User::getAddress)
                             .map(Address::getCity);

// 过滤
Optional<String> longString = optional.filter(s -> s.length() > 5);

// 条件执行
optional.ifPresent(System.out::println);

// Java 9+ 支持
optional.ifPresentOrElse(
    System.out::println,
    () -> System.out.println("No value")
);
```

### 最佳实践

#### ✅ 推荐做法

1. **方法返回值使用 Optional**
```java
public Optional<User> findUserById(Long id) {
    return userRepository.findById(id);
}
```

2. **链式调用处理复杂逻辑**
```java
String result = findUser(id)
    .filter(user -> user.isActive())
    .map(User::getName)
    .map(String::toUpperCase)
    .orElse("UNKNOWN");
```

3. **使用 orElseGet 而非 orElse**
```java
// 推荐：懒加载
String value = optional.orElseGet(() -> computeExpensiveDefault());

// 不推荐：总是执行
String value = optional.orElse(computeExpensiveDefault());
```

#### ❌ 避免的做法

1. **不要用于字段和参数**
```java
// 错误
public class User {
    private Optional<String> name; // 不要这样做
}

// 错误
public void process(Optional<String> value) { // 不要这样做
}
```

2. **避免直接使用 get()**
```java
// 危险
String value = optional.get(); // 可能抛出 NoSuchElementException

// 安全
String value = optional.orElse("default");
```

3. **不要用于集合**
```java
// 错误
Optional<List<String>> optionalList = Optional.of(Arrays.asList());

// 正确
List<String> list = Arrays.asList(); // 空集合比 Optional 更合适
```

### 性能考虑

#### 对象创建开销
- Optional 是对象，有创建和 GC 开销
- 频繁创建可能影响性能
- 在性能敏感的代码中谨慎使用

#### 内存占用
- Optional 包装增加内存占用
- 对于基本类型，考虑使用 OptionalInt/OptionalLong/OptionalDouble

#### 基准测试结果
```java
// 典型性能对比（仅供参考）
// 传统 null 检查：    100% 基准
// Optional 基本操作： 150-200% 开销
// Optional 函数式：   200-300% 开销
```

## 与传统编程的对比

### 代码简洁性

#### 传统方式
```java
// 传统的集合处理
List<String> result = new ArrayList<>();
for (User user : users) {
    if (user.getAge() >= 18) {
        String name = user.getName();
        if (name != null && !name.trim().isEmpty()) {
            result.add(name.toUpperCase());
        }
    }
}
```

#### Java 8 方式
```java
// 函数式处理
List<String> result = users.stream()
    .filter(user -> user.getAge() >= 18)
    .map(User::getName)
    .filter(Objects::nonNull)
    .map(String::trim)
    .filter(name -> !name.isEmpty())
    .map(String::toUpperCase)
    .collect(toList());
```

### 可读性和维护性

#### 优势
- **声明式编程**：关注做什么，而非怎么做
- **链式调用**：逻辑流程清晰
- **函数复用**：方法引用提高复用性
- **类型安全**：编译时检查，减少运行时错误

#### 挑战
- **学习曲线**：需要理解函数式编程概念
- **调试困难**：链式调用的调试相对复杂
- **性能理解**：需要了解惰性求值和并行流的特性

## 常见陷阱与注意事项

### Lambda 表达式陷阱

1. **变量捕获限制**
```java
int count = 0;
list.forEach(item -> {
    // count++; // 编译错误：变量必须是有效 final
});
```

2. **方法引用的上下文**
```java
// 注意方法引用的上下文
list.stream().map(this::processItem); // 捕获 this 引用
```

### Stream 陷阱

1. **重复消费**
```java
Stream<String> stream = list.stream();
stream.forEach(System.out::println);
// stream.count(); // 运行时错误：stream 已被消费
```

2. **并行流的副作用**
```java
// 错误：非线程安全的操作
List<String> result = new ArrayList<>();
list.parallelStream().forEach(result::add);
```

3. **无限流的处理**
```java
// 必须使用 limit() 限制无限流
Stream.iterate(0, i -> i + 1)
    .limit(100) // 必须限制
    .forEach(System.out::println);
```

### Optional 陷阱

1. **过度使用**
```java
// 不必要的 Optional
Optional<String> optional = Optional.of("Hello");
if (optional.isPresent()) {
    System.out.println(optional.get()); // 直接使用字符串更简单
}
```

2. **嵌套 Optional**
```java
// 错误的嵌套
Optional<Optional<String>> nested = Optional.of(Optional.of("Hello"));

// 正确使用 flatMap
Optional<String> result = user.flatMap(User::getAddress)
                             .map(Address::getCity);
```

## 总结

Java 8 的函数式编程特性为 Java 带来了革命性的变化：

### 核心价值
1. **提高代码质量**：更简洁、可读、可维护
2. **减少样板代码**：函数式操作替代冗长的循环
3. **增强表达能力**：声明式编程更接近业务逻辑
4. **改善错误处理**：Optional 提供更安全的空值处理

### 学习建议
1. **循序渐进**：从简单的 Lambda 开始，逐步掌握 Stream 和 Optional
2. **实践为主**：在实际项目中应用，积累经验
3. **性能意识**：了解函数式编程的性能特性
4. **最佳实践**：遵循社区推荐的编程规范

### 相关文件
- 示例代码：`src/main/java/com/trae/study/java8/`
  - `LambdaDemo.java` - Lambda 表达式演示
  - `StreamApiDemo.java` - Stream API 演示
  - `OptionalDemo.java` - Optional 演示
- 单元测试：`src/test/java/com/trae/study/java8/`
  - `LambdaDemoTest.java` - Lambda 测试
  - `StreamApiDemoTest.java` - Stream API 测试
  - `OptionalDemoTest.java` - Optional 测试
- 相关笔记：
  - `stream-api.md` - Stream API 详细解析

Java 8 的这些特性不仅改变了编程方式，更重要的是改变了思维方式，从命令式编程向函数式编程的转变，让 Java 开发者能够编写更优雅、更高效的代码。