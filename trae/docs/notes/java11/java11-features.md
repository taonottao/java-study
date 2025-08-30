# Java 11 新特性深度解析

> Java 11 作为 LTS（长期支持）版本，引入了多项实用的新特性，本文档深入分析这些特性的使用方法、性能特征和最佳实践。

## 目录

- [HttpClient API](#httpclient-api)
- [String 新方法](#string-新方法)
- [Files 便捷 IO](#files-便捷-io)
- [Optional.isEmpty()](#optionalisempty)
- [var in Lambda 参数](#var-in-lambda-参数)
- [单文件源码启动](#单文件源码启动)
- [性能分析与对比](#性能分析与对比)
- [最佳实践](#最佳实践)
- [常见陷阱](#常见陷阱)

## HttpClient API

### 核心特性

Java 11 正式引入了标准的 HttpClient API，替代了第三方库如 Apache HttpClient。

#### 主要优势

- **原生支持 HTTP/2**：默认使用 HTTP/2 协议
- **异步编程**：支持 CompletableFuture 异步操作
- **WebSocket 支持**：内置 WebSocket 客户端
- **响应式流**：支持 Flow API
- **连接池管理**：自动管理连接池

#### 基本用法

```java
// 创建 HttpClient
HttpClient client = HttpClient.newBuilder()
    .version(HttpClient.Version.HTTP_2)
    .connectTimeout(Duration.ofSeconds(10))
    .build();

// 创建请求
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/data"))
    .timeout(Duration.ofSeconds(30))
    .header("User-Agent", "Java11-HttpClient")
    .GET()
    .build();

// 同步发送
HttpResponse<String> response = client.send(request, 
    HttpResponse.BodyHandlers.ofString());

// 异步发送
CompletableFuture<HttpResponse<String>> futureResponse = 
    client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
```

#### 高级特性

1. **自定义 BodyHandler**
```java
// 处理 JSON 响应
HttpResponse<JsonNode> jsonResponse = client.send(request,
    HttpResponse.BodyHandlers.ofString()
        .map(JsonParser::parse));
```

2. **文件上传下载**
```java
// 文件下载
HttpResponse<Path> fileResponse = client.send(request,
    HttpResponse.BodyHandlers.ofFile(Paths.get("download.txt")));

// 文件上传
HttpRequest uploadRequest = HttpRequest.newBuilder()
    .uri(URI.create("https://api.example.com/upload"))
    .POST(HttpRequest.BodyPublishers.ofFile(Paths.get("upload.txt")))
    .build();
```

## String 新方法

### isBlank() 方法

判断字符串是否为空或只包含空白字符（包括 Unicode 空白字符）。

```java
// 与 isEmpty() 的区别
"".isEmpty()     // true
"".isBlank()     // true
"   ".isEmpty()  // false
"   ".isBlank()  // true
"\u2000".isBlank() // true (Unicode 空白字符)
```

#### 使用场景

- **表单验证**：检查用户输入是否有效
- **配置解析**：验证配置项是否为空
- **数据清洗**：过滤无效数据

### strip 系列方法

相比 `trim()` 方法，`strip` 系列方法能正确处理 Unicode 空白字符。

```java
String text = "\u2000  Hello World  \u2001";

// trim() 只能处理 ASCII 空白字符
text.trim()         // "\u2000  Hello World  \u2001"

// strip() 处理所有 Unicode 空白字符
text.strip()        // "Hello World"
text.stripLeading() // "Hello World  \u2001"
text.stripTrailing()// "\u2000  Hello World"
```

#### 性能对比

| 方法 | ASCII 空白 | Unicode 空白 | 性能 |
|------|------------|--------------|------|
| trim() | ✅ | ❌ | 更快 |
| strip() | ✅ | ✅ | 稍慢 |

### lines() 方法

将多行字符串转换为 Stream<String>，自动处理不同的换行符。

```java
String multiline = "第一行\n第二行\r\n第三行\n\n第五行";

// 获取所有行
List<String> lines = multiline.lines()
    .collect(Collectors.toList());

// 过滤非空行
List<String> nonEmptyLines = multiline.lines()
    .filter(line -> !line.isBlank())
    .collect(Collectors.toList());

// 统计行数
long lineCount = multiline.lines().count();
```

#### 优势

- **自动换行符处理**：支持 `\n`、`\r\n`、`\r`
- **流式处理**：可与 Stream API 结合
- **内存友好**：惰性求值，适合大文件

### repeat() 方法

重复字符串指定次数，比手动循环更高效。

```java
// 基本用法
"*".repeat(5)        // "*****"
"abc".repeat(3)      // "abcabcabc"

// 实际应用
String separator = "-".repeat(50);  // 分隔线
String padding = " ".repeat(indent); // 缩进
String pattern = "01".repeat(8);     // 模式生成
```

#### 性能特征

- **内部优化**：使用 `System.arraycopy()` 实现
- **内存效率**：一次性分配目标大小的数组
- **边界检查**：自动处理溢出情况

## Files 便捷 IO

### 字符串文件读写

Java 11 简化了文件的字符串读写操作。

```java
// 写入字符串到文件
Path file = Paths.get("example.txt");
String content = "Java 11 新特性\n包含中文字符";
Files.writeString(file, content);

// 指定字符编码
Files.writeString(file, content, StandardCharsets.UTF_8);

// 读取文件为字符串
String readContent = Files.readString(file);
String readWithEncoding = Files.readString(file, StandardCharsets.UTF_8);
```

### 与传统方式对比

#### 传统方式（Java 8 之前）

```java
// 写入文件
try (FileWriter writer = new FileWriter("example.txt")) {
    writer.write(content);
} catch (IOException e) {
    // 异常处理
}

// 读取文件
StringBuilder sb = new StringBuilder();
try (BufferedReader reader = new BufferedReader(
        new FileReader("example.txt"))) {
    String line;
    while ((line = reader.readLine()) != null) {
        sb.append(line).append("\n");
    }
} catch (IOException e) {
    // 异常处理
}
String content = sb.toString();
```

#### Java 11 方式

```java
// 写入文件
Files.writeString(Paths.get("example.txt"), content);

// 读取文件
String content = Files.readString(Paths.get("example.txt"));
```

### 优势分析

| 特性 | 传统方式 | Java 11 方式 |
|------|----------|---------------|
| 代码行数 | 15+ 行 | 1 行 |
| 异常处理 | 手动 try-catch | 自动传播 |
| 资源管理 | 手动 close | 自动管理 |
| 字符编码 | 需要指定 | 默认 UTF-8 |
| 可读性 | 较差 | 极佳 |

### 性能考虑

- **小文件**：性能优异，代码简洁
- **大文件**：一次性加载到内存，需注意内存使用
- **流式处理**：大文件建议使用 `Files.lines()` 或传统流方式

## Optional.isEmpty()

### 语义化改进

Java 11 为 Optional 添加了 `isEmpty()` 方法，提供更直观的语义。

```java
Optional<String> optional = Optional.ofNullable(getValue());

// Java 8 方式
if (!optional.isPresent()) {
    // 处理空值情况
}

// Java 11 方式
if (optional.isEmpty()) {
    // 处理空值情况
}
```

### 在流操作中的应用

```java
List<Optional<String>> optionals = getOptionalList();

// 统计空值数量
long emptyCount = optionals.stream()
    .mapToLong(opt -> opt.isEmpty() ? 1 : 0)
    .sum();

// 过滤非空值
List<String> values = optionals.stream()
    .filter(opt -> !opt.isEmpty())
    .map(Optional::get)
    .collect(Collectors.toList());
```

### 与 isPresent() 的关系

```java
// 等价关系
optional.isEmpty() == !optional.isPresent()
```

## var in Lambda 参数

### 语法增强

Java 11 允许在 lambda 表达式的参数中使用 `var` 关键字。

```java
List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

// Java 8 方式
names.stream()
    .map(name -> name.toUpperCase())
    .collect(Collectors.toList());

// Java 11 方式
names.stream()
    .map((var name) -> name.toUpperCase())
    .collect(Collectors.toList());
```

### 使用场景

#### 1. 添加注解

```java
list.stream()
    .map((@NonNull var item) -> item.toString())
    .collect(Collectors.toList());
```

#### 2. 复杂类型推断

```java
Map<String, List<User>> userGroups = users.stream()
    .collect(Collectors.groupingBy(
        (var user) -> user.getDepartment()
    ));
```

#### 3. 提高可读性

```java
// 明确参数类型
users.stream()
    .filter((var user) -> user.getAge() >= 18)
    .sorted((var u1, var u2) -> u1.getName().compareTo(u2.getName()))
    .collect(Collectors.toList());
```

### 限制条件

- **必须显式声明**：不能省略括号
- **所有参数一致**：要么都用 `var`，要么都不用
- **不能混用**：不能与显式类型混用

```java
// ❌ 错误用法
(var x, String y) -> x + y  // 不能混用
(var x, var y) -> x + y     // ✅ 正确
(String x, String y) -> x + y // ✅ 正确
```

## 单文件源码启动

### 特性概述

Java 11 支持直接运行单个 Java 源文件，无需先编译。

```bash
# 直接运行 Java 源文件
java HelloWorld.java

# 传递参数
java HelloWorld.java arg1 arg2

# 指定类路径
java -cp /path/to/libs HelloWorld.java
```

### Shebang 支持

可以在 Unix 系统上创建可执行的 Java 脚本：

```java
#!/usr/bin/java --source 11

public class QuickScript {
    public static void main(String[] args) {
        System.out.println("Hello from Java script!");
    }
}
```

```bash
# 设置执行权限
chmod +x script.java

# 直接执行
./script.java
```

### 使用场景

- **快速原型**：测试想法和概念
- **脚本编程**：替代 shell 脚本
- **教学演示**：简化学习环境
- **工具开发**：快速开发小工具

### 限制条件

- **单文件限制**：只能包含一个源文件
- **依赖限制**：不能依赖其他自定义类
- **包声明**：文件名必须与公共类名匹配

## 性能分析与对比

### String 方法性能

| 操作 | 传统方式 | Java 11 方式 | 性能提升 |
|------|----------|--------------|----------|
| 空白检查 | `trim().isEmpty()` | `isBlank()` | 10-15% |
| Unicode 处理 | 手动实现 | `strip()` | 显著 |
| 字符串重复 | StringBuilder | `repeat()` | 30-50% |
| 多行处理 | `split("\\r?\\n")` | `lines()` | 20-30% |

### Files IO 性能

```java
// 性能基准测试示例
public void benchmarkFileIO() {
    Path testFile = Paths.get("benchmark.txt");
    String content = "测试内容\n".repeat(10000);
    
    // Java 11 方式
    long start = System.nanoTime();
    Files.writeString(testFile, content);
    String read = Files.readString(testFile);
    long java11Time = System.nanoTime() - start;
    
    // 传统方式
    start = System.nanoTime();
    try (FileWriter writer = new FileWriter(testFile.toFile())) {
        writer.write(content);
    }
    // ... 读取代码
    long traditionalTime = System.nanoTime() - start;
    
    System.out.printf("Java 11: %d ns, 传统: %d ns%n", 
        java11Time, traditionalTime);
}
```

### HttpClient 性能

相比第三方库的优势：

- **内存使用**：减少 30-40%
- **启动时间**：减少依赖加载时间
- **HTTP/2 支持**：原生优化
- **异步性能**：基于 NIO，性能优异

## 最佳实践

### 1. String 方法选择

```java
// ✅ 推荐：根据需求选择合适的方法
if (input.isBlank()) {          // 检查空白
    return defaultValue;
}

String cleaned = input.strip(); // Unicode 安全
String padded = " ".repeat(indent) + content; // 高效重复

// ❌ 避免：不必要的复杂操作
if (input.trim().isEmpty()) {   // 应该用 isBlank()
    return defaultValue;
}
```

### 2. Files IO 使用

```java
// ✅ 推荐：小文件直接读写
if (Files.size(file) < 1024 * 1024) { // 小于 1MB
    String content = Files.readString(file);
    // 处理内容
    Files.writeString(file, processedContent);
}

// ✅ 推荐：大文件流式处理
else {
    try (Stream<String> lines = Files.lines(file)) {
        List<String> processed = lines
            .map(this::processLine)
            .collect(Collectors.toList());
        Files.write(file, processed);
    }
}
```

### 3. HttpClient 配置

```java
// ✅ 推荐：合理配置超时和连接池
HttpClient client = HttpClient.newBuilder()
    .version(HttpClient.Version.HTTP_2)
    .connectTimeout(Duration.ofSeconds(10))
    .executor(Executors.newFixedThreadPool(4))
    .build();

// ✅ 推荐：使用异步处理
CompletableFuture<String> future = client
    .sendAsync(request, HttpResponse.BodyHandlers.ofString())
    .thenApply(HttpResponse::body);
```

### 4. Optional.isEmpty() 使用

```java
// ✅ 推荐：语义清晰的条件判断
if (optional.isEmpty()) {
    handleEmptyCase();
} else {
    handlePresentCase(optional.get());
}

// ✅ 推荐：在流操作中使用
long emptyCount = optionals.stream()
    .mapToLong(opt -> opt.isEmpty() ? 1 : 0)
    .sum();
```

## 常见陷阱

### 1. String 方法陷阱

```java
// ❌ 陷阱：混淆 isEmpty() 和 isBlank()
String input = "   ";
if (input.isEmpty()) {     // false，字符串不为空
    // 这里不会执行
}
if (input.isBlank()) {     // true，只包含空白字符
    // 这里会执行
}

// ❌ 陷阱：repeat() 参数过大
String huge = "test".repeat(Integer.MAX_VALUE); // OutOfMemoryError
```

### 2. Files IO 陷阱

```java
// ❌ 陷阱：大文件一次性读取
String content = Files.readString(largeFile); // 可能 OutOfMemoryError

// ✅ 正确：检查文件大小
if (Files.size(file) > MAX_FILE_SIZE) {
    throw new IllegalArgumentException("文件过大");
}
```

### 3. HttpClient 陷阱

```java
// ❌ 陷阱：忘记设置超时
HttpClient client = HttpClient.newHttpClient(); // 可能无限等待

// ❌ 陷阱：同步调用阻塞线程
HttpResponse<String> response = client.send(request, 
    HttpResponse.BodyHandlers.ofString()); // 阻塞当前线程

// ✅ 正确：使用异步调用
CompletableFuture<HttpResponse<String>> future = client.sendAsync(request,
    HttpResponse.BodyHandlers.ofString());
```

### 4. var in Lambda 陷阱

```java
// ❌ 陷阱：不一致的参数声明
(var x, String y) -> x + y; // 编译错误

// ❌ 陷阱：省略括号
var x -> x.toString(); // 编译错误

// ✅ 正确：一致的声明
(var x, var y) -> x + y;
```

## 总结

Java 11 的新特性显著提升了开发效率和代码可读性：

1. **HttpClient**：提供了现代化的 HTTP 客户端解决方案
2. **String 增强**：更好的 Unicode 支持和便捷方法
3. **Files IO**：极大简化了文件操作
4. **Optional.isEmpty()**：提供更直观的语义
5. **var in Lambda**：增强了 lambda 表达式的灵活性
6. **单文件启动**：简化了原型开发和脚本编程

这些特性不仅提高了代码的简洁性，还在性能和安全性方面有所改进。在实际使用中，应该根据具体场景选择合适的特性，避免常见陷阱，充分发挥 Java 11 的优势。

## 相关资源

- **示例代码**：`src/main/java/com/trae/study/java11/Java11FeaturesDemo.java`
- **单元测试**：`src/test/java/com/trae/study/java11/Java11FeaturesDemoTest.java`
- **性能基准**：参见示例代码中的 `benchmarkJava11Features()` 方法
- **官方文档**：[JEP 323](https://openjdk.java.net/jeps/323), [JEP 321](https://openjdk.java.net/jeps/321)