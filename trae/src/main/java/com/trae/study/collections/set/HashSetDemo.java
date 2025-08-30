package com.trae.study.collections.set;

import cn.hutool.core.util.RandomUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * HashSet 源码深度解析演示
 * 
 * 本类深入演示 HashSet 的内部实现机制，包括：
 * 1. HashSet 基于 HashMap 的实现原理
 * 2. 内部结构分析（通过反射查看底层 HashMap）
 * 3. 哈希冲突处理和性能特性
 * 4. 与其他 Set 实现的对比
 * 5. 性能基准测试
 * 
 * @author Trae
 * @since 2024-01-20
 */
@Slf4j
public class HashSetDemo {

    /**
     * 测试数据类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestData {
        private int id;
        private String name;
        private int value;
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TestData testData = (TestData) obj;
            return id == testData.id;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
    
    /**
     * 用于制造哈希冲突的测试类
     */
    @Data
    @AllArgsConstructor
    public static class CollidingData {
        private int value;
        
        @Override
        public int hashCode() {
            // 故意让所有对象都有相同的哈希值，制造冲突
            return 42;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            CollidingData that = (CollidingData) obj;
            return value == that.value;
        }
    }
    
    /**
     * 性能统计结果
     */
    @Data
    @AllArgsConstructor
    public static class PerformanceStats {
        private String operation;
        private long timeMs;
        private int dataSize;
        private double throughput; // 操作数/秒
        
        @Override
        public String toString() {
            return String.format("%s: %d ms, 数据量: %d, 吞吐量: %.2f ops/sec", 
                operation, timeMs, dataSize, throughput);
        }
    }

    public static void main(String[] args) {
        HashSetDemo demo = new HashSetDemo();
        
        log.info("=== HashSet 源码深度解析演示 ===");
        
        // 1. 基本特性演示
        demo.demonstrateBasicFeatures();
        
        // 2. 内部结构分析
        demo.analyzeInternalStructure();
        
        // 3. 哈希冲突处理
        demo.demonstrateHashCollisions();
        
        // 4. 与其他 Set 实现对比
        demo.compareWithOtherSets();
        
        // 5. 性能基准测试
        demo.benchmarkHashSet();
        
        // 6. 综合演示
        demo.comprehensiveDemo();
    }
    
    /**
     * 演示 HashSet 的基本特性
     */
    public void demonstrateBasicFeatures() {
        log.info("\n=== HashSet 基本特性演示 ===");
        
        // 创建 HashSet
        Set<String> hashSet = new HashSet<>();
        
        // 添加元素
        hashSet.add("Apple");
        hashSet.add("Banana");
        hashSet.add("Cherry");
        hashSet.add("Apple"); // 重复元素，不会被添加
        
        log.info("HashSet 内容: {}", hashSet);
        log.info("大小: {}", hashSet.size());
        log.info("是否包含 Apple: {}", hashSet.contains("Apple"));
        
        // 删除元素
        boolean removed = hashSet.remove("Banana");
        log.info("删除 Banana: {}, 当前内容: {}", removed, hashSet);
        
        // 批量操作
        Set<String> otherSet = Set.of("Date", "Elderberry", "Apple");
        hashSet.addAll(otherSet);
        log.info("添加其他集合后: {}", hashSet);
        
        // 集合运算
        Set<String> intersection = new HashSet<>(hashSet);
        intersection.retainAll(otherSet);
        log.info("交集: {}", intersection);
        
        Set<String> union = new HashSet<>(hashSet);
        union.addAll(otherSet);
        log.info("并集: {}", union);
        
        Set<String> difference = new HashSet<>(hashSet);
        difference.removeAll(otherSet);
        log.info("差集: {}", difference);
    }
    
    /**
     * 分析 HashSet 的内部结构
     */
    public void analyzeInternalStructure() {
        log.info("\n=== HashSet 内部结构分析 ===");
        
        try {
            HashSet<String> hashSet = new HashSet<>();
            
            // 通过反射获取内部的 HashMap
            Field mapField = HashSet.class.getDeclaredField("map");
            mapField.setAccessible(true);
            HashMap<String, Object> internalMap = (HashMap<String, Object>) mapField.get(hashSet);
            
            log.info("空 HashSet 的内部 HashMap: {}", internalMap);
            
            // 添加一些元素
            hashSet.addAll(Arrays.asList("A", "B", "C", "D", "E"));
            
            log.info("添加元素后的 HashSet: {}", hashSet);
            log.info("内部 HashMap 的键集合: {}", internalMap.keySet());
            log.info("内部 HashMap 的大小: {}", internalMap.size());
            
            // 获取 HashMap 的内部数组
            Field tableField = HashMap.class.getDeclaredField("table");
            tableField.setAccessible(true);
            Object[] table = (Object[]) tableField.get(internalMap);
            
            if (table != null) {
                log.info("内部数组长度: {}", table.length);
                log.info("非空桶数量: {}", Arrays.stream(table).mapToInt(node -> node != null ? 1 : 0).sum());
            }
            
            // 演示 HashSet 中的 PRESENT 对象
            Field presentField = HashSet.class.getDeclaredField("PRESENT");
            presentField.setAccessible(true);
            Object present = presentField.get(null);
            log.info("PRESENT 对象: {}", present);
            
            // 验证所有值都是 PRESENT
            boolean allPresent = internalMap.values().stream().allMatch(value -> value == present);
            log.info("所有值都是 PRESENT 对象: {}", allPresent);
            
        } catch (Exception e) {
            log.error("反射分析失败", e);
        }
    }
    
    /**
     * 演示哈希冲突处理
     */
    public void demonstrateHashCollisions() {
        log.info("\n=== 哈希冲突处理演示 ===");
        
        // 使用制造冲突的数据
        Set<CollidingData> collidingSet = new HashSet<>();
        
        // 添加多个哈希值相同但不相等的对象
        for (int i = 0; i < 10; i++) {
            CollidingData data = new CollidingData(i);
            collidingSet.add(data);
            log.info("添加 CollidingData({}), 哈希值: {}, 当前大小: {}", 
                i, data.hashCode(), collidingSet.size());
        }
        
        log.info("最终 collidingSet 大小: {}", collidingSet.size());
        log.info("collidingSet 内容: {}", collidingSet);
        
        // 测试查找性能
        long startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            collidingSet.contains(new CollidingData(5));
        }
        long endTime = System.nanoTime();
        log.info("哈希冲突情况下 1000 次查找耗时: {} ns", endTime - startTime);
        
        // 对比正常情况
        Set<Integer> normalSet = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            normalSet.add(i);
        }
        
        startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            normalSet.contains(5);
        }
        endTime = System.nanoTime();
        log.info("正常情况下 1000 次查找耗时: {} ns", endTime - startTime);
    }
    
    /**
     * 与其他 Set 实现对比
     */
    public void compareWithOtherSets() {
        log.info("\n=== 与其他 Set 实现对比 ===");
        
        int dataSize = 10000;
        List<Integer> testData = IntStream.range(0, dataSize)
            .boxed()
            .collect(Collectors.toList());
        Collections.shuffle(testData);
        
        // HashSet
        long startTime = System.currentTimeMillis();
        Set<Integer> hashSet = new HashSet<>(testData);
        long hashSetTime = System.currentTimeMillis() - startTime;
        
        // LinkedHashSet
        startTime = System.currentTimeMillis();
        Set<Integer> linkedHashSet = new LinkedHashSet<>(testData);
        long linkedHashSetTime = System.currentTimeMillis() - startTime;
        
        // TreeSet
        startTime = System.currentTimeMillis();
        Set<Integer> treeSet = new TreeSet<>(testData);
        long treeSetTime = System.currentTimeMillis() - startTime;
        
        log.info("插入 {} 个元素的时间对比:", dataSize);
        log.info("HashSet: {} ms", hashSetTime);
        log.info("LinkedHashSet: {} ms", linkedHashSetTime);
        log.info("TreeSet: {} ms", treeSetTime);
        
        // 查找性能对比
        List<Integer> searchKeys = IntStream.range(0, 1000)
            .map(i -> RandomUtil.randomInt(0, dataSize))
            .boxed()
            .collect(Collectors.toList());
        
        // HashSet 查找
        startTime = System.nanoTime();
        for (Integer key : searchKeys) {
            hashSet.contains(key);
        }
        long hashSetSearchTime = System.nanoTime() - startTime;
        
        // LinkedHashSet 查找
        startTime = System.nanoTime();
        for (Integer key : searchKeys) {
            linkedHashSet.contains(key);
        }
        long linkedHashSetSearchTime = System.nanoTime() - startTime;
        
        // TreeSet 查找
        startTime = System.nanoTime();
        for (Integer key : searchKeys) {
            treeSet.contains(key);
        }
        long treeSetSearchTime = System.nanoTime() - startTime;
        
        log.info("\n查找 {} 个元素的时间对比:", searchKeys.size());
        log.info("HashSet: {} ns (平均 {} ns/次)", hashSetSearchTime, hashSetSearchTime / searchKeys.size());
        log.info("LinkedHashSet: {} ns (平均 {} ns/次)", linkedHashSetSearchTime, linkedHashSetSearchTime / searchKeys.size());
        log.info("TreeSet: {} ns (平均 {} ns/次)", treeSetSearchTime, treeSetSearchTime / searchKeys.size());
        
        // 内存使用对比（近似）
        log.info("\n特性对比:");
        log.info("HashSet: 无序，O(1) 查找，基于哈希表");
        log.info("LinkedHashSet: 插入顺序，O(1) 查找，哈希表+双向链表");
        log.info("TreeSet: 自然顺序，O(log n) 查找，红黑树");
        
        // 验证顺序特性
        Set<Integer> smallData = Set.of(3, 1, 4, 1, 5, 9, 2, 6);
        
        Set<Integer> hashSetOrder = new HashSet<>(smallData);
        Set<Integer> linkedHashSetOrder = new LinkedHashSet<>(Arrays.asList(3, 1, 4, 5, 9, 2, 6));
        Set<Integer> treeSetOrder = new TreeSet<>(smallData);
        
        log.info("\n顺序特性演示:");
        log.info("原始数据: [3, 1, 4, 5, 9, 2, 6]");
        log.info("HashSet: {}", hashSetOrder);
        log.info("LinkedHashSet: {}", linkedHashSetOrder);
        log.info("TreeSet: {}", treeSetOrder);
    }
    
    /**
     * HashSet 性能基准测试
     */
    public void benchmarkHashSet() {
        log.info("\n=== HashSet 性能基准测试 ===");
        
        List<PerformanceStats> results = new ArrayList<>();
        
        // 测试不同数据量下的性能
        int[] dataSizes = {1000, 10000, 100000, 1000000};
        
        for (int dataSize : dataSizes) {
            // 插入性能测试
            Set<Integer> hashSet = new HashSet<>();
            long startTime = System.currentTimeMillis();
            
            for (int i = 0; i < dataSize; i++) {
                hashSet.add(i);
            }
            
            long insertTime = System.currentTimeMillis() - startTime;
            double insertThroughput = dataSize * 1000.0 / insertTime;
            results.add(new PerformanceStats("插入-" + dataSize, insertTime, dataSize, insertThroughput));
            
            // 查找性能测试
            List<Integer> searchKeys = IntStream.range(0, Math.min(10000, dataSize))
                .map(i -> RandomUtil.randomInt(0, dataSize))
                .boxed()
                .collect(Collectors.toList());
            
            startTime = System.currentTimeMillis();
            for (Integer key : searchKeys) {
                hashSet.contains(key);
            }
            long searchTime = System.currentTimeMillis() - startTime;
            double searchThroughput = searchKeys.size() * 1000.0 / Math.max(searchTime, 1);
            results.add(new PerformanceStats("查找-" + dataSize, searchTime, searchKeys.size(), searchThroughput));
            
            // 删除性能测试
            List<Integer> deleteKeys = IntStream.range(0, Math.min(1000, dataSize))
                .map(i -> RandomUtil.randomInt(0, dataSize))
                .boxed()
                .collect(Collectors.toList());
            
            startTime = System.currentTimeMillis();
            for (Integer key : deleteKeys) {
                hashSet.remove(key);
            }
            long deleteTime = System.currentTimeMillis() - startTime;
            double deleteThroughput = deleteKeys.size() * 1000.0 / Math.max(deleteTime, 1);
            results.add(new PerformanceStats("删除-" + dataSize, deleteTime, deleteKeys.size(), deleteThroughput));
        }
        
        // 输出结果
        log.info("\n性能测试结果:");
        results.forEach(stat -> log.info(stat.toString()));
        
        // 负载因子对性能的影响
        benchmarkLoadFactor();
        
        // 初始容量对性能的影响
        benchmarkInitialCapacity();
    }
    
    /**
     * 测试负载因子对性能的影响
     */
    private void benchmarkLoadFactor() {
        log.info("\n=== 负载因子对性能的影响 ===");
        
        int dataSize = 100000;
        
        // 默认负载因子 (0.75)
        long startTime = System.currentTimeMillis();
        Set<Integer> defaultSet = new HashSet<>();
        for (int i = 0; i < dataSize; i++) {
            defaultSet.add(i);
        }
        long defaultTime = System.currentTimeMillis() - startTime;
        
        // 低负载因子 (0.5) - 通过设置更大的初始容量实现
        startTime = System.currentTimeMillis();
        Set<Integer> lowLoadSet = new HashSet<>(dataSize * 2);
        for (int i = 0; i < dataSize; i++) {
            lowLoadSet.add(i);
        }
        long lowLoadTime = System.currentTimeMillis() - startTime;
        
        // 高负载因子 - 通过设置较小的初始容量实现
        startTime = System.currentTimeMillis();
        Set<Integer> highLoadSet = new HashSet<>(dataSize / 4);
        for (int i = 0; i < dataSize; i++) {
            highLoadSet.add(i);
        }
        long highLoadTime = System.currentTimeMillis() - startTime;
        
        log.info("插入 {} 个元素的时间对比:", dataSize);
        log.info("默认负载因子 (0.75): {} ms", defaultTime);
        log.info("低负载因子 (~0.5): {} ms", lowLoadTime);
        log.info("高负载因子 (>1.0): {} ms", highLoadTime);
    }
    
    /**
     * 测试初始容量对性能的影响
     */
    private void benchmarkInitialCapacity() {
        log.info("\n=== 初始容量对性能的影响 ===");
        
        int dataSize = 100000;
        
        // 默认初始容量 (16)
        long startTime = System.currentTimeMillis();
        Set<Integer> defaultCapacitySet = new HashSet<>();
        for (int i = 0; i < dataSize; i++) {
            defaultCapacitySet.add(i);
        }
        long defaultCapacityTime = System.currentTimeMillis() - startTime;
        
        // 合适的初始容量
        startTime = System.currentTimeMillis();
        Set<Integer> appropriateCapacitySet = new HashSet<>(dataSize);
        for (int i = 0; i < dataSize; i++) {
            appropriateCapacitySet.add(i);
        }
        long appropriateCapacityTime = System.currentTimeMillis() - startTime;
        
        // 过大的初始容量
        startTime = System.currentTimeMillis();
        Set<Integer> largeCapacitySet = new HashSet<>(dataSize * 10);
        for (int i = 0; i < dataSize; i++) {
            largeCapacitySet.add(i);
        }
        long largeCapacityTime = System.currentTimeMillis() - startTime;
        
        log.info("插入 {} 个元素的时间对比:", dataSize);
        log.info("默认初始容量 (16): {} ms", defaultCapacityTime);
        log.info("合适初始容量 ({}): {} ms", dataSize, appropriateCapacityTime);
        log.info("过大初始容量 ({}): {} ms", dataSize * 10, largeCapacityTime);
    }
    
    /**
     * 综合演示
     */
    public void comprehensiveDemo() {
        log.info("\n=== HashSet 综合演示 ===");
        
        // 创建学生数据集
        Set<TestData> students = new HashSet<>();
        
        // 添加学生数据
        students.add(new TestData(1, "Alice", 85));
        students.add(new TestData(2, "Bob", 92));
        students.add(new TestData(3, "Charlie", 78));
        students.add(new TestData(1, "Alice Updated", 90)); // 相同 ID，不会添加
        
        log.info("学生集合: {}", students);
        log.info("学生数量: {}", students.size());
        
        // 使用 Stream API 进行操作
        Set<TestData> highScoreStudents = students.stream()
            .filter(student -> student.getValue() > 80)
            .collect(Collectors.toSet());
        
        log.info("高分学生 (>80): {}", highScoreStudents);
        
        // 集合运算
        Set<TestData> anotherGroup = new HashSet<>();
        anotherGroup.add(new TestData(2, "Bob", 92));
        anotherGroup.add(new TestData(4, "David", 88));
        anotherGroup.add(new TestData(5, "Eve", 95));
        
        // 交集
        Set<TestData> intersection = new HashSet<>(students);
        intersection.retainAll(anotherGroup);
        log.info("两组学生的交集: {}", intersection);
        
        // 并集
        Set<TestData> union = new HashSet<>(students);
        union.addAll(anotherGroup);
        log.info("两组学生的并集: {}", union);
        
        // 差集
        Set<TestData> difference = new HashSet<>(students);
        difference.removeAll(anotherGroup);
        log.info("第一组独有的学生: {}", difference);
        
        // 对称差集
        Set<TestData> symmetricDifference = new HashSet<>(union);
        symmetricDifference.removeAll(intersection);
        log.info("对称差集: {}", symmetricDifference);
        
        // 演示 equals 和 hashCode 的重要性
        demonstrateEqualsHashCode();
    }
    
    /**
     * 演示 equals 和 hashCode 的重要性
     */
    private void demonstrateEqualsHashCode() {
        log.info("\n=== equals 和 hashCode 的重要性 ===");
        
        // 正确实现 equals 和 hashCode 的类
        Set<TestData> correctSet = new HashSet<>();
        TestData data1 = new TestData(1, "Test", 100);
        TestData data2 = new TestData(1, "Test Updated", 200);
        
        correctSet.add(data1);
        correctSet.add(data2); // 由于 ID 相同，不会被添加
        
        log.info("正确实现的集合大小: {}", correctSet.size());
        log.info("正确实现的集合内容: {}", correctSet);
        
        // 错误实现的示例（没有重写 equals 和 hashCode）
        @Data
        @AllArgsConstructor
        class BadTestData {
            private int id;
            private String name;
            // 没有重写 equals 和 hashCode
        }
        
        Set<BadTestData> badSet = new HashSet<>();
        BadTestData badData1 = new BadTestData(1, "Test");
        BadTestData badData2 = new BadTestData(1, "Test");
        
        badSet.add(badData1);
        badSet.add(badData2); // 会被添加，因为使用默认的 Object.equals()
        
        log.info("错误实现的集合大小: {}", badSet.size());
        log.info("错误实现的集合内容: {}", badSet);
        
        // 验证查找行为
        log.info("正确实现 - 包含相同 ID 的对象: {}", correctSet.contains(new TestData(1, "Any Name", 999)));
        log.info("错误实现 - 包含相同内容的对象: {}", badSet.contains(new BadTestData(1, "Test")));
    }
}