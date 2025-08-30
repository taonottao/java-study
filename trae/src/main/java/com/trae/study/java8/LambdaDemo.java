package com.trae.study.java8;

import com.trae.study.dto.BenchmarkResultDTO;
import com.trae.study.util.StopWatchUtil;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * Lambda 表达式和函数式接口演示
 * 展示 Java 8 中 Lambda 表达式的各种用法和内置函数式接口
 * 
 * @author trae
 * @since 2024
 */
public class LambdaDemo {
    
    /**
     * 演示基础 Lambda 表达式语法
     */
    public void demonstrateBasicLambda() {
        System.out.println("=== 基础 Lambda 表达式语法 ===");
        
        // 1. 无参数 Lambda
        Runnable task = () -> System.out.println("执行任务");
        task.run();
        
        // 2. 单参数 Lambda（可省略括号）
        Consumer<String> printer = msg -> System.out.println("消息: " + msg);
        printer.accept("Hello Lambda");
        
        // 3. 多参数 Lambda
        BinaryOperator<Integer> adder = (a, b) -> a + b;
        System.out.println("加法结果: " + adder.apply(10, 20));
        
        // 4. 带代码块的 Lambda
        Function<String, String> processor = (input) -> {
            String trimmed = input.trim();
            String upperCase = trimmed.toUpperCase();
            return "处理结果: " + upperCase;
        };
        System.out.println(processor.apply("  hello world  "));
    }
    
    /**
     * 演示内置函数式接口
     */
    public void demonstrateFunctionalInterfaces() {
        System.out.println("\n=== 内置函数式接口 ===");
        
        // Function<T, R> - 接受一个参数，返回一个结果
        Function<String, Integer> lengthCalculator = String::length;
        System.out.println("字符串长度: " + lengthCalculator.apply("Java 8"));
        
        // Predicate<T> - 接受一个参数，返回 boolean
        Predicate<Integer> isEven = num -> num % 2 == 0;
        System.out.println("10 是偶数: " + isEven.test(10));
        System.out.println("7 是偶数: " + isEven.test(7));
        
        // Consumer<T> - 接受一个参数，无返回值
        Consumer<List<String>> listPrinter = list -> {
            System.out.println("列表内容: " + String.join(", ", list));
        };
        listPrinter.accept(Arrays.asList("Apple", "Banana", "Cherry"));
        
        // Supplier<T> - 无参数，返回一个结果
        Supplier<String> timestampSupplier = () -> "当前时间戳: " + System.currentTimeMillis();
        System.out.println(timestampSupplier.get());
        
        // UnaryOperator<T> - Function<T, T> 的特殊形式
        UnaryOperator<String> toUpperCase = String::toUpperCase;
        System.out.println("转大写: " + toUpperCase.apply("hello"));
        
        // BinaryOperator<T> - BiFunction<T, T, T> 的特殊形式
        BinaryOperator<String> concatenator = (s1, s2) -> s1 + " + " + s2;
        System.out.println("字符串连接: " + concatenator.apply("Java", "Lambda"));
    }
    
    /**
     * 演示方法引用的各种形式
     */
    public void demonstrateMethodReferences() {
        System.out.println("\n=== 方法引用 ===");
        
        List<String> names = Arrays.asList("Alice", "Bob", "Charlie", "David");
        
        // 1. 静态方法引用 - ClassName::staticMethod
        names.stream()
             .map(String::valueOf)  // 等价于 s -> String.valueOf(s)
             .forEach(System.out::println);
        
        // 2. 实例方法引用 - instance::instanceMethod
        String prefix = "Name: ";
        Function<String, String> prefixer = prefix::concat;  // 等价于 s -> prefix.concat(s)
        System.out.println(prefixer.apply("Alice"));
        
        // 3. 类的实例方法引用 - ClassName::instanceMethod
        names.stream()
             .map(String::toUpperCase)  // 等价于 s -> s.toUpperCase()
             .forEach(System.out::println);
        
        // 4. 构造器引用 - ClassName::new
        Supplier<List<String>> listSupplier = ArrayList::new;  // 等价于 () -> new ArrayList<>()
        List<String> newList = listSupplier.get();
        System.out.println("新建列表: " + newList);
        
        // 数组构造器引用
        Function<Integer, String[]> arrayCreator = String[]::new;  // 等价于 size -> new String[size]
        String[] array = arrayCreator.apply(5);
        System.out.println("数组长度: " + array.length);
    }
    
    /**
     * 演示 Lambda 表达式在集合操作中的应用
     */
    public void demonstrateLambdaWithCollections() {
        System.out.println("\n=== Lambda 在集合操作中的应用 ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 过滤偶数
        List<Integer> evenNumbers = numbers.stream()
                                          .filter(n -> n % 2 == 0)
                                          .collect(Collectors.toList());
        System.out.println("偶数: " + evenNumbers);
        
        // 映射平方
        List<Integer> squares = numbers.stream()
                                      .map(n -> n * n)
                                      .collect(Collectors.toList());
        System.out.println("平方: " + squares);
        
        // 排序（自定义比较器）
        List<String> words = Arrays.asList("apple", "pie", "washington", "book");
        List<String> sortedByLength = words.stream()
                                          .sorted((s1, s2) -> Integer.compare(s1.length(), s2.length()))
                                          .collect(Collectors.toList());
        System.out.println("按长度排序: " + sortedByLength);
        
        // 使用方法引用排序
        List<String> sortedByLengthRef = words.stream()
                                            .sorted(Comparator.comparing(String::length))
                                            .collect(Collectors.toList());
        System.out.println("按长度排序（方法引用）: " + sortedByLengthRef);
    }
    
    /**
     * 演示函数式接口的组合
     */
    public void demonstrateFunctionComposition() {
        System.out.println("\n=== 函数式接口组合 ===");
        
        // Function 组合
        Function<String, String> addPrefix = s -> "前缀_" + s;
        Function<String, String> addSuffix = s -> s + "_后缀";
        
        // compose: 先执行参数函数，再执行当前函数
        Function<String, String> composed1 = addSuffix.compose(addPrefix);
        System.out.println("compose 结果: " + composed1.apply("测试"));
        
        // andThen: 先执行当前函数，再执行参数函数
        Function<String, String> composed2 = addPrefix.andThen(addSuffix);
        System.out.println("andThen 结果: " + composed2.apply("测试"));
        
        // Predicate 组合
        Predicate<Integer> isPositive = n -> n > 0;
        Predicate<Integer> isEven = n -> n % 2 == 0;
        
        // and: 逻辑与
        Predicate<Integer> isPositiveAndEven = isPositive.and(isEven);
        System.out.println("4 是正偶数: " + isPositiveAndEven.test(4));
        System.out.println("-2 是正偶数: " + isPositiveAndEven.test(-2));
        
        // or: 逻辑或
        Predicate<Integer> isPositiveOrEven = isPositive.or(isEven);
        System.out.println("-2 是正数或偶数: " + isPositiveOrEven.test(-2));
        
        // negate: 逻辑非
        Predicate<Integer> isNotPositive = isPositive.negate();
        System.out.println("-5 不是正数: " + isNotPositive.test(-5));
    }
    
    /**
     * 自定义函数式接口演示
     */
    @FunctionalInterface
    public interface TriFunction<T, U, V, R> {
        R apply(T t, U u, V v);
        
        // 可以有默认方法
        default String getDescription() {
            return "三参数函数接口";
        }
    }
    
    /**
     * 演示自定义函数式接口
     */
    public void demonstrateCustomFunctionalInterface() {
        System.out.println("\n=== 自定义函数式接口 ===");
        
        // 使用自定义的三参数函数接口
        TriFunction<Integer, Integer, Integer, Integer> sum3 = (a, b, c) -> a + b + c;
        System.out.println("三数之和: " + sum3.apply(1, 2, 3));
        System.out.println("接口描述: " + sum3.getDescription());
        
        // 字符串格式化函数
        TriFunction<String, String, String, String> formatter = 
            (template, arg1, arg2) -> String.format(template, arg1, arg2);
        String result = formatter.apply("Hello %s, welcome to %s!", "Alice", "Java 8");
        System.out.println("格式化结果: " + result);
    }
    
    /**
     * Lambda 表达式性能测试
     */
    public BenchmarkResultDTO benchmarkLambdaPerformance() {
        System.out.println("\n=== Lambda 表达式性能测试 ===");
        
        List<Integer> numbers = new ArrayList<>();
        for (int i = 0; i < 1000000; i++) {
            numbers.add(i);
        }
        
        // 传统方式
        long traditionalTime = StopWatchUtil.measureTask("传统循环", () -> {
            int sum = 0;
            for (Integer num : numbers) {
                if (num % 2 == 0) {
                    sum += num * num;
                }
            }
            return sum;
        });
        
        // Lambda + Stream 方式
        long lambdaTime = StopWatchUtil.measureTask("Lambda Stream", () -> {
            return numbers.stream()
                         .filter(n -> n % 2 == 0)
                         .mapToInt(n -> n * n)
                         .sum();
        });
        
        // 并行 Stream 方式
        long parallelTime = StopWatchUtil.measureTask("并行 Stream", () -> {
            return numbers.parallelStream()
                         .filter(n -> n % 2 == 0)
                         .mapToInt(n -> n * n)
                         .sum();
        });
        
        return BenchmarkResultDTO.builder()
                .testName("Lambda 表达式性能对比")
                .description("传统循环 vs Lambda Stream vs 并行 Stream")
                .executionTimeMs(traditionalTime)
                .additionalMetrics(Map.of(
                    "传统循环耗时(ms)", traditionalTime,
                    "Lambda Stream耗时(ms)", lambdaTime,
                    "并行Stream耗时(ms)", parallelTime,
                    "数据量", numbers.size()
                ))
                .build();
    }
    
    /**
     * 综合演示方法
     */
    public void runAllDemonstrations() {
        System.out.println("Lambda 表达式和函数式接口综合演示");
        System.out.println("=".repeat(50));
        
        demonstrateBasicLambda();
        demonstrateFunctionalInterfaces();
        demonstrateMethodReferences();
        demonstrateLambdaWithCollections();
        demonstrateFunctionComposition();
        demonstrateCustomFunctionalInterface();
        
        BenchmarkResultDTO result = benchmarkLambdaPerformance();
        System.out.println("\n性能测试结果:");
        System.out.println(result);
    }
    
    public static void main(String[] args) {
        new LambdaDemo().runAllDemonstrations();
    }
}