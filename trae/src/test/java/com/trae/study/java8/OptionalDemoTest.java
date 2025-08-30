package com.trae.study.java8;

import com.trae.study.dto.BenchmarkResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Optional 演示测试类
 * 验证 Optional 的各种操作和用法
 * 
 * @author trae
 * @since 2024
 */
class OptionalDemoTest {
    
    private OptionalDemo optionalDemo;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    
    @BeforeEach
    void setUp() {
        optionalDemo = new OptionalDemo();
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }
    
    @Test
    @DisplayName("测试 Optional 创建方式")
    void testOptionalCreation() {
        // 测试 Optional.empty()
        Optional<String> empty = Optional.empty();
        assertTrue(empty.isEmpty());
        assertFalse(empty.isPresent());
        
        // 测试 Optional.of()
        Optional<String> nonNull = Optional.of("Hello");
        assertTrue(nonNull.isPresent());
        assertFalse(nonNull.isEmpty());
        assertEquals("Hello", nonNull.get());
        
        // 测试 Optional.of() 不能接受 null
        assertThrows(NullPointerException.class, () -> Optional.of(null));
        
        // 测试 Optional.ofNullable()
        Optional<String> nullable = Optional.ofNullable(null);
        assertTrue(nullable.isEmpty());
        
        Optional<String> notNullable = Optional.ofNullable("Not Null");
        assertTrue(notNullable.isPresent());
        assertEquals("Not Null", notNullable.get());
    }
    
    @Test
    @DisplayName("测试 Optional 基本操作")
    void testBasicOperations() {
        Optional<String> value = Optional.of("Hello");
        Optional<String> emptyValue = Optional.empty();
        
        // 测试 isPresent() 和 isEmpty()
        assertTrue(value.isPresent());
        assertFalse(value.isEmpty());
        assertFalse(emptyValue.isPresent());
        assertTrue(emptyValue.isEmpty());
        
        // 测试 get()
        assertEquals("Hello", value.get());
        assertThrows(NoSuchElementException.class, emptyValue::get);
        
        // 测试 orElse()
        assertEquals("Hello", value.orElse("Default"));
        assertEquals("Default", emptyValue.orElse("Default"));
        
        // 测试 orElseGet()
        assertEquals("Hello", value.orElseGet(() -> "From Supplier"));
        assertEquals("From Supplier", emptyValue.orElseGet(() -> "From Supplier"));
        
        // 测试 orElseThrow()
        assertEquals("Hello", value.orElseThrow(() -> new RuntimeException("No value")));
        assertThrows(RuntimeException.class, 
            () -> emptyValue.orElseThrow(() -> new RuntimeException("No value")));
    }
    
    @Test
    @DisplayName("测试 Optional 函数式操作")
    void testFunctionalOperations() {
        Optional<String> value = Optional.of("  Hello World  ");
        Optional<String> emptyValue = Optional.empty();
        
        // 测试 map()
        Optional<String> trimmed = value.map(String::trim);
        assertEquals("Hello World", trimmed.get());
        
        Optional<Integer> length = value.map(String::trim).map(String::length);
        assertEquals(11, length.get());
        
        // 空值的 map 操作
        Optional<String> emptyMapped = emptyValue.map(String::trim);
        assertTrue(emptyMapped.isEmpty());
        
        // 测试 filter()
        Optional<String> longValue = value.filter(s -> s.trim().length() > 10);
        assertTrue(longValue.isPresent());
        
        Optional<String> shortValue = value.filter(s -> s.trim().length() <= 5);
        assertTrue(shortValue.isEmpty());
        
        // 测试链式操作
        Optional<String> result = Optional.of("  java programming  ")
            .map(String::trim)
            .map(String::toUpperCase)
            .filter(s -> s.contains("JAVA"));
        
        assertTrue(result.isPresent());
        assertEquals("JAVA PROGRAMMING", result.get());
    }
    
    @Test
    @DisplayName("测试 Optional flatMap 操作")
    void testFlatMapOperations() {
        // 创建有地址的用户
        OptionalDemo.User userWithAddress = new OptionalDemo.User("Alice", "alice@example.com", 25,
            new OptionalDemo.Address("北京", "朝阳区", "100000"));
        
        // 创建无地址的用户
        OptionalDemo.User userWithoutAddress = new OptionalDemo.User("Bob", "bob@example.com", 30);
        
        Optional<OptionalDemo.User> user1 = Optional.of(userWithAddress);
        Optional<OptionalDemo.User> user2 = Optional.of(userWithoutAddress);
        
        // 测试 flatMap 获取用户城市
        Optional<String> city1 = user1.flatMap(OptionalDemo.User::getAddress)
                                     .map(OptionalDemo.Address::getCity);
        assertTrue(city1.isPresent());
        assertEquals("北京", city1.get());
        
        Optional<String> city2 = user2.flatMap(OptionalDemo.User::getAddress)
                                     .map(OptionalDemo.Address::getCity);
        assertTrue(city2.isEmpty());
        
        // 测试嵌套 Optional 的处理
        String cityName = user1.flatMap(OptionalDemo.User::getAddress)
                               .map(OptionalDemo.Address::getCity)
                               .orElse("未知城市");
        assertEquals("北京", cityName);
        
        String unknownCity = user2.flatMap(OptionalDemo.User::getAddress)
                                  .map(OptionalDemo.Address::getCity)
                                  .orElse("未知城市");
        assertEquals("未知城市", unknownCity);
    }
    
    @Test
    @DisplayName("测试 Optional 条件操作")
    void testConditionalOperations() {
        Optional<String> value = Optional.of("Hello");
        Optional<String> emptyValue = Optional.empty();
        
        // 测试 ifPresent()
        final boolean[] executed = {false};
        value.ifPresent(v -> executed[0] = true);
        assertTrue(executed[0]);
        
        executed[0] = false;
        emptyValue.ifPresent(v -> executed[0] = true);
        assertFalse(executed[0]);
        
        // 测试复杂条件处理
        Optional<OptionalDemo.User> user = Optional.of(new OptionalDemo.User("Alice", "alice@example.com", 25));
        
        final String[] result = {null};
        user.filter(u -> u.getAge() >= 18)
            .map(OptionalDemo.User::getName)
            .ifPresent(name -> result[0] = "成年用户: " + name);
        
        assertEquals("成年用户: Alice", result[0]);
        
        // 测试未成年用户
        Optional<OptionalDemo.User> youngUser = Optional.of(new OptionalDemo.User("Bob", "bob@example.com", 16));
        result[0] = null;
        
        youngUser.filter(u -> u.getAge() >= 18)
                 .map(OptionalDemo.User::getName)
                 .ifPresent(name -> result[0] = "成年用户: " + name);
        
        assertNull(result[0]); // 未执行
    }
    
    @Test
    @DisplayName("测试 Optional 与集合操作")
    void testOptionalWithCollections() {
        List<OptionalDemo.User> users = Arrays.asList(
            new OptionalDemo.User("Alice", "alice@example.com", 25),
            new OptionalDemo.User("Bob", "bob@example.com", 30),
            new OptionalDemo.User("Charlie", "charlie@example.com", 35),
            new OptionalDemo.User("Diana", "diana@example.com", 28)
        );
        
        // 测试 findFirst
        Optional<OptionalDemo.User> firstAdult = users.stream()
            .filter(user -> user.getAge() >= 30)
            .findFirst();
        
        assertTrue(firstAdult.isPresent());
        assertEquals("Bob", firstAdult.get().getName());
        
        // 测试 findAny
        Optional<OptionalDemo.User> anyYoung = users.stream()
            .filter(user -> user.getAge() < 30)
            .findAny();
        
        assertTrue(anyYoung.isPresent());
        assertTrue(anyYoung.get().getAge() < 30);
        
        // 测试 max 和 min
        Optional<OptionalDemo.User> oldest = users.stream()
            .max(Comparator.comparing(OptionalDemo.User::getAge));
        
        Optional<OptionalDemo.User> youngest = users.stream()
            .min(Comparator.comparing(OptionalDemo.User::getAge));
        
        assertTrue(oldest.isPresent());
        assertEquals("Charlie", oldest.get().getName());
        assertEquals(35, oldest.get().getAge());
        
        assertTrue(youngest.isPresent());
        assertEquals("Alice", youngest.get().getName());
        assertEquals(25, youngest.get().getAge());
        
        // 测试 reduce
        Optional<Integer> totalAge = users.stream()
            .map(OptionalDemo.User::getAge)
            .reduce(Integer::sum);
        
        assertTrue(totalAge.isPresent());
        assertEquals(118, totalAge.get()); // 25+30+35+28
        
        // 测试空集合
        List<OptionalDemo.User> emptyUsers = Arrays.asList();
        Optional<OptionalDemo.User> notFound = emptyUsers.stream()
            .findFirst();
        
        assertTrue(notFound.isEmpty());
    }
    
    @Test
    @DisplayName("测试 orElse vs orElseGet 性能差异")
    void testOrElseVsOrElseGet() {
        Optional<String> value = Optional.of("存在的值");
        Optional<String> emptyValue = Optional.empty();
        
        // 计数器用于验证方法调用次数
        final int[] callCount = {0};
        Supplier<String> expensiveSupplier = () -> {
            callCount[0]++;
            return "昂贵的默认值";
        };
        
        // 测试有值情况下的 orElse（总是执行）
        callCount[0] = 0;
        String result1 = value.orElse(expensiveSupplier.get());
        assertEquals("存在的值", result1);
        assertEquals(1, callCount[0]); // orElse 总是执行
        
        // 测试有值情况下的 orElseGet（不执行）
        callCount[0] = 0;
        String result2 = value.orElseGet(expensiveSupplier);
        assertEquals("存在的值", result2);
        assertEquals(0, callCount[0]); // orElseGet 不执行
        
        // 测试空值情况下的 orElse
        callCount[0] = 0;
        String result3 = emptyValue.orElse(expensiveSupplier.get());
        assertEquals("昂贵的默认值", result3);
        assertEquals(1, callCount[0]);
        
        // 测试空值情况下的 orElseGet
        callCount[0] = 0;
        String result4 = emptyValue.orElseGet(expensiveSupplier);
        assertEquals("昂贵的默认值", result4);
        assertEquals(1, callCount[0]);
    }
    
    @Test
    @DisplayName("测试 Optional 最佳实践")
    void testBestPractices() {
        // 1. 测试方法返回 Optional
        Optional<OptionalDemo.User> foundUser = findUserByEmail("alice@example.com");
        assertTrue(foundUser.isPresent());
        assertEquals("Alice", foundUser.get().getName());
        
        Optional<OptionalDemo.User> notFoundUser = findUserByEmail("notfound@example.com");
        assertTrue(notFoundUser.isEmpty());
        
        // 2. 测试链式调用
        String userInfo = Optional.of(new OptionalDemo.User("Bob", "bob@example.com", 30))
            .filter(user -> user.getAge() >= 18)
            .map(user -> user.getName().toUpperCase())
            .map(name -> "用户: " + name)
            .orElse("无效用户");
        
        assertEquals("用户: BOB", userInfo);
        
        // 测试未成年用户
        String invalidUserInfo = Optional.of(new OptionalDemo.User("Child", "child@example.com", 16))
            .filter(user -> user.getAge() >= 18)
            .map(user -> user.getName().toUpperCase())
            .map(name -> "用户: " + name)
            .orElse("无效用户");
        
        assertEquals("无效用户", invalidUserInfo);
        
        // 3. 测试嵌套 Optional 处理
        OptionalDemo.User userWithAddress = new OptionalDemo.User("Charlie", "charlie@example.com", 35,
            new OptionalDemo.Address("上海", "浦东新区", "200000"));
        
        String addressInfo = Optional.of(userWithAddress)
            .flatMap(OptionalDemo.User::getAddress)
            .map(addr -> addr.getCity() + ", " + addr.getStreet())
            .orElse("地址未知");
        
        assertEquals("上海, 浦东新区", addressInfo);
        
        // 测试无地址用户
        OptionalDemo.User userWithoutAddress = new OptionalDemo.User("David", "david@example.com", 40);
        
        String unknownAddress = Optional.of(userWithoutAddress)
            .flatMap(OptionalDemo.User::getAddress)
            .map(addr -> addr.getCity() + ", " + addr.getStreet())
            .orElse("地址未知");
        
        assertEquals("地址未知", unknownAddress);
    }
    
    @Test
    @DisplayName("测试 Optional 性能基准")
    void testOptionalPerformanceBenchmark() {
        BenchmarkResultDTO result = optionalDemo.benchmarkOptionalPerformance();
        
        assertNotNull(result);
        assertEquals("Optional 性能对比", result.getTestName());
        assertEquals("传统null检查 vs Optional", result.getDescription());
        assertTrue(result.getExecutionTimeMs() >= 0);
        
        // 验证附加指标
        assertNotNull(result.getAdditionalMetrics());
        assertTrue(result.getAdditionalMetrics().containsKey("传统null检查耗时(ms)"));
        assertTrue(result.getAdditionalMetrics().containsKey("Optional方式耗时(ms)"));
        assertTrue(result.getAdditionalMetrics().containsKey("Optional函数式耗时(ms)"));
        assertTrue(result.getAdditionalMetrics().containsKey("迭代次数"));
        assertTrue(result.getAdditionalMetrics().containsKey("测试数据量"));
        assertTrue(result.getAdditionalMetrics().containsKey("Optional相对传统倍数"));
        assertTrue(result.getAdditionalMetrics().containsKey("函数式相对传统倍数"));
        
        // 验证迭代次数
        assertEquals(1000000, result.getAdditionalMetrics().get("迭代次数"));
        assertEquals(5, result.getAdditionalMetrics().get("测试数据量"));
    }
    
    @Test
    @DisplayName("测试综合演示方法")
    void testRunAllDemonstrations() {
        assertDoesNotThrow(() -> optionalDemo.runAllDemonstrations());
        
        String output = outputStream.toString();
        assertTrue(output.contains("Optional 综合演示"));
        assertTrue(output.contains("Optional 创建方式演示"));
        assertTrue(output.contains("Optional 基本操作演示"));
        assertTrue(output.contains("Optional 函数式操作演示"));
        assertTrue(output.contains("Optional 条件操作演示"));
        assertTrue(output.contains("Optional 与集合操作演示"));
        assertTrue(output.contains("Optional 最佳实践演示"));
        assertTrue(output.contains("Optional 性能基准测试"));
        assertTrue(output.contains("Optional 演示完成"));
    }
    
    @Test
    @DisplayName("测试 Optional 边界条件")
    void testOptionalEdgeCases() {
        // 测试空字符串
        Optional<String> emptyString = Optional.of("");
        assertTrue(emptyString.isPresent());
        assertEquals("", emptyString.get());
        
        // 测试只包含空格的字符串
        Optional<String> whitespaceString = Optional.of("   ");
        Optional<String> trimmed = whitespaceString
            .map(String::trim)
            .filter(s -> !s.isEmpty());
        assertTrue(trimmed.isEmpty());
        
        // 测试数字边界
        Optional<Integer> zero = Optional.of(0);
        assertTrue(zero.isPresent());
        assertEquals(0, zero.get());
        
        Optional<Integer> negative = Optional.of(-1);
        assertTrue(negative.isPresent());
        assertEquals(-1, negative.get());
        
        // 测试布尔值
        Optional<Boolean> falseValue = Optional.of(false);
        assertTrue(falseValue.isPresent());
        assertFalse(falseValue.get());
        
        // 测试集合
        Optional<List<String>> emptyList = Optional.of(Arrays.asList());
        assertTrue(emptyList.isPresent());
        assertTrue(emptyList.get().isEmpty());
    }
    
    @Test
    @DisplayName("测试 Optional 类型转换")
    void testOptionalTypeConversion() {
        // 字符串到整数转换
        Optional<String> numberString = Optional.of("123");
        Optional<Integer> number = numberString.map(Integer::parseInt);
        assertTrue(number.isPresent());
        assertEquals(123, number.get());
        
        // 无效数字字符串
        Optional<String> invalidString = Optional.of("abc");
        assertThrows(NumberFormatException.class, () -> {
            invalidString.map(Integer::parseInt).get();
        });
        
        // 安全的数字转换
        Optional<Integer> safeNumber = parseIntSafely("123");
        assertTrue(safeNumber.isPresent());
        assertEquals(123, safeNumber.get());
        
        Optional<Integer> invalidNumber = parseIntSafely("abc");
        assertTrue(invalidNumber.isEmpty());
        
        // 对象属性提取
        Optional<OptionalDemo.User> user = Optional.of(new OptionalDemo.User("Alice", "alice@example.com", 25));
        Optional<String> userName = user.map(OptionalDemo.User::getName);
        Optional<String> userEmail = user.map(OptionalDemo.User::getEmail);
        Optional<Integer> userAge = user.map(OptionalDemo.User::getAge);
        
        assertTrue(userName.isPresent());
        assertEquals("Alice", userName.get());
        assertTrue(userEmail.isPresent());
        assertEquals("alice@example.com", userEmail.get());
        assertTrue(userAge.isPresent());
        assertEquals(25, userAge.get());
    }
    
    @Test
    @DisplayName("测试 Optional 与异常处理")
    void testOptionalWithExceptionHandling() {
        // 测试 orElseThrow 的不同异常类型
        Optional<String> value = Optional.of("test");
        Optional<String> emptyValue = Optional.empty();
        
        // 成功情况
        assertEquals("test", value.orElseThrow());
        assertEquals("test", value.orElseThrow(() -> new RuntimeException("Should not throw")));
        
        // 异常情况
        assertThrows(NoSuchElementException.class, emptyValue::orElseThrow);
        
        assertThrows(IllegalStateException.class, 
            () -> emptyValue.orElseThrow(() -> new IllegalStateException("Custom exception")));
        
        assertThrows(RuntimeException.class,
            () -> emptyValue.orElseThrow(() -> new RuntimeException("Runtime exception")));
        
        // 测试异常消息
        RuntimeException exception = assertThrows(RuntimeException.class,
            () -> emptyValue.orElseThrow(() -> new RuntimeException("Value is missing")));
        assertEquals("Value is missing", exception.getMessage());
    }
    
    /**
     * 辅助方法：根据邮箱查找用户
     */
    private Optional<OptionalDemo.User> findUserByEmail(String email) {
        Map<String, OptionalDemo.User> userDatabase = Map.of(
            "alice@example.com", new OptionalDemo.User("Alice", "alice@example.com", 25),
            "bob@example.com", new OptionalDemo.User("Bob", "bob@example.com", 30)
        );
        
        return Optional.ofNullable(userDatabase.get(email));
    }
    
    /**
     * 辅助方法：安全的整数解析
     */
    private Optional<Integer> parseIntSafely(String str) {
        try {
            return Optional.of(Integer.parseInt(str));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }
    
    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}