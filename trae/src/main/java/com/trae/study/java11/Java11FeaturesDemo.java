package com.trae.study.java11;

import cn.hutool.core.util.StrUtil;
import com.trae.study.dto.BenchmarkResultDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Java 11 新特性演示
 * 展示 Java 11 引入的主要新特性和改进
 * 
 * @author trae
 * @since 2024
 */
public class Java11FeaturesDemo {
    
    /**
     * 用户信息类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        private String name;
        private String email;
        private Integer age;
        private String description;
    }
    
    /**
     * HTTP 响应结果类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HttpResponseResult {
        private int statusCode;
        private String body;
        private Map<String, List<String>> headers;
        private long responseTimeMs;
    }
    
    /**
     * 文件操作结果类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FileOperationResult {
        private boolean success;
        private String message;
        private String content;
        private long fileSizeBytes;
    }
    
    /**
     * 演示 Java 11 HttpClient 新特性
     * 替代传统的 HttpURLConnection，提供更现代的 HTTP 客户端
     */
    public void demonstrateHttpClient() {
        System.out.println("\n=== Java 11 HttpClient 演示 ===");
        
        // 1. 创建 HttpClient
        HttpClient client = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)  // 支持 HTTP/2
            .connectTimeout(Duration.ofSeconds(10))
            .build();
        
        try {
            // 2. 同步 GET 请求
            System.out.println("\n1. 同步 GET 请求:");
            HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/get"))
                .timeout(Duration.ofSeconds(30))
                .header("User-Agent", "Java11-HttpClient")
                .GET()
                .build();
            
            long startTime = System.currentTimeMillis();
            HttpResponse<String> getResponse = client.send(getRequest, 
                HttpResponse.BodyHandlers.ofString());
            long responseTime = System.currentTimeMillis() - startTime;
            
            System.out.println("状态码: " + getResponse.statusCode());
            System.out.println("响应时间: " + responseTime + "ms");
            System.out.println("响应头: " + getResponse.headers().map());
            System.out.println("响应体长度: " + getResponse.body().length() + " 字符");
            
            // 3. 同步 POST 请求
            System.out.println("\n2. 同步 POST 请求:");
            String jsonBody = "{\"name\":\"Java11\",\"version\":\"LTS\"}";
            HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://httpbin.org/post"))
                .timeout(Duration.ofSeconds(30))
                .header("Content-Type", "application/json")
                .header("User-Agent", "Java11-HttpClient")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();
            
            startTime = System.currentTimeMillis();
            HttpResponse<String> postResponse = client.send(postRequest, 
                HttpResponse.BodyHandlers.ofString());
            responseTime = System.currentTimeMillis() - startTime;
            
            System.out.println("状态码: " + postResponse.statusCode());
            System.out.println("响应时间: " + responseTime + "ms");
            System.out.println("请求体已发送: " + jsonBody);
            
            // 4. 异步请求
            System.out.println("\n3. 异步请求:");
            CompletableFuture<HttpResponse<String>> asyncResponse = client.sendAsync(
                getRequest, HttpResponse.BodyHandlers.ofString());
            
            asyncResponse.thenApply(HttpResponse::body)
                        .thenApply(String::length)
                        .thenAccept(length -> System.out.println("异步响应体长度: " + length + " 字符"))
                        .join(); // 等待完成
            
        } catch (IOException | InterruptedException e) {
            System.err.println("HTTP 请求失败: " + e.getMessage());
        }
    }
    
    /**
     * 演示 String 新方法
     * Java 11 为 String 类添加了多个实用方法
     */
    public void demonstrateStringMethods() {
        System.out.println("\n=== Java 11 String 新方法演示 ===");
        
        // 1. isBlank() - 检查字符串是否为空或只包含空白字符
        System.out.println("\n1. isBlank() 方法:");
        String[] testStrings = {"", "   ", "\t\n", "Hello", " World "};
        for (String str : testStrings) {
            System.out.printf("'%s' -> isEmpty: %b, isBlank: %b%n", 
                str.replace("\t", "\\t").replace("\n", "\\n"), 
                str.isEmpty(), str.isBlank());
        }
        
        // 2. strip(), stripLeading(), stripTrailing() - 去除空白字符（支持 Unicode）
        System.out.println("\n2. strip 系列方法:");
        String unicodeSpaces = "\u2000\u2001  Hello World  \u2002\u2003";
        System.out.println("原字符串长度: " + unicodeSpaces.length());
        System.out.println("trim() 后长度: " + unicodeSpaces.trim().length());
        System.out.println("strip() 后长度: " + unicodeSpaces.strip().length());
        System.out.println("stripLeading(): '" + unicodeSpaces.stripLeading() + "'");
        System.out.println("stripTrailing(): '" + unicodeSpaces.stripTrailing() + "'");
        
        // 3. lines() - 将字符串按行分割为 Stream
        System.out.println("\n3. lines() 方法:");
        String multilineText = "第一行\n第二行\r\n第三行\n\n第五行";
        System.out.println("原文本:");
        System.out.println(multilineText);
        System.out.println("\n按行处理:");
        multilineText.lines()
                    .filter(line -> !line.isBlank())
                    .map(line -> "处理: " + line.strip())
                    .forEach(System.out::println);
        
        // 4. repeat() - 重复字符串
        System.out.println("\n4. repeat() 方法:");
        String pattern = "*-";
        for (int i = 0; i <= 5; i++) {
            System.out.println("重复 " + i + " 次: '" + pattern.repeat(i) + "'");
        }
        
        // 5. 实际应用示例
        System.out.println("\n5. 实际应用示例:");
        demonstrateStringMethodsInPractice();
    }
    
    /**
     * String 新方法的实际应用示例
     */
    private void demonstrateStringMethodsInPractice() {
        // 处理用户输入数据
        List<String> userInputs = Arrays.asList(
            "  alice@example.com  ",
            "\t\tbob@test.com\n",
            "",
            "   ",
            "charlie@domain.com",
            "\u2000david@site.com\u2001"
        );
        
        System.out.println("清理用户输入数据:");
        List<String> cleanedEmails = userInputs.stream()
            .filter(input -> !input.isBlank())  // 过滤空白输入
            .map(String::strip)                 // 清理空白字符
            .filter(email -> email.contains("@")) // 简单邮箱验证
            .collect(Collectors.toList());
        
        cleanedEmails.forEach(email -> System.out.println("有效邮箱: " + email));
        
        // 生成分隔线
        System.out.println("\n生成分隔线:");
        String separator = "=".repeat(50);
        System.out.println(separator);
        System.out.println("标题内容");
        System.out.println(separator);
        
        // 处理多行文本
        String logText = "INFO: 应用启动\nWARN: 配置文件缺失\nERROR: 数据库连接失败\n\nINFO: 重试连接";
        System.out.println("\n日志处理:");
        Map<String, Long> logLevelCount = logText.lines()
            .filter(line -> !line.isBlank())
            .map(String::strip)
            .collect(Collectors.groupingBy(
                line -> line.split(":")[0],
                Collectors.counting()
            ));
        
        logLevelCount.forEach((level, count) -> 
            System.out.println(level + " 级别日志: " + count + " 条"));
    }
    
    /**
     * 演示 Files 便捷 IO 方法
     * Java 11 为 Files 类添加了更多便捷的文件操作方法
     */
    public void demonstrateFilesIO() {
        System.out.println("\n=== Java 11 Files 便捷 IO 演示 ===");
        
        try {
            // 1. writeString() 和 readString() - 直接读写字符串
            System.out.println("\n1. 字符串文件读写:");
            Path tempFile = Files.createTempFile("java11-demo", ".txt");
            
            String content = "Java 11 新特性演示\n" +
                           "包含中文字符测试\n" +
                           "多行文本内容\n" +
                           "时间戳: " + System.currentTimeMillis();
            
            // 写入字符串到文件
            Files.writeString(tempFile, content);
            System.out.println("已写入文件: " + tempFile);
            System.out.println("文件大小: " + Files.size(tempFile) + " 字节");
            
            // 从文件读取字符串
            String readContent = Files.readString(tempFile);
            System.out.println("读取内容:");
            System.out.println(readContent);
            
            // 2. 使用不同字符编码
            System.out.println("\n2. 字符编码处理:");
            Path utf8File = Files.createTempFile("utf8-demo", ".txt");
            String unicodeContent = "Unicode 测试: 🚀 Java 11 ✨ 新特性 🎉";
            
            Files.writeString(utf8File, unicodeContent, java.nio.charset.StandardCharsets.UTF_8);
            String readUnicodeContent = Files.readString(utf8File, java.nio.charset.StandardCharsets.UTF_8);
            System.out.println("UTF-8 内容: " + readUnicodeContent);
            
            // 3. 文件操作性能对比
            System.out.println("\n3. 性能对比:");
            demonstrateFilesPerformance();
            
            // 清理临时文件
            Files.deleteIfExists(tempFile);
            Files.deleteIfExists(utf8File);
            
        } catch (IOException e) {
            System.err.println("文件操作失败: " + e.getMessage());
        }
    }
    
    /**
     * Files 操作性能对比
     */
    private void demonstrateFilesPerformance() throws IOException {
        String testContent = "测试内容\n".repeat(1000); // 1000 行测试数据
        
        // Java 11 方式
        long startTime = System.nanoTime();
        Path java11File = Files.createTempFile("java11-perf", ".txt");
        Files.writeString(java11File, testContent);
        String java11Read = Files.readString(java11File);
        long java11Time = System.nanoTime() - startTime;
        
        // 传统方式
        startTime = System.nanoTime();
        Path traditionalFile = Files.createTempFile("traditional-perf", ".txt");
        Files.write(traditionalFile, testContent.getBytes());
        byte[] traditionalRead = Files.readAllBytes(traditionalFile);
        String traditionalContent = new String(traditionalRead);
        long traditionalTime = System.nanoTime() - startTime;
        
        System.out.printf("Java 11 方式耗时: %.2f ms%n", java11Time / 1_000_000.0);
        System.out.printf("传统方式耗时: %.2f ms%n", traditionalTime / 1_000_000.0);
        System.out.printf("性能比较: Java 11 是传统方式的 %.2f 倍%n", 
            (double) traditionalTime / java11Time);
        
        // 验证内容一致性
        System.out.println("内容一致性: " + java11Read.equals(traditionalContent));
        
        // 清理
        Files.deleteIfExists(java11File);
        Files.deleteIfExists(traditionalFile);
    }
    
    /**
     * 演示 Optional.isEmpty() 方法
     * Java 11 为 Optional 添加了 isEmpty() 方法，与 isPresent() 相对应
     */
    public void demonstrateOptionalIsEmpty() {
        System.out.println("\n=== Java 11 Optional.isEmpty() 演示 ===");
        
        // 1. 基本用法对比
        System.out.println("\n1. isEmpty() vs isPresent():");
        Optional<String> emptyOpt = Optional.empty();
        Optional<String> presentOpt = Optional.of("Hello Java 11");
        
        System.out.println("空 Optional:");
        System.out.println("  isEmpty(): " + emptyOpt.isEmpty());
        System.out.println("  isPresent(): " + emptyOpt.isPresent());
        
        System.out.println("非空 Optional:");
        System.out.println("  isEmpty(): " + presentOpt.isEmpty());
        System.out.println("  isPresent(): " + presentOpt.isPresent());
        
        // 2. 在条件判断中的使用
        System.out.println("\n2. 条件判断中的使用:");
        List<Optional<String>> optionals = Arrays.asList(
            Optional.of("有值1"),
            Optional.empty(),
            Optional.of("有值2"),
            Optional.empty(),
            Optional.of("有值3")
        );
        
        // 使用 isEmpty() 过滤
        System.out.println("空的 Optional 数量: " + 
            optionals.stream().mapToInt(opt -> opt.isEmpty() ? 1 : 0).sum());
        
        // 使用 isPresent() 过滤
        System.out.println("非空的 Optional 数量: " + 
            optionals.stream().mapToInt(opt -> opt.isPresent() ? 1 : 0).sum());
        
        // 3. 实际应用场景
        System.out.println("\n3. 实际应用场景:");
        demonstrateOptionalIsEmptyInPractice();
    }
    
    /**
     * Optional.isEmpty() 的实际应用示例
     */
    private void demonstrateOptionalIsEmptyInPractice() {
        List<User> users = Arrays.asList(
            new User("Alice", "alice@example.com", 25, "Java 开发者"),
            new User("Bob", "bob@example.com", null, null),
            new User("Charlie", "charlie@example.com", 30, ""),
            new User("Diana", "diana@example.com", 28, "Python 开发者")
        );
        
        System.out.println("用户数据验证:");
        users.forEach(user -> {
            Optional<Integer> age = Optional.ofNullable(user.getAge());
            Optional<String> description = Optional.ofNullable(user.getDescription())
                .filter(desc -> !desc.isBlank());
            
            System.out.printf("用户 %s: ", user.getName());
            
            if (age.isEmpty()) {
                System.out.print("年龄缺失 ");
            } else {
                System.out.printf("年龄 %d ", age.get());
            }
            
            if (description.isEmpty()) {
                System.out.print("描述缺失");
            } else {
                System.out.printf("描述: %s", description.get());
            }
            
            System.out.println();
        });
        
        // 统计缺失数据
        long missingAgeCount = users.stream()
            .map(user -> Optional.ofNullable(user.getAge()))
            .mapToLong(opt -> opt.isEmpty() ? 1 : 0)
            .sum();
        
        long missingDescCount = users.stream()
            .map(user -> Optional.ofNullable(user.getDescription())
                              .filter(desc -> !desc.isBlank()))
            .mapToLong(opt -> opt.isEmpty() ? 1 : 0)
            .sum();
        
        System.out.println("\n数据完整性统计:");
        System.out.println("缺失年龄的用户: " + missingAgeCount);
        System.out.println("缺失描述的用户: " + missingDescCount);
    }
    
    /**
     * 演示 var 在 lambda 参数中的使用
     * Java 11 允许在 lambda 表达式的参数中使用 var
     */
    public void demonstrateVarInLambda() {
        System.out.println("\n=== Java 11 var in Lambda 演示 ===");
        
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "Diana");
        
        // 1. 基本用法
        System.out.println("\n1. 基本用法对比:");
        
        // 传统方式
        System.out.println("传统方式:");
        names.stream()
             .map((String name) -> name.toUpperCase())
             .forEach(System.out::println);
        
        // 类型推断
        System.out.println("\n类型推断:");
        names.stream()
             .map(name -> name.toLowerCase())
             .forEach(System.out::println);
        
        // Java 11 var
        System.out.println("\nJava 11 var:");
        names.stream()
             .map((var name) -> name.toUpperCase() + "!")
             .forEach(System.out::println);
        
        // 2. var 的优势：支持注解
        System.out.println("\n2. var 支持注解:");
        demonstrateVarWithAnnotations();
        
        // 3. 复杂类型的 var 使用
        System.out.println("\n3. 复杂类型的 var 使用:");
        demonstrateVarWithComplexTypes();
    }
    
    /**
     * 演示 var 与注解的结合使用
     */
    private void demonstrateVarWithAnnotations() {
        List<String> emails = Arrays.asList(
            "alice@example.com",
            "invalid-email",
            "bob@test.com",
            "",
            "charlie@domain.com"
        );
        
        // 使用 var 可以添加注解（这里用注释模拟，因为需要自定义注解）
        System.out.println("邮箱验证处理:");
        emails.stream()
              .filter((var email) -> !email.isBlank()) // var 允许添加注解
              .filter((var email) -> email.contains("@"))
              .map((var email) -> "有效邮箱: " + email)
              .forEach(System.out::println);
    }
    
    /**
     * 演示 var 在复杂类型中的使用
     */
    private void demonstrateVarWithComplexTypes() {
        List<User> users = Arrays.asList(
            new User("Alice", "alice@example.com", 25, "Java 开发者"),
            new User("Bob", "bob@example.com", 30, "Python 开发者"),
            new User("Charlie", "charlie@example.com", 35, "Go 开发者")
        );
        
        // 复杂的 lambda 表达式中使用 var
        System.out.println("用户信息处理:");
        users.stream()
             .filter((var user) -> user.getAge() >= 30)
             .map((var user) -> {
                 return String.format("高级开发者: %s (%d岁) - %s", 
                     user.getName(), user.getAge(), user.getDescription());
             })
             .forEach(System.out::println);
        
        // 在 Comparator 中使用 var
        System.out.println("\n按年龄排序:");
        users.stream()
             .sorted((var u1, var u2) -> Integer.compare(u1.getAge(), u2.getAge()))
             .map((var user) -> user.getName() + " (" + user.getAge() + "岁)")
             .forEach(System.out::println);
    }
    
    /**
     * 演示单文件源码启动特性
     * Java 11 支持直接运行单个 .java 文件，无需先编译
     */
    public void demonstrateSingleFileSourceLaunch() {
        System.out.println("\n=== Java 11 单文件源码启动演示 ===");
        
        System.out.println("\nJava 11 单文件源码启动特性:");
        System.out.println("1. 可以直接运行 .java 文件: java HelloWorld.java");
        System.out.println("2. 无需先使用 javac 编译");
        System.out.println("3. 适合脚本化使用和快速原型开发");
        System.out.println("4. 支持 shebang (#!) 语法，可在 Unix 系统中作为脚本执行");
        
        // 创建示例单文件程序
        String sampleCode = generateSampleSingleFileCode();
        System.out.println("\n示例单文件程序:");
        System.out.println(sampleCode);
        
        // 演示如何使用
        System.out.println("\n使用方法:");
        System.out.println("1. 将上述代码保存为 QuickDemo.java");
        System.out.println("2. 运行命令: java QuickDemo.java");
        System.out.println("3. 或在 Unix 系统中添加 shebang 后直接执行: ./QuickDemo.java");
    }
    
    /**
     * 生成示例单文件程序代码
     */
    private String sampleCode;
    
    private String generateSampleSingleFileCode() {
        return "#!/usr/bin/java --source 11\n" +
               "\n" +
               "import java.util.*;\n" +
               "import java.util.stream.*;\n" +
               "\n" +
               "/**\n" +
               " * Java 11 单文件源码启动示例\n" +
               " * 可以直接运行: java QuickDemo.java\n" +
               " */\n" +
               "public class QuickDemo {\n" +
               "    public static void main(String[] args) {\n" +
               "        System.out.println(\"Java 11 单文件启动演示\");\n" +
               "        \n" +
               "        // 使用 Java 11 新特性\n" +
               "        List<String> items = List.of(\"Java\", \"11\", \"Features\");\n" +
               "        \n" +
               "        String result = items.stream()\n" +
               "            .map((var item) -> item.toUpperCase())\n" +
               "            .collect(Collectors.joining(\" \"));\n" +
               "        \n" +
               "        System.out.println(\"结果: \" + result);\n" +
               "        \n" +
               "        // 字符串新方法\n" +
               "        String text = \"  Hello World  \";\n" +
               "        System.out.println(\"原文: '\" + text + \"'\");\n" +
               "        System.out.println(\"清理后: '\" + text.strip() + \"'\");\n" +
               "        System.out.println(\"是否空白: \" + text.isBlank());\n" +
               "        \n" +
               "        // 重复字符串\n" +
               "        System.out.println(\"分隔线: \" + \"-\".repeat(30));\n" +
               "    }\n" +
               "}";
    }
    
    /**
     * Java 11 特性性能基准测试
     */
    public BenchmarkResultDTO benchmarkJava11Features() {
        System.out.println("\n=== Java 11 特性性能基准测试 ===");
        
        long startTime = System.currentTimeMillis();
        Map<String, Object> metrics = new HashMap<>();
        
        // 1. String 方法性能测试
        long stringTestStart = System.nanoTime();
        testStringMethodsPerformance(metrics);
        long stringTestTime = System.nanoTime() - stringTestStart;
        
        // 2. Files IO 性能测试
        long filesTestStart = System.nanoTime();
        testFilesIOPerformance(metrics);
        long filesTestTime = System.nanoTime() - filesTestStart;
        
        // 3. Optional 性能测试
        long optionalTestStart = System.nanoTime();
        testOptionalPerformance(metrics);
        long optionalTestTime = System.nanoTime() - optionalTestStart;
        
        long totalTime = System.currentTimeMillis() - startTime;
        
        // 添加测试时间指标
        metrics.put("String方法测试耗时(ms)", stringTestTime / 1_000_000.0);
        metrics.put("Files IO测试耗时(ms)", filesTestTime / 1_000_000.0);
        metrics.put("Optional测试耗时(ms)", optionalTestTime / 1_000_000.0);
        metrics.put("总测试时间(ms)", (double) totalTime);
        
        BenchmarkResultDTO result = new BenchmarkResultDTO(
            "Java 11 特性性能测试",
            "测试 String 新方法、Files IO、Optional.isEmpty 等特性的性能",
            totalTime,
            metrics
        );
        
        System.out.println("\n性能测试完成:");
        System.out.printf("总耗时: %d ms%n", totalTime);
        System.out.printf("String 方法测试: %.2f ms%n", (Double) metrics.get("String方法测试耗时(ms)"));
        System.out.printf("Files IO 测试: %.2f ms%n", (Double) metrics.get("Files IO测试耗时(ms)"));
        System.out.printf("Optional 测试: %.2f ms%n", (Double) metrics.get("Optional测试耗时(ms)"));
        
        return result;
    }
    
    /**
     * String 方法性能测试
     */
    private void testStringMethodsPerformance(Map<String, Object> metrics) {
        int iterations = 100_000;
        String testString = "  \t\n  Hello Java 11 World  \u2000\u2001  ";
        
        // 定义Optional变量用于性能测试
        Optional<String> emptyOpt = Optional.empty();
        Optional<String> presentOpt = Optional.of("test");
        
        // 测试 strip vs trim
        long stripStart = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            testString.strip();
        }
        long stripTime = System.nanoTime() - stripStart;
        
        long trimStart = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            testString.trim();
        }
        long trimTime = System.nanoTime() - trimStart;
        
        // 测试 isBlank vs isEmpty
        long isBlankStart = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            testString.isBlank();
        }
        long isBlankTime = System.nanoTime() - isBlankStart;
        
        long isEmptyStart = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            testString.isEmpty();
        }
        long isEmptyTime = System.nanoTime() - isEmptyStart;
        
        int presentSink = 0; // 防止JIT消除，累加到metrics
        long isPresentStart = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            if (!emptyOpt.isPresent()) { presentSink++; }
            if (!presentOpt.isPresent()) { presentSink++; }
        }
        long isPresentTime = System.nanoTime() - isPresentStart;
        
        metrics.put("String.strip()耗时(ms)", stripTime / 1_000_000.0);
        metrics.put("String.trim()耗时(ms)", trimTime / 1_000_000.0);
        metrics.put("String.isBlank()耗时(ms)", isBlankTime / 1_000_000.0);
        metrics.put("String.isEmpty()耗时(ms)", isEmptyTime / 1_000_000.0);
        metrics.put("!Optional.isPresent()耗时(ms)", isPresentTime / 1_000_000.0);
        metrics.put("String测试迭代次数", iterations);
        metrics.put("strip相对trim倍数", (double) stripTime / trimTime);
        metrics.put("isBlank相对isEmpty倍数", (double) isBlankTime / isEmptyTime);
        metrics.put("Optional !isPresent() dummy", presentSink);
    }
    
    /**
     * Files IO 性能测试
     */
    private void testFilesIOPerformance(Map<String, Object> metrics) {
        try {
            String testContent = "测试内容\n".repeat(1000);
            int iterations = 100;
            
            // 测试 writeString/readString vs write/readAllBytes
            long java11Start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                Path tempFile = Files.createTempFile("perf-test", ".txt");
                Files.writeString(tempFile, testContent);
                Files.readString(tempFile);
                Files.deleteIfExists(tempFile);
            }
            long java11Time = System.nanoTime() - java11Start;
            
            long traditionalStart = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                Path tempFile = Files.createTempFile("perf-test", ".txt");
                Files.write(tempFile, testContent.getBytes());
                new String(Files.readAllBytes(tempFile));
                Files.deleteIfExists(tempFile);
            }
            long traditionalTime = System.nanoTime() - traditionalStart;
            
            metrics.put("Files writeString/readString耗时(ms)", java11Time / 1_000_000.0);
            metrics.put("Files write/readAllBytes耗时(ms)", traditionalTime / 1_000_000.0);
            metrics.put("Files测试迭代次数", iterations);
            metrics.put("Java11 Files相对传统倍数", (double) java11Time / traditionalTime);
            
        } catch (IOException e) {
            metrics.put("Files测试错误", e.getMessage());
        }
    }
    
    /**
     * Optional 性能测试
     */
    private void testOptionalPerformance(Map<String, Object> metrics) {
        int iterations = 1_000_000;
        Optional<String> emptyOpt = Optional.empty();
        Optional<String> presentOpt = Optional.of("test");
        
        // 测试 isEmpty vs !isPresent
        long isEmptyStart = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            emptyOpt.isEmpty();
            presentOpt.isEmpty();
        }
        long isEmptyTime = System.nanoTime() - isEmptyStart;
        
        int presentSink = 0; // 防止JIT消除，累加到metrics
        long isPresentStart = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            if (!emptyOpt.isPresent()) { presentSink++; }
            if (!presentOpt.isPresent()) { presentSink++; }
        }
        long isPresentTime = System.nanoTime() - isPresentStart;
        
        metrics.put("Optional.isEmpty()耗时(ms)", isEmptyTime / 1_000_000.0);
        metrics.put("!Optional.isPresent()耗时(ms)", isPresentTime / 1_000_000.0);
        metrics.put("Optional测试迭代次数", iterations);
        metrics.put("isEmpty相对!isPresent倍数", (double) isEmptyTime / isPresentTime);
        metrics.put("Optional !isPresent() dummy", presentSink);
    }
    
    /**
     * 运行所有演示
     */
    public void runAllDemonstrations() {
        System.out.println("Java 11 新特性综合演示");
        System.out.println("=".repeat(50));
        
        demonstrateHttpClient();
        demonstrateStringMethods();
        demonstrateFilesIO();
        demonstrateOptionalIsEmpty();
        demonstrateVarInLambda();
        demonstrateSingleFileSourceLaunch();
        
        BenchmarkResultDTO benchmark = benchmarkJava11Features();
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Java 11 特性演示完成");
        System.out.printf("总体性能测试耗时: %d ms%n", benchmark.getExecutionTimeMs());
    }
    
    /**
     * 主方法 - 演示入口
     */
    public static void main(String[] args) {
        Java11FeaturesDemo demo = new Java11FeaturesDemo();
        demo.runAllDemonstrations();
    }
}