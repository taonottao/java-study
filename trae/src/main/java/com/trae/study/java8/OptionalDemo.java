package com.trae.study.java8;

import cn.hutool.core.util.StrUtil;
import com.trae.study.dto.BenchmarkResultDTO;
import com.trae.study.util.StopWatchUtil;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Optional 演示类
 * 展示 Java 8 Optional 的各种用法和最佳实践
 * 
 * Optional 是一个容器类，用于包装可能为 null 的值，
 * 避免 NullPointerException，提供更安全的编程方式
 * 
 * @author trae
 * @since 2024
 */
public class OptionalDemo {
    
    /**
     * 用户实体类
     */
    public static class User {
        private String name;
        private String email;
        private Integer age;
        private Address address;
        
        public User() {}
        
        public User(String name, String email, Integer age) {
            this.name = name;
            this.email = email;
            this.age = age;
        }
        
        public User(String name, String email, Integer age, Address address) {
            this.name = name;
            this.email = email;
            this.age = age;
            this.address = address;
        }
        
        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        
        public Optional<Address> getAddress() { 
            return Optional.ofNullable(address); 
        }
        public void setAddress(Address address) { this.address = address; }
        
        @Override
        public String toString() {
            return String.format("User{name='%s', email='%s', age=%d, address=%s}", 
                               name, email, age, address);
        }
    }
    
    /**
     * 地址实体类
     */
    public static class Address {
        private String city;
        private String street;
        private String zipCode;
        
        public Address() {}
        
        public Address(String city, String street, String zipCode) {
            this.city = city;
            this.street = street;
            this.zipCode = zipCode;
        }
        
        // Getters and Setters
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        
        public String getStreet() { return street; }
        public void setStreet(String street) { this.street = street; }
        
        public String getZipCode() { return zipCode; }
        public void setZipCode(String zipCode) { this.zipCode = zipCode; }
        
        @Override
        public String toString() {
            return String.format("Address{city='%s', street='%s', zipCode='%s'}", 
                               city, street, zipCode);
        }
    }
    
    /**
     * 演示 Optional 的创建方式
     */
    public void demonstrateOptionalCreation() {
        System.out.println("\n=== Optional 创建方式演示 ===");
        
        // 1. Optional.empty() - 创建空的 Optional
        Optional<String> empty = Optional.empty();
        System.out.println("空 Optional: " + empty);
        System.out.println("是否为空: " + empty.isEmpty());
        
        // 2. Optional.of() - 创建非空 Optional（值不能为 null）
        Optional<String> nonNull = Optional.of("Hello World");
        System.out.println("非空 Optional: " + nonNull);
        System.out.println("是否存在: " + nonNull.isPresent());
        
        // 3. Optional.ofNullable() - 创建可能为空的 Optional
        String nullableValue = null;
        Optional<String> nullable = Optional.ofNullable(nullableValue);
        System.out.println("可空 Optional: " + nullable);
        
        String nonNullValue = "Not Null";
        Optional<String> notNullable = Optional.ofNullable(nonNullValue);
        System.out.println("非空可空 Optional: " + notNullable);
        
        // 4. 从方法返回 Optional
        Optional<User> user = findUserById(1);
        System.out.println("查找用户结果: " + user);
        
        Optional<User> notFoundUser = findUserById(999);
        System.out.println("未找到用户: " + notFoundUser);
    }
    
    /**
     * 演示 Optional 的基本操作
     */
    public void demonstrateBasicOperations() {
        System.out.println("\n=== Optional 基本操作演示 ===");
        
        Optional<String> value = Optional.of("Hello Optional");
        Optional<String> emptyValue = Optional.empty();
        
        // 1. isPresent() 和 isEmpty()
        System.out.println("value.isPresent(): " + value.isPresent());
        System.out.println("value.isEmpty(): " + value.isEmpty());
        System.out.println("emptyValue.isPresent(): " + emptyValue.isPresent());
        System.out.println("emptyValue.isEmpty(): " + emptyValue.isEmpty());
        
        // 2. get() - 获取值（不安全，可能抛异常）
        if (value.isPresent()) {
            System.out.println("使用 get() 获取值: " + value.get());
        }
        
        // 3. orElse() - 提供默认值
        String result1 = value.orElse("默认值");
        String result2 = emptyValue.orElse("默认值");
        System.out.println("value.orElse(): " + result1);
        System.out.println("emptyValue.orElse(): " + result2);
        
        // 4. orElseGet() - 通过 Supplier 提供默认值
        String result3 = value.orElseGet(() -> "通过 Supplier 提供的默认值");
        String result4 = emptyValue.orElseGet(() -> "通过 Supplier 提供的默认值");
        System.out.println("value.orElseGet(): " + result3);
        System.out.println("emptyValue.orElseGet(): " + result4);
        
        // 5. orElseThrow() - 抛出异常
        try {
            String result5 = value.orElseThrow(() -> new RuntimeException("值不存在"));
            System.out.println("value.orElseThrow(): " + result5);
        } catch (RuntimeException e) {
            System.out.println("异常: " + e.getMessage());
        }
        
        try {
            emptyValue.orElseThrow(() -> new RuntimeException("值不存在"));
        } catch (RuntimeException e) {
            System.out.println("emptyValue.orElseThrow() 异常: " + e.getMessage());
        }
    }
    
    /**
     * 演示 Optional 的函数式操作
     */
    public void demonstrateFunctionalOperations() {
        System.out.println("\n=== Optional 函数式操作演示 ===");
        
        Optional<String> value = Optional.of("  Hello World  ");
        Optional<String> emptyValue = Optional.empty();
        
        // 1. map() - 转换值
        Optional<String> trimmed = value.map(String::trim);
        Optional<Integer> length = value.map(String::trim).map(String::length);
        System.out.println("原值: " + value);
        System.out.println("trim 后: " + trimmed);
        System.out.println("长度: " + length);
        
        // 空值的 map 操作
        Optional<String> emptyTrimmed = emptyValue.map(String::trim);
        System.out.println("空值 map 结果: " + emptyTrimmed);
        
        // 2. flatMap() - 扁平化操作
        Optional<User> user = Optional.of(new User("Alice", "alice@example.com", 25,
            new Address("北京", "朝阳区", "100000")));
        
        // 获取用户的城市
        Optional<String> city = user.flatMap(User::getAddress)
                                   .map(Address::getCity);
        System.out.println("用户城市: " + city.orElse("未知"));
        
        // 没有地址的用户
        Optional<User> userWithoutAddress = Optional.of(new User("Bob", "bob@example.com", 30));
        Optional<String> cityNotFound = userWithoutAddress.flatMap(User::getAddress)
                                                         .map(Address::getCity);
        System.out.println("无地址用户的城市: " + cityNotFound.orElse("未知"));
        
        // 3. filter() - 过滤值
        Optional<String> longValue = value.filter(s -> s.trim().length() > 10);
        Optional<String> shortValue = value.filter(s -> s.trim().length() <= 5);
        System.out.println("长度 > 10 的值: " + longValue);
        System.out.println("长度 <= 5 的值: " + shortValue);
        
        // 4. 链式操作
        String result = Optional.of("  java programming  ")
            .map(String::trim)
            .map(String::toUpperCase)
            .filter(s -> s.contains("JAVA"))
            .map(s -> "语言: " + s)
            .orElse("未找到匹配的语言");
        System.out.println("链式操作结果: " + result);
    }
    
    /**
     * 演示 Optional 的条件操作
     */
    public void demonstrateConditionalOperations() {
        System.out.println("\n=== Optional 条件操作演示 ===");
        
        Optional<String> value = Optional.of("Hello");
        Optional<String> emptyValue = Optional.empty();
        
        // 1. ifPresent() - 如果值存在则执行操作
        System.out.println("ifPresent() 演示:");
        value.ifPresent(v -> System.out.println("  值存在: " + v));
        emptyValue.ifPresent(v -> System.out.println("  这行不会执行"));
        
        // 2. ifPresentOrElse() - Java 9+ 的方法（这里模拟实现）
        System.out.println("ifPresentOrElse() 演示:");
        ifPresentOrElse(value, 
            v -> System.out.println("  值存在: " + v),
            () -> System.out.println("  值不存在"));
        
        ifPresentOrElse(emptyValue,
            v -> System.out.println("  这行不会执行"),
            () -> System.out.println("  值不存在，执行默认操作"));
        
        // 3. 复杂的条件处理
        Optional<User> user = findUserById(1);
        processUser(user);
        
        Optional<User> notFoundUser = findUserById(999);
        processUser(notFoundUser);
    }
    
    /**
     * 演示 Optional 在集合操作中的应用
     */
    public void demonstrateOptionalWithCollections() {
        System.out.println("\n=== Optional 与集合操作演示 ===");
        
        List<User> users = Arrays.asList(
            new User("Alice", "alice@example.com", 25),
            new User("Bob", "bob@example.com", 30),
            new User("Charlie", "charlie@example.com", 35),
            new User("Diana", "diana@example.com", 28)
        );
        
        // 1. 查找第一个满足条件的用户
        Optional<User> firstAdult = users.stream()
            .filter(user -> user.getAge() >= 30)
            .findFirst();
        
        System.out.println("第一个年龄 >= 30 的用户: " + 
            firstAdult.map(User::getName).orElse("未找到"));
        
        // 2. 查找任意一个满足条件的用户
        Optional<User> anyYoung = users.stream()
            .filter(user -> user.getAge() < 30)
            .findAny();
        
        System.out.println("任意一个年龄 < 30 的用户: " + 
            anyYoung.map(User::getName).orElse("未找到"));
        
        // 3. 获取最大值和最小值
        Optional<User> oldest = users.stream()
            .max(Comparator.comparing(User::getAge));
        
        Optional<User> youngest = users.stream()
            .min(Comparator.comparing(User::getAge));
        
        System.out.println("年龄最大的用户: " + 
            oldest.map(u -> u.getName() + "(" + u.getAge() + ")").orElse("未找到"));
        System.out.println("年龄最小的用户: " + 
            youngest.map(u -> u.getName() + "(" + u.getAge() + ")").orElse("未找到"));
        
        // 4. 归约操作
        Optional<Integer> totalAge = users.stream()
            .map(User::getAge)
            .reduce(Integer::sum);
        
        System.out.println("总年龄: " + totalAge.orElse(0));
        
        // 5. 过滤和转换
        List<String> emailsOfAdults = users.stream()
            .filter(user -> user.getAge() >= 30)
            .map(User::getEmail)
            .collect(Collectors.toList());
        
        System.out.println("成年用户邮箱: " + emailsOfAdults);
    }
    
    /**
     * 演示 Optional 的最佳实践
     */
    public void demonstrateBestPractices() {
        System.out.println("\n=== Optional 最佳实践演示 ===");
        
        // 1. 不要在字段中使用 Optional
        System.out.println("1. 字段中不使用 Optional（在实体类设计中体现）");
        
        // 2. 不要在方法参数中使用 Optional
        System.out.println("2. 方法参数不使用 Optional");
        processUserCorrect(new User("Alice", "alice@example.com", 25));
        processUserCorrect(null);
        
        // 3. 使用 Optional 作为返回值
        System.out.println("3. 方法返回值使用 Optional");
        Optional<String> config = getConfigValue("database.url");
        System.out.println("配置值: " + config.orElse("默认配置"));
        
        // 4. 避免使用 get() 方法
        System.out.println("4. 避免直接使用 get() 方法");
        Optional<String> value = Optional.of("test");
        // 不好的做法：value.get()
        // 好的做法：
        value.ifPresent(v -> System.out.println("  安全获取值: " + v));
        
        // 5. 使用 orElse vs orElseGet
        System.out.println("5. orElse vs orElseGet 的性能差异");
        demonstrateOrElseVsOrElseGet();
        
        // 6. 链式调用的优雅处理
        System.out.println("6. 链式调用处理复杂逻辑");
        String userInfo = Optional.of(new User("Bob", "bob@example.com", 30))
            .filter(user -> user.getAge() >= 18)
            .map(user -> user.getName().toUpperCase())
            .map(name -> "用户: " + name)
            .orElse("无效用户");
        System.out.println("  " + userInfo);
        
        // 7. 处理嵌套的 Optional
        System.out.println("7. 处理嵌套 Optional");
        Optional<User> userWithAddress = Optional.of(new User("Charlie", "charlie@example.com", 35,
            new Address("上海", "浦东新区", "200000")));
        
        String addressInfo = userWithAddress
            .flatMap(User::getAddress)
            .map(addr -> addr.getCity() + ", " + addr.getStreet())
            .orElse("地址未知");
        System.out.println("  地址信息: " + addressInfo);
    }
    
    /**
     * 演示 orElse 和 orElseGet 的性能差异
     */
    private void demonstrateOrElseVsOrElseGet() {
        Optional<String> value = Optional.of("存在的值");
        
        // orElse 总是会执行
        System.out.println("  orElse 演示（即使值存在也会执行）:");
        String result1 = value.orElse(expensiveOperation("orElse"));
        System.out.println("    结果: " + result1);
        
        // orElseGet 只在值不存在时执行
        System.out.println("  orElseGet 演示（值存在时不执行）:");
        String result2 = value.orElseGet(() -> expensiveOperation("orElseGet"));
        System.out.println("    结果: " + result2);
        
        // 空值情况
        Optional<String> emptyValue = Optional.empty();
        System.out.println("  空值情况下两者都会执行:");
        String result3 = emptyValue.orElse(expensiveOperation("orElse-empty"));
        String result4 = emptyValue.orElseGet(() -> expensiveOperation("orElseGet-empty"));
        System.out.println("    orElse 结果: " + result3);
        System.out.println("    orElseGet 结果: " + result4);
    }
    
    /**
     * 模拟耗时操作
     */
    private String expensiveOperation(String source) {
        System.out.println("    执行耗时操作: " + source);
        return "来自 " + source + " 的默认值";
    }
    
    /**
     * Optional 性能基准测试
     */
    public BenchmarkResultDTO benchmarkOptionalPerformance() {
        System.out.println("\n=== Optional 性能基准测试 ===");
        
        int iterations = 1_000_000;
        List<String> testData = Arrays.asList("value1", null, "value2", null, "value3");
        
        return StopWatchUtil.execute("Optional 性能对比", "传统null检查 vs Optional", () -> {
            
            // 1. 传统 null 检查
            long traditionalTime = StopWatchUtil.measureTime(() -> {
                int count = 0;
                for (int i = 0; i < iterations; i++) {
                    for (String data : testData) {
                        String result = processWithNullCheck(data);
                        if (result != null) {
                            count++;
                        }
                    }
                }
                return count;
            });
            
            // 2. Optional 方式
            long optionalTime = StopWatchUtil.measureTime(() -> {
                int count = 0;
                for (int i = 0; i < iterations; i++) {
                    for (String data : testData) {
                        Optional<String> result = processWithOptional(data);
                        if (result.isPresent()) {
                            count++;
                        }
                    }
                }
                return count;
            });
            
            // 3. Optional 函数式风格
            long functionalTime = StopWatchUtil.measureTime(() -> {
                int count = 0;
                for (int i = 0; i < iterations; i++) {
                    count += testData.stream()
                        .map(this::processWithOptional)
                        .mapToInt(opt -> opt.isPresent() ? 1 : 0)
                        .sum();
                }
                return count;
            });
            
            System.out.println("传统null检查耗时: " + traditionalTime + "ms");
            System.out.println("Optional方式耗时: " + optionalTime + "ms");
            System.out.println("Optional函数式耗时: " + functionalTime + "ms");
            System.out.println("Optional相对传统方式倍数: " + String.format("%.2f", (double) optionalTime / traditionalTime));
            System.out.println("函数式相对传统方式倍数: " + String.format("%.2f", (double) functionalTime / traditionalTime));
            
            Map<String, Object> metrics = new HashMap<>();
            metrics.put("传统null检查耗时(ms)", traditionalTime);
            metrics.put("Optional方式耗时(ms)", optionalTime);
            metrics.put("Optional函数式耗时(ms)", functionalTime);
            metrics.put("迭代次数", iterations);
            metrics.put("测试数据量", testData.size());
            metrics.put("Optional相对传统倍数", String.format("%.2f", (double) optionalTime / traditionalTime));
            metrics.put("函数式相对传统倍数", String.format("%.2f", (double) functionalTime / traditionalTime));
            
            return metrics;
        });
    }
    
    /**
     * 传统 null 检查方式处理
     */
    private String processWithNullCheck(String input) {
        if (input != null && !input.trim().isEmpty()) {
            return input.toUpperCase();
        }
        return null;
    }
    
    /**
     * Optional 方式处理
     */
    private Optional<String> processWithOptional(String input) {
        return Optional.ofNullable(input)
            .filter(s -> !s.trim().isEmpty())
            .map(String::toUpperCase);
    }
    
    /**
     * 模拟根据 ID 查找用户
     */
    private Optional<User> findUserById(int id) {
        Map<Integer, User> userDatabase = Map.of(
            1, new User("Alice", "alice@example.com", 25),
            2, new User("Bob", "bob@example.com", 30),
            3, new User("Charlie", "charlie@example.com", 35)
        );
        
        return Optional.ofNullable(userDatabase.get(id));
    }
    
    /**
     * 模拟获取配置值
     */
    private Optional<String> getConfigValue(String key) {
        Map<String, String> config = Map.of(
            "database.url", "jdbc:mysql://localhost:3306/test",
            "redis.host", "localhost",
            "redis.port", "6379"
        );
        
        return Optional.ofNullable(config.get(key));
    }
    
    /**
     * 正确的用户处理方法（不使用 Optional 作为参数）
     */
    private void processUserCorrect(User user) {
        if (user != null) {
            System.out.println("  处理用户: " + user.getName());
        } else {
            System.out.println("  用户为空，跳过处理");
        }
    }
    
    /**
     * 处理用户信息
     */
    private void processUser(Optional<User> userOpt) {
        userOpt.ifPresent(user -> {
            System.out.println("  找到用户: " + user.getName() + ", 邮箱: " + user.getEmail());
            
            // 处理用户地址
            user.getAddress().ifPresent(address -> {
                System.out.println("    地址: " + address.getCity() + ", " + address.getStreet());
            });
        });
        
        if (userOpt.isEmpty()) {
            System.out.println("  用户不存在");
        }
    }
    
    /**
     * 模拟 Java 9+ 的 ifPresentOrElse 方法
     */
    private <T> void ifPresentOrElse(Optional<T> optional, 
                                   java.util.function.Consumer<T> action, 
                                   Runnable emptyAction) {
        if (optional.isPresent()) {
            action.accept(optional.get());
        } else {
            emptyAction.run();
        }
    }
    
    /**
     * 运行所有演示
     */
    public void runAllDemonstrations() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Optional 综合演示");
        System.out.println("=".repeat(50));
        
        demonstrateOptionalCreation();
        demonstrateBasicOperations();
        demonstrateFunctionalOperations();
        demonstrateConditionalOperations();
        demonstrateOptionalWithCollections();
        demonstrateBestPractices();
        
        // 性能测试
        BenchmarkResultDTO result = benchmarkOptionalPerformance();
        System.out.println("\n性能测试结果: " + result.getTestName());
        System.out.println("总耗时: " + result.getExecutionTimeMs() + "ms");
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("Optional 演示完成");
        System.out.println("=".repeat(50));
    }
    
    /**
     * 主方法
     */
    public static void main(String[] args) {
        OptionalDemo demo = new OptionalDemo();
        demo.runAllDemonstrations();
    }
}