package com.trae.study.java8;

import com.trae.study.dto.BenchmarkResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.function.*;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Lambda 表达式演示测试类
 * 验证 Lambda 表达式和函数式接口的各种用法
 * 
 * @author trae
 * @since 2024
 */
class LambdaDemoTest {
    
    private LambdaDemo lambdaDemo;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    
    @BeforeEach
    void setUp() {
        lambdaDemo = new LambdaDemo();
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }
    
    @Test
    @DisplayName("测试基础 Lambda 表达式语法")
    void testBasicLambda() {
        // 测试无参数 Lambda
        Runnable task = () -> System.out.println("测试任务");
        assertDoesNotThrow(task::run);
        
        // 测试单参数 Lambda
        Consumer<String> printer = msg -> System.out.println("消息: " + msg);
        assertDoesNotThrow(() -> printer.accept("测试消息"));
        
        // 测试多参数 Lambda
        BinaryOperator<Integer> adder = (a, b) -> a + b;
        assertEquals(30, adder.apply(10, 20));
        
        // 测试带代码块的 Lambda
        Function<String, String> processor = (input) -> {
            String trimmed = input.trim();
            String upperCase = trimmed.toUpperCase();
            return "处理结果: " + upperCase;
        };
        assertEquals("处理结果: HELLO", processor.apply("  hello  "));
    }
    
    @Test
    @DisplayName("测试内置函数式接口")
    void testFunctionalInterfaces() {
        // 测试 Function<T, R>
        Function<String, Integer> lengthCalculator = String::length;
        assertEquals(6, lengthCalculator.apply("Java 8"));
        
        // 测试 Predicate<T>
        Predicate<Integer> isEven = num -> num % 2 == 0;
        assertTrue(isEven.test(10));
        assertFalse(isEven.test(7));
        
        // 测试 Consumer<T>
        Consumer<List<String>> listPrinter = list -> {
            System.out.println("列表内容: " + String.join(", ", list));
        };
        assertDoesNotThrow(() -> listPrinter.accept(Arrays.asList("Apple", "Banana")));
        
        // 测试 Supplier<T>
        Supplier<String> timestampSupplier = () -> "当前时间戳: " + System.currentTimeMillis();
        String result = timestampSupplier.get();
        assertTrue(result.startsWith("当前时间戳: "));
        
        // 测试 UnaryOperator<T>
        UnaryOperator<String> toUpperCase = String::toUpperCase;
        assertEquals("HELLO", toUpperCase.apply("hello"));
        
        // 测试 BinaryOperator<T>
        BinaryOperator<String> concatenator = (s1, s2) -> s1 + " + " + s2;
        assertEquals("Java + Lambda", concatenator.apply("Java", "Lambda"));
    }
    
    @Test
    @DisplayName("测试方法引用")
    void testMethodReferences() {
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");
        
        // 测试静态方法引用
        Function<String, String> valueOf = String::valueOf;
        assertEquals("Alice", valueOf.apply("Alice"));
        
        // 测试实例方法引用
        String prefix = "Name: ";
        Function<String, String> prefixer = prefix::concat;
        assertEquals("Name: Alice", prefixer.apply("Alice"));
        
        // 测试类的实例方法引用
        Function<String, String> toUpperCase = String::toUpperCase;
        assertEquals("ALICE", toUpperCase.apply("alice"));
        
        // 测试构造器引用
        Supplier<List<String>> listSupplier = ArrayList::new;
        List<String> newList = listSupplier.get();
        assertNotNull(newList);
        assertTrue(newList.isEmpty());
        
        // 测试数组构造器引用
        Function<Integer, String[]> arrayCreator = String[]::new;
        String[] array = arrayCreator.apply(5);
        assertEquals(5, array.length);
    }
    
    @Test
    @DisplayName("测试 Lambda 在集合操作中的应用")
    void testLambdaWithCollections() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 测试过滤偶数
        List<Integer> evenNumbers = numbers.stream()
                                          .filter(n -> n % 2 == 0)
                                          .collect(java.util.stream.Collectors.toList());
        assertEquals(Arrays.asList(2, 4, 6, 8, 10), evenNumbers);
        
        // 测试映射平方
        List<Integer> squares = numbers.stream()
                                      .map(n -> n * n)
                                      .collect(java.util.stream.Collectors.toList());
        assertEquals(Arrays.asList(1, 4, 9, 16, 25, 36, 49, 64, 81, 100), squares);
        
        // 测试排序
        List<String> words = Arrays.asList("apple", "pie", "washington", "book");
        List<String> sortedByLength = words.stream()
                                          .sorted((s1, s2) -> Integer.compare(s1.length(), s2.length()))
                                          .collect(java.util.stream.Collectors.toList());
        assertEquals(Arrays.asList("pie", "book", "apple", "washington"), sortedByLength);
    }
    
    @Test
    @DisplayName("测试函数式接口组合")
    void testFunctionComposition() {
        // 测试 Function 组合
        Function<String, String> addPrefix = s -> "前缀_" + s;
        Function<String, String> addSuffix = s -> s + "_后缀";
        
        // 测试 compose
        Function<String, String> composed1 = addSuffix.compose(addPrefix);
        assertEquals("前缀_测试_后缀", composed1.apply("测试"));
        
        // 测试 andThen
        Function<String, String> composed2 = addPrefix.andThen(addSuffix);
        assertEquals("前缀_测试_后缀", composed2.apply("测试"));
        
        // 测试 Predicate 组合
        Predicate<Integer> isPositive = n -> n > 0;
        Predicate<Integer> isEven = n -> n % 2 == 0;
        
        // 测试 and
        Predicate<Integer> isPositiveAndEven = isPositive.and(isEven);
        assertTrue(isPositiveAndEven.test(4));
        assertFalse(isPositiveAndEven.test(-2));
        assertFalse(isPositiveAndEven.test(3));
        
        // 测试 or
        Predicate<Integer> isPositiveOrEven = isPositive.or(isEven);
        assertTrue(isPositiveOrEven.test(-2));  // 偶数
        assertTrue(isPositiveOrEven.test(3));   // 正数
        assertTrue(isPositiveOrEven.test(4));   // 正偶数
        assertFalse(isPositiveOrEven.test(-3)); // 负奇数
        
        // 测试 negate
        Predicate<Integer> isNotPositive = isPositive.negate();
        assertTrue(isNotPositive.test(-5));
        assertFalse(isNotPositive.test(5));
    }
    
    @Test
    @DisplayName("测试自定义函数式接口")
    void testCustomFunctionalInterface() {
        // 测试三参数函数接口
        LambdaDemo.TriFunction<Integer, Integer, Integer, Integer> sum3 = (a, b, c) -> a + b + c;
        assertEquals(6, sum3.apply(1, 2, 3));
        assertEquals("三参数函数接口", sum3.getDescription());
        
        // 测试字符串格式化函数
        LambdaDemo.TriFunction<String, String, String, String> formatter = 
            (template, arg1, arg2) -> String.format(template, arg1, arg2);
        String result = formatter.apply("Hello %s, welcome to %s!", "Alice", "Java 8");
        assertEquals("Hello Alice, welcome to Java 8!", result);
    }
    
    @Test
    @DisplayName("测试 Lambda 表达式性能基准")
    void testLambdaPerformanceBenchmark() {
        BenchmarkResultDTO result = lambdaDemo.benchmarkLambdaPerformance();
        
        assertNotNull(result);
        assertEquals("Lambda 表达式性能对比", result.getTestName());
        assertEquals("传统循环 vs Lambda Stream vs 并行 Stream", result.getDescription());
        assertTrue(result.getExecutionTimeMs() > 0);
        
        // 验证附加指标
        assertNotNull(result.getAdditionalMetrics());
        assertTrue(result.getAdditionalMetrics().containsKey("传统循环耗时(ms)"));
        assertTrue(result.getAdditionalMetrics().containsKey("Lambda Stream耗时(ms)"));
        assertTrue(result.getAdditionalMetrics().containsKey("并行Stream耗时(ms)"));
        assertTrue(result.getAdditionalMetrics().containsKey("数据量"));
        
        // 验证数据量
        assertEquals(1000000, result.getAdditionalMetrics().get("数据量"));
    }
    
    @Test
    @DisplayName("测试综合演示方法")
    void testRunAllDemonstrations() {
        assertDoesNotThrow(() -> lambdaDemo.runAllDemonstrations());
        
        String output = outputStream.toString();
        assertTrue(output.contains("Lambda 表达式和函数式接口综合演示"));
        assertTrue(output.contains("基础 Lambda 表达式语法"));
        assertTrue(output.contains("内置函数式接口"));
        assertTrue(output.contains("方法引用"));
        assertTrue(output.contains("Lambda 在集合操作中的应用"));
        assertTrue(output.contains("函数式接口组合"));
        assertTrue(output.contains("自定义函数式接口"));
        assertTrue(output.contains("性能测试结果"));
    }
    
    @Test
    @DisplayName("测试 Lambda 表达式的类型推断")
    void testLambdaTypeInference() {
        // 编译器可以推断参数类型
        Function<String, Integer> lengthFunc1 = (String s) -> s.length();  // 显式类型
        Function<String, Integer> lengthFunc2 = s -> s.length();           // 推断类型
        
        assertEquals(5, lengthFunc1.apply("hello"));
        assertEquals(5, lengthFunc2.apply("hello"));
        
        // 测试复杂类型推断
        java.util.Comparator<String> comparator1 = (s1, s2) -> s1.compareToIgnoreCase(s2);
        java.util.Comparator<String> comparator2 = String::compareToIgnoreCase;
        
        assertEquals(0, comparator1.compare("Hello", "HELLO"));
        assertEquals(0, comparator2.compare("Hello", "HELLO"));
    }
    
    @Test
    @DisplayName("测试 Lambda 表达式的变量捕获")
    void testLambdaVariableCapture() {
        // 测试 effectively final 变量捕获
        String prefix = "前缀: ";
        Function<String, String> addPrefix = s -> prefix + s;
        assertEquals("前缀: 测试", addPrefix.apply("测试"));
        
        // 测试局部变量捕获
        final int multiplier = 10;
        Function<Integer, Integer> multiply = n -> n * multiplier;
        assertEquals(50, multiply.apply(5));
        
        // 测试实例变量访问
        List<String> instanceList = Arrays.asList("a", "b", "c");
        Supplier<Integer> sizeSupplier = () -> instanceList.size();
        assertEquals(3, sizeSupplier.get());
    }
    
    @Test
    @DisplayName("测试边界条件")
    void testEdgeCases() {
        // 测试空集合
        List<Integer> emptyList = Arrays.asList();
        long count = emptyList.stream().filter(n -> n > 0).count();
        assertEquals(0, count);
        
        // 测试 null 处理
        Function<String, String> nullSafeToUpper = s -> s == null ? null : s.toUpperCase();
        assertNull(nullSafeToUpper.apply(null));
        assertEquals("HELLO", nullSafeToUpper.apply("hello"));
        
        // 测试异常处理
        Function<String, Integer> parseIntSafe = s -> {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                return 0;
            }
        };
        assertEquals(123, parseIntSafe.apply("123"));
        assertEquals(0, parseIntSafe.apply("abc"));
    }
    
    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}