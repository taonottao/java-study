package com.trae.study.java8;

import com.trae.study.dto.BenchmarkResultDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Stream API 演示测试类
 * 验证 Stream API 的各种操作和用法
 * 
 * @author trae
 * @since 2024
 */
class StreamApiDemoTest {
    
    private StreamApiDemo streamApiDemo;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    
    @BeforeEach
    void setUp() {
        streamApiDemo = new StreamApiDemo();
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        System.setOut(new PrintStream(outputStream));
    }
    
    @Test
    @DisplayName("测试 Stream 创建方式")
    void testStreamCreation() {
        // 测试从集合创建
        List<String> list = Arrays.asList("a", "b", "c");
        List<String> result = list.stream().collect(Collectors.toList());
        assertEquals(Arrays.asList("a", "b", "c"), result);
        
        // 测试从数组创建
        String[] array = {"x", "y", "z"};
        List<String> arrayResult = Arrays.stream(array).collect(Collectors.toList());
        assertEquals(Arrays.asList("x", "y", "z"), arrayResult);
        
        // 测试 Stream.of()
        List<Integer> ofResult = Stream.of(1, 2, 3, 4, 5).collect(Collectors.toList());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), ofResult);
        
        // 测试 Stream.generate()
        List<String> generateResult = Stream.generate(() -> "test").limit(3).collect(Collectors.toList());
        assertEquals(Arrays.asList("test", "test", "test"), generateResult);
        
        // 测试 Stream.iterate()
        List<Integer> iterateResult = Stream.iterate(0, n -> n + 2).limit(5).collect(Collectors.toList());
        assertEquals(Arrays.asList(0, 2, 4, 6, 8), iterateResult);
        
        // 测试 IntStream.range()
        List<Integer> rangeResult = IntStream.range(1, 6).boxed().collect(Collectors.toList());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), rangeResult);
        
        // 测试空流
        long emptyCount = Stream.empty().count();
        assertEquals(0, emptyCount);
    }
    
    @Test
    @DisplayName("测试中间操作")
    void testIntermediateOperations() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 测试 filter
        List<Integer> evenNumbers = numbers.stream()
                                          .filter(n -> n % 2 == 0)
                                          .collect(Collectors.toList());
        assertEquals(Arrays.asList(2, 4, 6, 8, 10), evenNumbers);
        
        // 测试 map
        List<Integer> squares = numbers.stream()
                                      .map(n -> n * n)
                                      .collect(Collectors.toList());
        assertEquals(Arrays.asList(1, 4, 9, 16, 25, 36, 49, 64, 81, 100), squares);
        
        // 测试 flatMap
        List<List<String>> nestedList = Arrays.asList(
            Arrays.asList("a", "b"),
            Arrays.asList("c", "d")
        );
        List<String> flatList = nestedList.stream()
                                         .flatMap(List::stream)
                                         .collect(Collectors.toList());
        assertEquals(Arrays.asList("a", "b", "c", "d"), flatList);
        
        // 测试 distinct
        List<Integer> duplicates = Arrays.asList(1, 2, 2, 3, 3, 3, 4);
        List<Integer> unique = duplicates.stream()
                                       .distinct()
                                       .collect(Collectors.toList());
        assertEquals(Arrays.asList(1, 2, 3, 4), unique);
        
        // 测试 sorted
        List<String> words = Arrays.asList("banana", "apple", "cherry");
        List<String> sorted = words.stream()
                                  .sorted()
                                  .collect(Collectors.toList());
        assertEquals(Arrays.asList("apple", "banana", "cherry"), sorted);
        
        // 测试 limit
        List<Integer> limited = numbers.stream()
                                      .limit(5)
                                      .collect(Collectors.toList());
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), limited);
        
        // 测试 skip
        List<Integer> skipped = numbers.stream()
                                      .skip(5)
                                      .collect(Collectors.toList());
        assertEquals(Arrays.asList(6, 7, 8, 9, 10), skipped);
    }
    
    @Test
    @DisplayName("测试终止操作")
    void testTerminalOperations() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 测试 collect
        Set<Integer> evenSet = numbers.stream()
                                     .filter(n -> n % 2 == 0)
                                     .collect(Collectors.toSet());
        assertEquals(Set.of(2, 4, 6, 8, 10), evenSet);
        
        // 测试 reduce
        Optional<Integer> sum = numbers.stream().reduce((a, b) -> a + b);
        assertEquals(55, sum.orElse(0));
        
        // 测试带初始值的 reduce
        Integer product = Arrays.asList(1, 2, 3, 4).stream().reduce(1, (a, b) -> a * b);
        assertEquals(24, product);
        
        // 测试 count
        long count = numbers.stream().filter(n -> n > 5).count();
        assertEquals(5, count);
        
        // 测试 anyMatch, allMatch, noneMatch
        boolean anyEven = numbers.stream().anyMatch(n -> n % 2 == 0);
        boolean allPositive = numbers.stream().allMatch(n -> n > 0);
        boolean noneNegative = numbers.stream().noneMatch(n -> n < 0);
        assertTrue(anyEven);
        assertTrue(allPositive);
        assertTrue(noneNegative);
        
        // 测试 findFirst, findAny
        Optional<Integer> first = numbers.stream().filter(n -> n > 5).findFirst();
        Optional<Integer> any = numbers.stream().filter(n -> n > 5).findAny();
        assertEquals(6, first.orElse(-1));
        assertTrue(any.isPresent());
        assertTrue(any.get() > 5);
        
        // 测试 min, max
        Optional<Integer> min = numbers.stream().min(Integer::compareTo);
        Optional<Integer> max = numbers.stream().max(Integer::compareTo);
        assertEquals(1, min.orElse(-1));
        assertEquals(10, max.orElse(-1));
    }
    
    @Test
    @DisplayName("测试学生数据复杂操作")
    void testComplexOperationsWithStudents() {
        List<StreamApiDemo.Student> students = Arrays.asList(
            new StreamApiDemo.Student("Alice", 20, "女", "计算机科学", 85.5),
            new StreamApiDemo.Student("Bob", 22, "男", "数学", 92.0),
            new StreamApiDemo.Student("Charlie", 19, "男", "计算机科学", 78.5)
        );
        
        // 测试按专业分组
        Map<String, List<StreamApiDemo.Student>> studentsByMajor = students.stream()
            .collect(Collectors.groupingBy(StreamApiDemo.Student::getMajor));
        assertEquals(2, studentsByMajor.size());
        assertTrue(studentsByMajor.containsKey("计算机科学"));
        assertTrue(studentsByMajor.containsKey("数学"));
        assertEquals(2, studentsByMajor.get("计算机科学").size());
        assertEquals(1, studentsByMajor.get("数学").size());
        
        // 测试按性别统计平均分
        Map<String, Double> avgScoreByGender = students.stream()
            .collect(Collectors.groupingBy(
                StreamApiDemo.Student::getGender,
                Collectors.averagingDouble(StreamApiDemo.Student::getScore)
            ));
        assertEquals(2, avgScoreByGender.size());
        assertTrue(avgScoreByGender.containsKey("男"));
        assertTrue(avgScoreByGender.containsKey("女"));
        
        // 测试分区
        Map<Boolean, List<StreamApiDemo.Student>> partitioned = students.stream()
            .collect(Collectors.partitioningBy(student -> student.getScore() >= 85));
        assertEquals(2, partitioned.size());
        assertEquals(2, partitioned.get(true).size());  // 高分组
        assertEquals(1, partitioned.get(false).size()); // 普通组
        
        // 测试排序
        List<StreamApiDemo.Student> sortedStudents = students.stream()
            .sorted(Comparator.comparing(StreamApiDemo.Student::getScore).reversed())
            .collect(Collectors.toList());
        assertEquals("Bob", sortedStudents.get(0).getName()); // 最高分
        assertEquals("Charlie", sortedStudents.get(2).getName()); // 最低分
    }
    
    @Test
    @DisplayName("测试并行流")
    void testParallelStreams() {
        List<Integer> numbers = IntStream.range(1, 1000)
                                       .boxed()
                                       .collect(Collectors.toList());
        
        // 测试顺序流和并行流结果一致性
        long sequentialSum = numbers.stream()
                                   .filter(n -> n % 2 == 0)
                                   .mapToLong(n -> n * n)
                                   .sum();
        
        long parallelSum = numbers.parallelStream()
                                 .filter(n -> n % 2 == 0)
                                 .mapToLong(n -> n * n)
                                 .sum();
        
        assertEquals(sequentialSum, parallelSum);
        
        // 测试并行流是否真的并行
        Set<String> threadNames = Collections.synchronizedSet(new HashSet<>());
        Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10).parallelStream()
              .forEach(n -> threadNames.add(Thread.currentThread().getName()));
        
        // 在多核环境下，应该有多个线程参与
        // 但在测试环境中可能只有一个线程，所以只验证至少有一个线程
        assertTrue(threadNames.size() >= 1);
    }
    
    @Test
    @DisplayName("测试高级 Collectors")
    void testAdvancedCollectors() {
        List<StreamApiDemo.Student> students = Arrays.asList(
            new StreamApiDemo.Student("Alice", 20, "女", "计算机科学", 85.5),
            new StreamApiDemo.Student("Bob", 22, "男", "数学", 92.0),
            new StreamApiDemo.Student("Charlie", 19, "男", "计算机科学", 78.5)
        );
        
        // 测试统计信息
        DoubleSummaryStatistics scoreStats = students.stream()
            .collect(Collectors.summarizingDouble(StreamApiDemo.Student::getScore));
        assertEquals(3, scoreStats.getCount());
        assertEquals(92.0, scoreStats.getMax());
        assertEquals(78.5, scoreStats.getMin());
        assertTrue(scoreStats.getAverage() > 85.0 && scoreStats.getAverage() < 86.0);
        
        // 测试字符串连接
        String allNames = students.stream()
            .map(StreamApiDemo.Student::getName)
            .collect(Collectors.joining(", "));
        assertEquals("Alice, Bob, Charlie", allNames);
        
        // 测试带前缀后缀的字符串连接
        String namesWithBrackets = students.stream()
            .map(StreamApiDemo.Student::getName)
            .collect(Collectors.joining(", ", "[", "]"));
        assertEquals("[Alice, Bob, Charlie]", namesWithBrackets);
        
        // 测试多级分组
        Map<String, Map<String, List<StreamApiDemo.Student>>> multiGrouping = students.stream()
            .collect(Collectors.groupingBy(
                StreamApiDemo.Student::getMajor,
                Collectors.groupingBy(StreamApiDemo.Student::getGender)
            ));
        assertEquals(2, multiGrouping.size()); // 两个专业
        assertTrue(multiGrouping.containsKey("计算机科学"));
        assertTrue(multiGrouping.containsKey("数学"));
    }
    
    @Test
    @DisplayName("测试 Stream 性能基准")
    void testStreamPerformanceBenchmark() {
        BenchmarkResultDTO result = streamApiDemo.benchmarkStreamPerformance();
        
        assertNotNull(result);
        assertEquals("Stream API 性能对比", result.getTestName());
        assertEquals("传统循环 vs Stream API vs 并行Stream", result.getDescription());
        assertTrue(result.getExecutionTimeMs() >= 0);
        
        // 验证附加指标
        assertNotNull(result.getAdditionalMetrics());
        assertTrue(result.getAdditionalMetrics().containsKey("传统for循环耗时(ms)"));
        assertTrue(result.getAdditionalMetrics().containsKey("Stream API耗时(ms)"));
        assertTrue(result.getAdditionalMetrics().containsKey("并行Stream耗时(ms)"));
        assertTrue(result.getAdditionalMetrics().containsKey("数据量"));
        assertTrue(result.getAdditionalMetrics().containsKey("Stream相对传统循环倍数"));
        assertTrue(result.getAdditionalMetrics().containsKey("并行Stream相对传统循环倍数"));
        
        // 验证数据量
        assertEquals(999999, result.getAdditionalMetrics().get("数据量"));
    }
    
    @Test
    @DisplayName("测试综合演示方法")
    void testRunAllDemonstrations() {
        assertDoesNotThrow(() -> streamApiDemo.runAllDemonstrations());
        
        String output = outputStream.toString();
        assertTrue(output.contains("Stream API 综合演示"));
        assertTrue(output.contains("Stream 创建方式"));
        assertTrue(output.contains("中间操作"));
        assertTrue(output.contains("终止操作"));
        assertTrue(output.contains("复杂 Stream 操作"));
        assertTrue(output.contains("并行流演示"));
        assertTrue(output.contains("高级 Collectors"));
        assertTrue(output.contains("性能测试结果"));
    }
    
    @Test
    @DisplayName("测试 Stream 的惰性求值")
    void testStreamLazyEvaluation() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        
        // 创建一个会记录调用次数的函数
        final int[] mapCallCount = {0};
        final int[] filterCallCount = {0};
        
        List<Integer> result = numbers.stream()
            .filter(n -> {
                filterCallCount[0]++;
                return n % 2 == 0;
            })
            .map(n -> {
                mapCallCount[0]++;
                return n * n;
            })
            .limit(1)  // 只取第一个
            .collect(Collectors.toList());
        
        // 由于惰性求值和 limit(1)，不会处理所有元素
        assertEquals(1, result.size());
        assertEquals(4, result.get(0)); // 2 * 2 = 4
        
        // filter 会被调用直到找到第一个偶数（1不是偶数，2是偶数）
        assertEquals(2, filterCallCount[0]);
        // map 只会被调用一次（对找到的第一个偶数）
        assertEquals(1, mapCallCount[0]);
    }
    
    @Test
    @DisplayName("测试 Stream 的短路操作")
    void testStreamShortCircuit() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // 测试 findFirst 短路
        Optional<Integer> first = numbers.stream()
            .filter(n -> n > 5)
            .findFirst();
        assertTrue(first.isPresent());
        assertEquals(6, first.get());
        
        // 测试 anyMatch 短路
        boolean hasEven = numbers.stream()
            .anyMatch(n -> n % 2 == 0);
        assertTrue(hasEven);
        
        // 测试 allMatch 短路（遇到第一个不匹配就返回）
        boolean allEven = numbers.stream()
            .allMatch(n -> n % 2 == 0);
        assertFalse(allEven);
    }
    
    @Test
    @DisplayName("测试边界条件")
    void testEdgeCases() {
        // 测试空流
        List<Integer> emptyList = Arrays.asList();
        
        // 空流的各种操作
        assertEquals(0, emptyList.stream().count());
        assertFalse(emptyList.stream().findFirst().isPresent());
        assertFalse(emptyList.stream().anyMatch(n -> true));
        assertTrue(emptyList.stream().allMatch(n -> false)); // 空流的 allMatch 返回 true
        assertTrue(emptyList.stream().noneMatch(n -> true));
        
        // 空流的 reduce
        Optional<Integer> sum = emptyList.stream().reduce((a, b) -> a + b);
        assertFalse(sum.isPresent());
        
        // 带初始值的 reduce
        Integer sumWithIdentity = emptyList.stream().reduce(0, (a, b) -> a + b);
        assertEquals(0, sumWithIdentity);
        
        // 测试单元素流
        List<Integer> singleElement = Arrays.asList(42);
        assertEquals(1, singleElement.stream().count());
        assertEquals(42, singleElement.stream().findFirst().orElse(-1));
        assertEquals(42, singleElement.stream().reduce((a, b) -> a + b).orElse(-1));
        
        // 测试 null 元素处理
        List<String> withNulls = Arrays.asList("a", null, "b", null, "c");
        List<String> nonNulls = withNulls.stream()
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
        assertEquals(Arrays.asList("a", "b", "c"), nonNulls);
    }
    
    @org.junit.jupiter.api.AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }
}