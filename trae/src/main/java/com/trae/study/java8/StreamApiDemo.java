package com.trae.study.java8;

import com.trae.study.dto.BenchmarkResultDTO;
import com.trae.study.util.StopWatchUtil;

import java.util.*;
import java.util.function.Function;
import java.util.stream.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

/**
 * Stream API 演示
 * 展示 Java 8 中 Stream API 的各种操作和用法
 * 
 * @author trae
 * @since 2024
 */
public class StreamApiDemo {
    
    /**
     * 学生信息类，用于演示复杂对象的 Stream 操作
     */
    public static class Student {
        private String name;
        private int age;
        private String gender;
        private String major;
        private double score;
        
        public Student(String name, int age, String gender, String major, double score) {
            this.name = name;
            this.age = age;
            this.gender = gender;
            this.major = major;
            this.score = score;
        }
        
        // Getters
        public String getName() { return name; }
        public int getAge() { return age; }
        public String getGender() { return gender; }
        public String getMajor() { return major; }
        public double getScore() { return score; }
        
        @Override
        public String toString() {
            return String.format("Student{name='%s', age=%d, gender='%s', major='%s', score=%.1f}", 
                               name, age, gender, major, score);
        }
    }
    
    /**
     * 创建测试数据
     */
    private List<Student> createStudentData() {
        return Arrays.asList(
            new Student("Alice", 20, "女", "计算机科学", 85.5),
            new Student("Bob", 22, "男", "数学", 92.0),
            new Student("Charlie", 19, "男", "物理", 78.5),
            new Student("Diana", 21, "女", "计算机科学", 88.0),
            new Student("Eve", 20, "女", "数学", 95.5),
            new Student("Frank", 23, "男", "物理", 82.0),
            new Student("Grace", 19, "女", "计算机科学", 90.5),
            new Student("Henry", 22, "男", "数学", 87.5),
            new Student("Ivy", 20, "女", "物理", 91.0),
            new Student("Jack", 21, "男", "计算机科学", 83.5)
        );
    }
    
    /**
     * 演示 Stream 的创建方式
     */
    public void demonstrateStreamCreation() {
        System.out.println("=== Stream 创建方式 ===");
        
        // 1. 从集合创建
        List<String> list = Arrays.asList("a", "b", "c");
        Stream<String> streamFromList = list.stream();
        System.out.println("从集合创建: " + streamFromList.collect(Collectors.toList()));
        
        // 2. 从数组创建
        String[] array = {"x", "y", "z"};
        Stream<String> streamFromArray = Arrays.stream(array);
        System.out.println("从数组创建: " + streamFromArray.collect(Collectors.toList()));
        
        // 3. 使用 Stream.of()
        Stream<Integer> streamOf = Stream.of(1, 2, 3, 4, 5);
        System.out.println("使用 Stream.of(): " + streamOf.collect(Collectors.toList()));
        
        // 4. 使用 Stream.generate() 创建无限流
        Stream<Double> randomStream = Stream.generate(Math::random).limit(5);
        System.out.println("无限流（限制5个）: " + 
            randomStream.map(d -> String.format("%.2f", d)).collect(Collectors.toList()));
        
        // 5. 使用 Stream.iterate() 创建无限流
        Stream<Integer> iterateStream = Stream.iterate(0, n -> n + 2).limit(5);
        System.out.println("迭代流: " + iterateStream.collect(Collectors.toList()));
        
        // 6. 使用 IntStream.range()
        IntStream rangeStream = IntStream.range(1, 6);
        System.out.println("范围流: " + rangeStream.boxed().collect(Collectors.toList()));
        
        // 7. 空流
        Stream<String> emptyStream = Stream.empty();
        System.out.println("空流大小: " + emptyStream.count());
    }
    
    /**
     * 演示中间操作（Intermediate Operations）
     */
    public void demonstrateIntermediateOperations() {
        System.out.println("\n=== 中间操作 ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // filter - 过滤
        List<Integer> evenNumbers = numbers.stream()
                                          .filter(n -> n % 2 == 0)
                                          .collect(Collectors.toList());
        System.out.println("过滤偶数: " + evenNumbers);
        
        // map - 映射
        List<Integer> squares = numbers.stream()
                                      .map(n -> n * n)
                                      .collect(Collectors.toList());
        System.out.println("平方映射: " + squares);
        
        // flatMap - 扁平化映射
        List<List<String>> nestedList = Arrays.asList(
            Arrays.asList("a", "b"),
            Arrays.asList("c", "d"),
            Arrays.asList("e", "f")
        );
        List<String> flatList = nestedList.stream()
                                         .flatMap(List::stream)
                                         .collect(Collectors.toList());
        System.out.println("扁平化: " + flatList);
        
        // distinct - 去重
        List<Integer> duplicates = Arrays.asList(1, 2, 2, 3, 3, 3, 4, 4, 5);
        List<Integer> unique = duplicates.stream()
                                       .distinct()
                                       .collect(Collectors.toList());
        System.out.println("去重: " + unique);
        
        // sorted - 排序
        List<String> words = Arrays.asList("banana", "apple", "cherry", "date");
        List<String> sorted = words.stream()
                                  .sorted()
                                  .collect(Collectors.toList());
        System.out.println("排序: " + sorted);
        
        // 自定义排序
        List<String> sortedByLength = words.stream()
                                          .sorted(Comparator.comparing(String::length))
                                          .collect(Collectors.toList());
        System.out.println("按长度排序: " + sortedByLength);
        
        // limit - 限制
        List<Integer> limited = numbers.stream()
                                      .limit(5)
                                      .collect(Collectors.toList());
        System.out.println("限制前5个: " + limited);
        
        // skip - 跳过
        List<Integer> skipped = numbers.stream()
                                      .skip(5)
                                      .collect(Collectors.toList());
        System.out.println("跳过前5个: " + skipped);
        
        // peek - 查看（用于调试）
        List<Integer> peeked = numbers.stream()
                                     .filter(n -> n > 5)
                                     .peek(n -> System.out.println("处理中: " + n))
                                     .map(n -> n * 2)
                                     .collect(Collectors.toList());
        System.out.println("peek 结果: " + peeked);
    }
    
    /**
     * 演示终止操作（Terminal Operations）
     */
    public void demonstrateTerminalOperations() {
        System.out.println("\n=== 终止操作 ===");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // forEach - 遍历
        System.out.print("forEach 遍历: ");
        numbers.stream().filter(n -> n <= 5).forEach(n -> System.out.print(n + " "));
        System.out.println();
        
        // collect - 收集
        Set<Integer> evenSet = numbers.stream()
                                     .filter(n -> n % 2 == 0)
                                     .collect(Collectors.toSet());
        System.out.println("收集到 Set: " + evenSet);
        
        // reduce - 归约
        Optional<Integer> sum = numbers.stream().reduce((a, b) -> a + b);
        System.out.println("归约求和: " + sum.orElse(0));
        
        // 带初始值的 reduce
        Integer product = numbers.stream().reduce(1, (a, b) -> a * b);
        System.out.println("归约求积: " + product);
        
        // count - 计数
        long count = numbers.stream().filter(n -> n > 5).count();
        System.out.println("大于5的数量: " + count);
        
        // anyMatch, allMatch, noneMatch - 匹配
        boolean anyEven = numbers.stream().anyMatch(n -> n % 2 == 0);
        boolean allPositive = numbers.stream().allMatch(n -> n > 0);
        boolean noneNegative = numbers.stream().noneMatch(n -> n < 0);
        System.out.println("是否有偶数: " + anyEven);
        System.out.println("是否全为正数: " + allPositive);
        System.out.println("是否没有负数: " + noneNegative);
        
        // findFirst, findAny - 查找
        Optional<Integer> first = numbers.stream().filter(n -> n > 5).findFirst();
        Optional<Integer> any = numbers.stream().filter(n -> n > 5).findAny();
        System.out.println("第一个大于5的数: " + first.orElse(-1));
        System.out.println("任意一个大于5的数: " + any.orElse(-1));
        
        // min, max - 最值
        Optional<Integer> min = numbers.stream().min(Integer::compareTo);
        Optional<Integer> max = numbers.stream().max(Integer::compareTo);
        System.out.println("最小值: " + min.orElse(-1));
        System.out.println("最大值: " + max.orElse(-1));
    }
    
    /**
     * 演示复杂的 Stream 操作
     */
    public void demonstrateComplexOperations() {
        System.out.println("\n=== 复杂 Stream 操作 ===");
        
        List<Student> students = createStudentData();
        
        // 1. 按专业分组
        Map<String, List<Student>> studentsByMajor = students.stream()
            .collect(Collectors.groupingBy(Student::getMajor));
        System.out.println("按专业分组:");
        studentsByMajor.forEach((major, studentList) -> {
            System.out.println("  " + major + ": " + studentList.size() + "人");
        });
        
        // 2. 按性别统计平均分
        Map<String, Double> avgScoreByGender = students.stream()
            .collect(Collectors.groupingBy(
                Student::getGender,
                Collectors.averagingDouble(Student::getScore)
            ));
        System.out.println("按性别统计平均分:");
        avgScoreByGender.forEach((gender, avgScore) -> {
            System.out.println("  " + gender + ": " + String.format("%.2f", avgScore));
        });
        
        // 3. 找出每个专业分数最高的学生
        Map<String, Optional<Student>> topStudentByMajor = students.stream()
            .collect(Collectors.groupingBy(
                Student::getMajor,
                Collectors.maxBy(Comparator.comparing(Student::getScore))
            ));
        System.out.println("每个专业分数最高的学生:");
        topStudentByMajor.forEach((major, student) -> {
            if (student.isPresent()) {
                System.out.println("  " + major + ": " + student.get().getName() + 
                                 " (" + student.get().getScore() + "分)");
            }
        });
        
        // 4. 按分数段统计人数
        Map<String, Long> countByScoreRange = students.stream()
            .collect(Collectors.groupingBy(
                student -> {
                    double score = student.getScore();
                    if (score >= 90) return "优秀(90+)";
                    else if (score >= 80) return "良好(80-89)";
                    else if (score >= 70) return "中等(70-79)";
                    else return "及格以下(<70)";
                },
                Collectors.counting()
            ));
        System.out.println("按分数段统计人数:");
        countByScoreRange.forEach((range, count) -> {
            System.out.println("  " + range + ": " + count + "人");
        });
        
        // 5. 多级排序：先按分数降序，再按年龄升序
        List<Student> sortedStudents = students.stream()
            .sorted(Comparator.comparing(Student::getScore).reversed()
                             .thenComparing(Student::getAge))
            .collect(Collectors.toList());
        System.out.println("多级排序结果（前5名）:");
        sortedStudents.stream().limit(5).forEach(student -> {
            System.out.println("  " + student.getName() + ": " + 
                             student.getScore() + "分, " + student.getAge() + "岁");
        });
        
        // 6. 分区：分数是否大于等于85
        Map<Boolean, List<Student>> partitioned = students.stream()
            .collect(Collectors.partitioningBy(student -> student.getScore() >= 85));
        System.out.println("分区结果:");
        System.out.println("  高分组(>=85): " + partitioned.get(true).size() + "人");
        System.out.println("  普通组(<85): " + partitioned.get(false).size() + "人");
    }
    
    /**
     * 演示并行流
     */
    public void demonstrateParallelStreams() {
        System.out.println("\n=== 并行流演示 ===");
        
        List<Integer> largeList = IntStream.range(1, 1000000)
                                          .boxed()
                                          .collect(Collectors.toList());
        
        // 顺序流
        long sequentialTime = StopWatchUtil.measureTask("顺序流求和", () -> {
            return largeList.stream()
                           .filter(n -> n % 2 == 0)
                           .mapToLong(n -> n * n)
                           .sum();
        });
        
        // 并行流
        long parallelTime = StopWatchUtil.measureTask("并行流求和", () -> {
            return largeList.parallelStream()
                           .filter(n -> n % 2 == 0)
                           .mapToLong(n -> n * n)
                           .sum();
        });
        
        System.out.println("顺序流耗时: " + sequentialTime + "ms");
        System.out.println("并行流耗时: " + parallelTime + "ms");
        System.out.println("性能提升: " + String.format("%.2f", (double)sequentialTime / parallelTime) + "倍");
        
        // 并行流的线程安全问题演示
        System.out.println("\n并行流线程信息:");
        Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8).parallelStream()
              .forEach(n -> System.out.println("处理 " + n + " 的线程: " + 
                                              Thread.currentThread().getName()));
    }
    
    /**
     * 演示 Collectors 的高级用法
     */
    public void demonstrateAdvancedCollectors() {
        System.out.println("\n=== 高级 Collectors ===");
        
        List<Student> students = createStudentData();
        
        // 1. 统计信息
        DoubleSummaryStatistics scoreStats = students.stream()
            .collect(Collectors.summarizingDouble(Student::getScore));
        System.out.println("分数统计信息:");
        System.out.println("  平均分: " + String.format("%.2f", scoreStats.getAverage()));
        System.out.println("  最高分: " + scoreStats.getMax());
        System.out.println("  最低分: " + scoreStats.getMin());
        System.out.println("  总人数: " + scoreStats.getCount());
        System.out.println("  分数总和: " + scoreStats.getSum());
        
        // 2. 字符串连接
        String allNames = students.stream()
            .map(Student::getName)
            .collect(Collectors.joining(", ", "学生名单: [", "]"));
        System.out.println(allNames);
        
        // 3. 自定义 Collector
        String customResult = students.stream()
            .collect(Collector.of(
                StringBuilder::new,  // supplier
                (sb, student) -> {   // accumulator
                    if (sb.length() > 0) sb.append("; ");
                    sb.append(student.getName()).append("(").append(student.getScore()).append(")");
                },
                (sb1, sb2) -> sb1.append(sb2),  // combiner
                StringBuilder::toString         // finisher
            ));
        System.out.println("自定义收集器结果: " + customResult);
        
        // 4. 多级分组
        Map<String, Map<String, List<Student>>> multiGrouping = students.stream()
            .collect(Collectors.groupingBy(
                Student::getMajor,
                Collectors.groupingBy(Student::getGender)
            ));
        System.out.println("多级分组（专业->性别）:");
        multiGrouping.forEach((major, genderMap) -> {
            System.out.println("  " + major + ":");
            genderMap.forEach((gender, studentList) -> {
                System.out.println("    " + gender + ": " + studentList.size() + "人");
            });
        });
    }
    
    /**
     * Stream 性能基准测试
     */
    public BenchmarkResultDTO benchmarkStreamPerformance() {
        System.out.println("\n=== Stream 性能基准测试 ===");
        
        List<Integer> data = IntStream.range(1, 1000000)
                                    .boxed()
                                    .collect(Collectors.toList());
        
        // 传统 for 循环
        long traditionalTime = StopWatchUtil.measureTask("传统for循环", () -> {
            List<Integer> result = new ArrayList<>();
            for (Integer num : data) {
                if (num % 2 == 0 && num > 100) {
                    result.add(num * num);
                }
            }
            return result.size();
        });
        
        // Stream API
        long streamTime = StopWatchUtil.measureTask("Stream API", () -> {
            return data.stream()
                      .filter(n -> n % 2 == 0)
                      .filter(n -> n > 100)
                      .map(n -> n * n)
                      .collect(Collectors.toList())
                      .size();
        });
        
        // 并行 Stream
        long parallelStreamTime = StopWatchUtil.measureTask("并行Stream", () -> {
            return data.parallelStream()
                      .filter(n -> n % 2 == 0)
                      .filter(n -> n > 100)
                      .map(n -> n * n)
                      .collect(Collectors.toList())
                      .size();
        });
        
        return BenchmarkResultDTO.builder()
                .testName("Stream API 性能对比")
                .description("传统循环 vs Stream API vs 并行Stream")
                .executionTimeMs(streamTime)
                .additionalMetrics(Map.of(
                    "传统for循环耗时(ms)", traditionalTime,
                    "Stream API耗时(ms)", streamTime,
                    "并行Stream耗时(ms)", parallelStreamTime,
                    "数据量", data.size(),
                    "Stream相对传统循环倍数", String.format("%.2f", (double)streamTime / traditionalTime),
                    "并行Stream相对传统循环倍数", String.format("%.2f", (double)parallelStreamTime / traditionalTime)
                ))
                .build();
    }
    
    /**
     * 综合演示方法
     */
    public void runAllDemonstrations() {
        System.out.println("Stream API 综合演示");
        System.out.println("=".repeat(50));
        
        demonstrateStreamCreation();
        demonstrateIntermediateOperations();
        demonstrateTerminalOperations();
        demonstrateComplexOperations();
        demonstrateParallelStreams();
        demonstrateAdvancedCollectors();
        
        BenchmarkResultDTO result = benchmarkStreamPerformance();
        System.out.println("\n性能测试结果:");
        System.out.println(result);
    }
    
    public static void main(String[] args) {
        new StreamApiDemo().runAllDemonstrations();
    }
}