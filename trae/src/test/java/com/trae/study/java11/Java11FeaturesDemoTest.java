package com.trae.study.java11;

import com.trae.study.dto.BenchmarkResultDTO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Java 11 新特性演示测试类
 * 验证 Java 11 各项新特性的功能正确性
 * 
 * @author trae
 * @since 2024
 */
@DisplayName("Java 11 新特性演示测试")
class Java11FeaturesDemoTest {
    
    private Java11FeaturesDemo demo;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        demo = new Java11FeaturesDemo();
        
        // 捕获控制台输出
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }
    
    @AfterEach
    void tearDown() {
        // 恢复控制台输出
        System.setOut(originalOut);
    }
    
    @Nested
    @DisplayName("HttpClient 功能测试")
    class HttpClientTests {
        
        @Test
        @DisplayName("HttpClient 演示方法执行测试")
        void testDemonstrateHttpClient() {
            // 由于网络请求的不确定性，这里主要测试方法能正常执行
            assertDoesNotThrow(() -> {
                demo.demonstrateHttpClient();
            });
            
            String output = outputStream.toString();
            assertTrue(output.contains("HttpClient 演示"));
        }
        
        @Test
        @DisplayName("HttpClient 基本功能验证")
        void testHttpClientBasicFeatures() {
            // 测试 HttpClient 的基本创建和配置
            assertDoesNotThrow(() -> {
                java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
                    .version(java.net.http.HttpClient.Version.HTTP_2)
                    .connectTimeout(java.time.Duration.ofSeconds(10))
                    .build();
                
                assertNotNull(client);
                assertEquals(java.net.http.HttpClient.Version.HTTP_2, client.version());
            });
        }
        
        @Test
        @DisplayName("HttpRequest 构建测试")
        void testHttpRequestBuilder() {
            assertDoesNotThrow(() -> {
                java.net.http.HttpRequest request = java.net.http.HttpRequest.newBuilder()
                    .uri(java.net.URI.create("https://httpbin.org/get"))
                    .timeout(java.time.Duration.ofSeconds(30))
                    .header("User-Agent", "Java11-HttpClient")
                    .GET()
                    .build();
                
                assertNotNull(request);
                assertEquals("GET", request.method());
                assertTrue(request.headers().firstValue("User-Agent").isPresent());
                assertEquals("Java11-HttpClient", request.headers().firstValue("User-Agent").get());
            });
        }
    }
    
    @Nested
    @DisplayName("String 新方法测试")
    class StringMethodsTests {
        
        @Test
        @DisplayName("isBlank() 方法测试")
        void testIsBlank() {
            // 测试各种空白字符情况
            assertTrue("".isBlank());
            assertTrue("   ".isBlank());
            assertTrue("\t\n\r".isBlank());
            assertTrue("\u2000\u2001".isBlank()); // Unicode 空白字符
            
            assertFalse("Hello".isBlank());
            assertFalse(" Hello ".isBlank());
            assertFalse("\tHello\n".isBlank());
        }
        
        @Test
        @DisplayName("strip 系列方法测试")
        void testStripMethods() {
            String testString = "\u2000\u2001  Hello World  \u2002\u2003";
            
            // strip() 应该移除所有 Unicode 空白字符
            assertEquals("Hello World", testString.strip());
            
            // stripLeading() 只移除开头的空白字符
            assertEquals("Hello World  \u2002\u2003", testString.stripLeading());
            
            // stripTrailing() 只移除结尾的空白字符
            assertEquals("\u2000\u2001  Hello World", testString.stripTrailing());
            
            // 对比 trim() 的行为
            String asciiSpaces = "  Hello World  ";
            assertEquals(asciiSpaces.trim(), asciiSpaces.strip());
        }
        
        @Test
        @DisplayName("lines() 方法测试")
        void testLines() {
            String multilineText = "第一行\n第二行\r\n第三行\n\n第五行";
            
            List<String> lines = multilineText.lines().collect(Collectors.toList());
            
            assertEquals(5, lines.size());
            assertEquals("第一行", lines.get(0));
            assertEquals("第二行", lines.get(1));
            assertEquals("第三行", lines.get(2));
            assertEquals("", lines.get(3)); // 空行
            assertEquals("第五行", lines.get(4));
            
            // 测试过滤非空行
            List<String> nonEmptyLines = multilineText.lines()
                .filter(line -> !line.isBlank())
                .collect(Collectors.toList());
            
            assertEquals(4, nonEmptyLines.size());
        }
        
        @Test
        @DisplayName("repeat() 方法测试")
        void testRepeat() {
            assertEquals("", "abc".repeat(0));
            assertEquals("abc", "abc".repeat(1));
            assertEquals("abcabc", "abc".repeat(2));
            assertEquals("abcabcabc", "abc".repeat(3));
            
            // 测试空字符串
            assertEquals("", "".repeat(5));
            
            // 测试单字符
            assertEquals("*****", "*".repeat(5));
        }
        
        @Test
        @DisplayName("String 新方法演示执行测试")
        void testDemonstrateStringMethods() {
            assertDoesNotThrow(() -> {
                demo.demonstrateStringMethods();
            });
            
            String output = outputStream.toString();
            assertTrue(output.contains("String 新方法演示"));
            assertTrue(output.contains("isBlank() 方法"));
            assertTrue(output.contains("strip 系列方法"));
            assertTrue(output.contains("lines() 方法"));
            assertTrue(output.contains("repeat() 方法"));
        }
    }
    
    @Nested
    @DisplayName("Files 便捷 IO 测试")
    class FilesIOTests {
        
        @Test
        @DisplayName("writeString 和 readString 测试")
        void testWriteStringAndReadString() throws IOException {
            Path testFile = tempDir.resolve("test.txt");
            String content = "Java 11 新特性测试\n包含中文字符\n多行文本";
            
            // 写入字符串
            Files.writeString(testFile, content);
            assertTrue(Files.exists(testFile));
            assertTrue(Files.size(testFile) > 0);
            
            // 读取字符串
            String readContent = Files.readString(testFile);
            assertEquals(content, readContent);
        }
        
        @Test
        @DisplayName("字符编码处理测试")
        void testCharacterEncoding() throws IOException {
            Path testFile = tempDir.resolve("utf8-test.txt");
            String unicodeContent = "Unicode 测试: 🚀 Java 11 ✨ 新特性 🎉";
            
            // 使用 UTF-8 编码写入
            Files.writeString(testFile, unicodeContent, java.nio.charset.StandardCharsets.UTF_8);
            
            // 使用 UTF-8 编码读取
            String readContent = Files.readString(testFile, java.nio.charset.StandardCharsets.UTF_8);
            assertEquals(unicodeContent, readContent);
            
            // 验证 Unicode 字符正确保存
            assertTrue(readContent.contains("🚀"));
            assertTrue(readContent.contains("✨"));
            assertTrue(readContent.contains("🎉"));
        }
        
        @Test
        @DisplayName("Files IO 演示执行测试")
        void testDemonstrateFilesIO() {
            assertDoesNotThrow(() -> {
                demo.demonstrateFilesIO();
            });
            
            String output = outputStream.toString();
            assertTrue(output.contains("Files 便捷 IO 演示"));
            assertTrue(output.contains("字符串文件读写"));
            assertTrue(output.contains("字符编码处理"));
        }
        
        @Test
        @DisplayName("大文件处理测试")
        void testLargeFileHandling() throws IOException {
            Path testFile = tempDir.resolve("large-test.txt");
            
            // 创建较大的测试内容
            String lineContent = "这是测试行内容，包含中文字符和数字123\n";
            String largeContent = lineContent.repeat(1000); // 1000 行
            
            long startTime = System.currentTimeMillis();
            Files.writeString(testFile, largeContent);
            String readContent = Files.readString(testFile);
            long endTime = System.currentTimeMillis();
            
            assertEquals(largeContent, readContent);
            assertTrue(Files.size(testFile) > 10000); // 文件应该足够大
            
            // 性能应该在合理范围内（1秒内）
            assertTrue(endTime - startTime < 1000, 
                "大文件读写耗时过长: " + (endTime - startTime) + "ms");
        }
    }
    
    @Nested
    @DisplayName("Optional.isEmpty() 测试")
    class OptionalIsEmptyTests {
        
        @Test
        @DisplayName("isEmpty() 基本功能测试")
        void testIsEmptyBasicFunctionality() {
            Optional<String> emptyOpt = Optional.empty();
            Optional<String> presentOpt = Optional.of("test");
            
            // 测试 isEmpty()
            assertTrue(emptyOpt.isEmpty());
            assertFalse(presentOpt.isEmpty());
            
            // 验证与 isPresent() 的对应关系
            assertEquals(emptyOpt.isEmpty(), !emptyOpt.isPresent());
            assertEquals(presentOpt.isEmpty(), !presentOpt.isPresent());
        }
        
        @Test
        @DisplayName("isEmpty() 在流操作中的使用测试")
        void testIsEmptyInStreamOperations() {
            List<Optional<String>> optionals = Arrays.asList(
                Optional.of("有值1"),
                Optional.empty(),
                Optional.of("有值2"),
                Optional.empty(),
                Optional.of("有值3")
            );
            
            // 统计空的 Optional
            long emptyCount = optionals.stream()
                .mapToLong(opt -> opt.isEmpty() ? 1 : 0)
                .sum();
            assertEquals(2, emptyCount);
            
            // 统计非空的 Optional
            long presentCount = optionals.stream()
                .mapToLong(opt -> opt.isPresent() ? 1 : 0)
                .sum();
            assertEquals(3, presentCount);
            
            // 验证总数
            assertEquals(optionals.size(), emptyCount + presentCount);
        }
        
        @Test
        @DisplayName("Optional.isEmpty() 演示执行测试")
        void testDemonstrateOptionalIsEmpty() {
            assertDoesNotThrow(() -> {
                demo.demonstrateOptionalIsEmpty();
            });
            
            String output = outputStream.toString();
            assertTrue(output.contains("Optional.isEmpty() 演示"));
            assertTrue(output.contains("isEmpty() vs isPresent()"));
        }
        
        @Test
        @DisplayName("Optional 与 null 安全性测试")
        void testOptionalNullSafety() {
            // 测试 ofNullable 与 isEmpty 的结合
            String nullString = null;
            String emptyString = "";
            String validString = "valid";
            
            Optional<String> nullOpt = Optional.ofNullable(nullString);
            Optional<String> emptyOpt = Optional.ofNullable(emptyString);
            Optional<String> validOpt = Optional.ofNullable(validString);
            
            assertTrue(nullOpt.isEmpty());
            assertFalse(emptyOpt.isEmpty()); // 空字符串不是 null
            assertFalse(validOpt.isEmpty());
            
            // 测试过滤空白字符串
            Optional<String> filteredEmptyOpt = Optional.ofNullable(emptyString)
                .filter(s -> !s.isBlank());
            assertTrue(filteredEmptyOpt.isEmpty());
        }
    }
    
    @Nested
    @DisplayName("var in Lambda 测试")
    class VarInLambdaTests {
        
        @Test
        @DisplayName("var 在 lambda 中的基本使用测试")
        void testVarInLambdaBasicUsage() {
            List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
            
            // 使用 var 的 lambda
            List<String> upperNames = names.stream()
                .map((var name) -> name.toUpperCase())
                .collect(Collectors.toList());
            
            assertEquals(3, upperNames.size());
            assertEquals("ALICE", upperNames.get(0));
            assertEquals("BOB", upperNames.get(1));
            assertEquals("CHARLIE", upperNames.get(2));
        }
        
        @Test
        @DisplayName("var 在复杂 lambda 中的使用测试")
        void testVarInComplexLambda() {
            List<Java11FeaturesDemo.User> users = Arrays.asList(
                new Java11FeaturesDemo.User("Alice", "alice@example.com", 25, "Java 开发者"),
                new Java11FeaturesDemo.User("Bob", "bob@example.com", 30, "Python 开发者"),
                new Java11FeaturesDemo.User("Charlie", "charlie@example.com", 35, "Go 开发者")
            );
            
            // 使用 var 进行复杂过滤和映射
            List<String> seniorDevelopers = users.stream()
                .filter((var user) -> user.getAge() >= 30)
                .map((var user) -> user.getName() + " (" + user.getAge() + "岁)")
                .collect(Collectors.toList());
            
            assertEquals(2, seniorDevelopers.size());
            assertTrue(seniorDevelopers.get(0).contains("Bob"));
            assertTrue(seniorDevelopers.get(1).contains("Charlie"));
        }
        
        @Test
        @DisplayName("var 在 Comparator 中的使用测试")
        void testVarInComparator() {
            List<Java11FeaturesDemo.User> users = Arrays.asList(
                new Java11FeaturesDemo.User("Charlie", "charlie@example.com", 35, "Go 开发者"),
                new Java11FeaturesDemo.User("Alice", "alice@example.com", 25, "Java 开发者"),
                new Java11FeaturesDemo.User("Bob", "bob@example.com", 30, "Python 开发者")
            );
            
            // 使用 var 进行排序
            List<Java11FeaturesDemo.User> sortedUsers = users.stream()
                .sorted((var u1, var u2) -> Integer.compare(u1.getAge(), u2.getAge()))
                .collect(Collectors.toList());
            
            assertEquals("Alice", sortedUsers.get(0).getName());
            assertEquals("Bob", sortedUsers.get(1).getName());
            assertEquals("Charlie", sortedUsers.get(2).getName());
        }
        
        @Test
        @DisplayName("var in Lambda 演示执行测试")
        void testDemonstrateVarInLambda() {
            assertDoesNotThrow(() -> {
                demo.demonstrateVarInLambda();
            });
            
            String output = outputStream.toString();
            assertTrue(output.contains("var in Lambda 演示"));
            assertTrue(output.contains("基本用法对比"));
        }
    }
    
    @Nested
    @DisplayName("单文件源码启动测试")
    class SingleFileSourceLaunchTests {
        
        @Test
        @DisplayName("单文件源码启动演示执行测试")
        void testDemonstrateSingleFileSourceLaunch() {
            assertDoesNotThrow(() -> {
                demo.demonstrateSingleFileSourceLaunch();
            });
            
            String output = outputStream.toString();
            assertTrue(output.contains("单文件源码启动演示"));
            assertTrue(output.contains("java HelloWorld.java"));
            assertTrue(output.contains("shebang"));
        }
        
        @Test
        @DisplayName("示例代码生成测试")
        void testSampleCodeGeneration() {
            // 通过反射访问私有方法进行测试
            assertDoesNotThrow(() -> {
                java.lang.reflect.Method method = Java11FeaturesDemo.class
                    .getDeclaredMethod("generateSampleSingleFileCode");
                method.setAccessible(true);
                String sampleCode = (String) method.invoke(demo);
                
                assertNotNull(sampleCode);
                assertTrue(sampleCode.contains("#!/usr/bin/java --source 11"));
                assertTrue(sampleCode.contains("public class QuickDemo"));
                assertTrue(sampleCode.contains("public static void main"));
                assertTrue(sampleCode.contains("(var item)"));
                assertTrue(sampleCode.contains(".strip()"));
                assertTrue(sampleCode.contains(".repeat("));
            });
        }
    }
    
    @Nested
    @DisplayName("性能基准测试")
    class BenchmarkTests {
        
        @Test
        @DisplayName("Java 11 特性性能基准测试")
        void testBenchmarkJava11Features() {
            BenchmarkResultDTO result = assertDoesNotThrow(() -> {
                return demo.benchmarkJava11Features();
            });
            
            assertNotNull(result);
            assertEquals("Java 11 特性性能测试", result.getTestName());
            assertTrue(result.getExecutionTimeMs() > 0);
            assertNotNull(result.getMetrics());
            
            // 验证关键指标存在
            Map<String, Object> metrics = result.getMetrics();
            assertTrue(metrics.containsKey("String方法测试耗时(ms)"));
            assertTrue(metrics.containsKey("Files IO测试耗时(ms)"));
            assertTrue(metrics.containsKey("Optional测试耗时(ms)"));
            assertTrue(metrics.containsKey("总测试时间(ms)"));
            
            // 验证性能指标合理性
            Double stringTestTime = (Double) metrics.get("String方法测试耗时(ms)");
            Double filesTestTime = (Double) metrics.get("Files IO测试耗时(ms)");
            Double optionalTestTime = (Double) metrics.get("Optional测试耗时(ms)");
            
            assertTrue(stringTestTime > 0, "String 测试时间应该大于 0");
            assertTrue(filesTestTime > 0, "Files 测试时间应该大于 0");
            assertTrue(optionalTestTime > 0, "Optional 测试时间应该大于 0");
        }
        
        @Test
        @DisplayName("String 方法性能对比测试")
        void testStringMethodsPerformanceComparison() {
            // 直接测试 String 新方法的性能特征
            String testString = "  \t\n  Hello Java 11 World  \u2000\u2001  ";
            int iterations = 10000;
            
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
            
            // 验证两种方法都能正常执行
            assertTrue(stripTime > 0);
            assertTrue(trimTime > 0);
            
            // 验证结果的正确性
            assertEquals("Hello Java 11 World", testString.strip());
            assertNotEquals(testString.strip(), testString.trim()); // Unicode 空白字符的差异
        }
        
        @Test
        @DisplayName("Optional 性能特征测试")
        void testOptionalPerformanceCharacteristics() {
            Optional<String> emptyOpt = Optional.empty();
            Optional<String> presentOpt = Optional.of("test");
            int iterations = 100000;
            
            // 测试 isEmpty 性能
            long isEmptyStart = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                emptyOpt.isEmpty();
                presentOpt.isEmpty();
            }
            long isEmptyTime = System.nanoTime() - isEmptyStart;
            
            // 测试 isPresent 性能
            long isPresentStart = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                emptyOpt.isPresent();
                presentOpt.isPresent();
            }
            long isPresentTime = System.nanoTime() - isPresentStart;
            
            // 验证性能合理性
            assertTrue(isEmptyTime > 0);
            assertTrue(isPresentTime > 0);
            
            // isEmpty 和 isPresent 的性能应该相近
            double ratio = (double) isEmptyTime / isPresentTime;
            assertTrue(ratio > 0.5 && ratio < 2.0, 
                "isEmpty 和 isPresent 性能差异过大: " + ratio);
        }
    }
    
    @Nested
    @DisplayName("综合功能测试")
    class IntegrationTests {
        
        @Test
        @DisplayName("所有演示方法执行测试")
        void testRunAllDemonstrations() {
            assertDoesNotThrow(() -> {
                demo.runAllDemonstrations();
            });
            
            String output = outputStream.toString();
            assertTrue(output.contains("Java 11 新特性综合演示"));
            assertTrue(output.contains("HttpClient 演示"));
            assertTrue(output.contains("String 新方法演示"));
            assertTrue(output.contains("Files 便捷 IO 演示"));
            assertTrue(output.contains("Optional.isEmpty() 演示"));
            assertTrue(output.contains("var in Lambda 演示"));
            assertTrue(output.contains("单文件源码启动演示"));
            assertTrue(output.contains("Java 11 特性演示完成"));
        }
        
        @Test
        @DisplayName("User 类功能测试")
        void testUserClass() {
            Java11FeaturesDemo.User user = new Java11FeaturesDemo.User(
                "Alice", "alice@example.com", 25, "Java 开发者");
            
            assertEquals("Alice", user.getName());
            assertEquals("alice@example.com", user.getEmail());
            assertEquals(Integer.valueOf(25), user.getAge());
            assertEquals("Java 开发者", user.getDescription());
            
            // 测试 setter
            user.setAge(26);
            assertEquals(Integer.valueOf(26), user.getAge());
            
            // 测试 toString（Lombok 生成）
            String userString = user.toString();
            assertTrue(userString.contains("Alice"));
            assertTrue(userString.contains("alice@example.com"));
        }
        
        @Test
        @DisplayName("HttpResponseResult 类功能测试")
        void testHttpResponseResultClass() {
            Map<String, List<String>> headers = new HashMap<>();
            headers.put("Content-Type", Arrays.asList("application/json"));
            
            Java11FeaturesDemo.HttpResponseResult result = 
                new Java11FeaturesDemo.HttpResponseResult(
                    200, "{\"status\":\"ok\"}", headers, 150L);
            
            assertEquals(200, result.getStatusCode());
            assertEquals("{\"status\":\"ok\"}", result.getBody());
            assertEquals(headers, result.getHeaders());
            assertEquals(150L, result.getResponseTimeMs());
        }
        
        @Test
        @DisplayName("FileOperationResult 类功能测试")
        void testFileOperationResultClass() {
            Java11FeaturesDemo.FileOperationResult result = 
                new Java11FeaturesDemo.FileOperationResult(
                    true, "操作成功", "文件内容", 1024L);
            
            assertTrue(result.isSuccess());
            assertEquals("操作成功", result.getMessage());
            assertEquals("文件内容", result.getContent());
            assertEquals(1024L, result.getFileSizeBytes());
        }
    }
    
    @Nested
    @DisplayName("边界条件测试")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("空字符串和 null 处理测试")
        void testEmptyStringAndNullHandling() {
            // 测试空字符串的各种新方法
            String empty = "";
            assertTrue(empty.isBlank());
            assertEquals("", empty.strip());
            assertEquals(0, empty.lines().count());
            assertEquals("", empty.repeat(5));
            
            // 测试只包含空白字符的字符串
            String whitespace = "   \t\n   ";
            assertTrue(whitespace.isBlank());
            assertEquals("", whitespace.strip());
            assertTrue(whitespace.lines().anyMatch(String::isBlank));
        }
        
        @Test
        @DisplayName("大数据量处理测试")
        void testLargeDataHandling() throws IOException {
            // 测试大量重复操作
            String pattern = "test";
            String repeated = pattern.repeat(10000);
            assertEquals(40000, repeated.length()); // 4 * 10000
            
            // 测试大文件读写
            Path largeFile = tempDir.resolve("large.txt");
            String largeContent = "大文件测试内容\n".repeat(5000);
            
            long startTime = System.currentTimeMillis();
            Files.writeString(largeFile, largeContent);
            String readContent = Files.readString(largeFile);
            long endTime = System.currentTimeMillis();
            
            assertEquals(largeContent, readContent);
            assertTrue(endTime - startTime < 2000, "大文件操作耗时过长");
        }
        
        @Test
        @DisplayName("异常情况处理测试")
        void testExceptionHandling() {
            // 测试 repeat 方法的边界情况
            assertThrows(IllegalArgumentException.class, () -> {
                "test".repeat(-1);
            });
            
            // 测试文件操作异常
            Path nonExistentFile = tempDir.resolve("non-existent/file.txt");
            assertThrows(IOException.class, () -> {
                Files.readString(nonExistentFile);
            });
        }
        
        @Test
        @DisplayName("Unicode 字符处理测试")
        void testUnicodeCharacterHandling() {
            // 测试各种 Unicode 字符
            String unicode = "🚀 Java 11 ✨ 新特性 🎉";
            
            assertFalse(unicode.isBlank());
            assertEquals(unicode, unicode.strip()); // 没有前后空白
            
            // 测试 Unicode 空白字符
            String unicodeSpaces = "\u2000\u2001\u2002\u2003";
            assertTrue(unicodeSpaces.isBlank());
            assertEquals("", unicodeSpaces.strip());
            
            // 测试混合内容
            String mixed = unicodeSpaces + unicode + unicodeSpaces;
            assertEquals(unicode, mixed.strip());
        }
    }
}