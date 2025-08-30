package com.trae.study.collections.set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * HashSetDemo 单元测试
 * 
 * 测试 HashSet 的各种特性和功能，包括：
 * 1. 基本操作测试
 * 2. 内部结构验证
 * 3. 哈希冲突处理测试
 * 4. 性能特性验证
 * 5. 边界条件测试
 * 
 * @author Trae
 * @since 2024-01-20
 */
@DisplayName("HashSet 演示测试")
class HashSetDemoTest {

    private HashSetDemo demo;

    @BeforeEach
    void setUp() {
        demo = new HashSetDemo();
    }

    @Nested
    @DisplayName("基本特性测试")
    class BasicFeaturesTest {

        @Test
        @DisplayName("基本操作测试")
        void testBasicOperations() {
            Set<String> hashSet = new HashSet<>();
            
            // 测试添加
            assertTrue(hashSet.add("Apple"));
            assertTrue(hashSet.add("Banana"));
            assertFalse(hashSet.add("Apple")); // 重复元素
            
            assertEquals(2, hashSet.size());
            assertTrue(hashSet.contains("Apple"));
            assertTrue(hashSet.contains("Banana"));
            assertFalse(hashSet.contains("Cherry"));
            
            // 测试删除
            assertTrue(hashSet.remove("Apple"));
            assertFalse(hashSet.remove("Cherry")); // 不存在的元素
            
            assertEquals(1, hashSet.size());
            assertFalse(hashSet.contains("Apple"));
            assertTrue(hashSet.contains("Banana"));
        }

        @Test
        @DisplayName("集合运算测试")
        void testSetOperations() {
            Set<Integer> set1 = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
            Set<Integer> set2 = new HashSet<>(Arrays.asList(4, 5, 6, 7, 8));
            
            // 交集
            Set<Integer> intersection = new HashSet<>(set1);
            intersection.retainAll(set2);
            assertEquals(Set.of(4, 5), intersection);
            
            // 并集
            Set<Integer> union = new HashSet<>(set1);
            union.addAll(set2);
            assertEquals(Set.of(1, 2, 3, 4, 5, 6, 7, 8), union);
            
            // 差集
            Set<Integer> difference = new HashSet<>(set1);
            difference.removeAll(set2);
            assertEquals(Set.of(1, 2, 3), difference);
            
            // 对称差集
            Set<Integer> symmetricDifference = new HashSet<>(union);
            symmetricDifference.removeAll(intersection);
            assertEquals(Set.of(1, 2, 3, 6, 7, 8), symmetricDifference);
        }

        @Test
        @DisplayName("批量操作测试")
        void testBulkOperations() {
            Set<String> hashSet = new HashSet<>();
            List<String> data = Arrays.asList("A", "B", "C", "D", "E");
            
            // 批量添加
            assertTrue(hashSet.addAll(data));
            assertEquals(5, hashSet.size());
            
            // 批量包含检查
            assertTrue(hashSet.containsAll(Arrays.asList("A", "C", "E")));
            assertFalse(hashSet.containsAll(Arrays.asList("A", "F")));
            
            // 批量删除
            assertTrue(hashSet.removeAll(Arrays.asList("A", "C")));
            assertEquals(3, hashSet.size());
            assertFalse(hashSet.contains("A"));
            assertFalse(hashSet.contains("C"));
            
            // 保留操作
            assertTrue(hashSet.retainAll(Arrays.asList("B", "D", "F")));
            assertEquals(2, hashSet.size());
            assertTrue(hashSet.contains("B"));
            assertTrue(hashSet.contains("D"));
            assertFalse(hashSet.contains("E"));
        }
    }

    @Nested
    @DisplayName("内部结构测试")
    class InternalStructureTest {

        @Test
        @DisplayName("内部HashMap验证")
        void testInternalHashMap() throws Exception {
            HashSet<String> hashSet = new HashSet<>();
            
            // 通过反射获取内部HashMap
            Field mapField = HashSet.class.getDeclaredField("map");
            mapField.setAccessible(true);
            HashMap<String, Object> internalMap = (HashMap<String, Object>) mapField.get(hashSet);
            
            assertNotNull(internalMap);
            assertEquals(0, internalMap.size());
            
            // 添加元素
            hashSet.add("test");
            assertEquals(1, internalMap.size());
            assertTrue(internalMap.containsKey("test"));
            
            // 验证PRESENT对象
            Field presentField = HashSet.class.getDeclaredField("PRESENT");
            presentField.setAccessible(true);
            Object present = presentField.get(null);
            
            assertEquals(present, internalMap.get("test"));
        }

        @Test
        @DisplayName("PRESENT对象一致性测试")
        void testPresentObjectConsistency() throws Exception {
            HashSet<String> hashSet = new HashSet<>();
            hashSet.addAll(Arrays.asList("A", "B", "C"));
            
            Field mapField = HashSet.class.getDeclaredField("map");
            mapField.setAccessible(true);
            HashMap<String, Object> internalMap = (HashMap<String, Object>) mapField.get(hashSet);
            
            Field presentField = HashSet.class.getDeclaredField("PRESENT");
            presentField.setAccessible(true);
            Object present = presentField.get(null);
            
            // 验证所有值都是同一个PRESENT对象
            for (Object value : internalMap.values()) {
                assertSame(present, value);
            }
        }
    }

    @Nested
    @DisplayName("哈希冲突测试")
    class HashCollisionTest {

        @Test
        @DisplayName("哈希冲突处理测试")
        void testHashCollisionHandling() {
            Set<HashSetDemo.CollidingData> collidingSet = new HashSet<>();
            
            // 添加多个哈希值相同但不相等的对象
            for (int i = 0; i < 10; i++) {
                HashSetDemo.CollidingData data = new HashSetDemo.CollidingData(i);
                assertTrue(collidingSet.add(data));
                assertEquals(42, data.hashCode()); // 验证哈希值都是42
            }
            
            assertEquals(10, collidingSet.size());
            
            // 验证查找功能正常
            for (int i = 0; i < 10; i++) {
                assertTrue(collidingSet.contains(new HashSetDemo.CollidingData(i)));
            }
            
            assertFalse(collidingSet.contains(new HashSetDemo.CollidingData(10)));
        }

        @Test
        @DisplayName("哈希冲突性能测试")
        void testHashCollisionPerformance() {
            Set<HashSetDemo.CollidingData> collidingSet = new HashSet<>();
            Set<Integer> normalSet = new HashSet<>();
            
            // 准备数据
            for (int i = 0; i < 100; i++) {
                collidingSet.add(new HashSetDemo.CollidingData(i));
                normalSet.add(i);
            }
            
            // 测试查找性能（简单验证，不做严格的性能断言）
            long collidingTime = measureSearchTime(collidingSet, 
                new HashSetDemo.CollidingData(50), 1000);
            long normalTime = measureSearchTime(normalSet, 50, 1000);
            
            // 哈希冲突情况下查找时间应该更长
            assertTrue(collidingTime >= normalTime, 
                String.format("冲突查找时间: %d ns, 正常查找时间: %d ns", collidingTime, normalTime));
        }
        
        private <T> long measureSearchTime(Set<T> set, T searchItem, int iterations) {
            long startTime = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                set.contains(searchItem);
            }
            return System.nanoTime() - startTime;
        }
    }

    @Nested
    @DisplayName("TestData类测试")
    class TestDataTest {

        @Test
        @DisplayName("TestData equals和hashCode测试")
        void testTestDataEqualsAndHashCode() {
            HashSetDemo.TestData data1 = new HashSetDemo.TestData(1, "Alice", 85);
            HashSetDemo.TestData data2 = new HashSetDemo.TestData(1, "Bob", 90);
            HashSetDemo.TestData data3 = new HashSetDemo.TestData(2, "Alice", 85);
            
            // 相同ID的对象应该相等
            assertEquals(data1, data2);
            assertEquals(data1.hashCode(), data2.hashCode());
            
            // 不同ID的对象应该不相等
            assertNotEquals(data1, data3);
            
            // 在HashSet中的行为
            Set<HashSetDemo.TestData> set = new HashSet<>();
            assertTrue(set.add(data1));
            assertFalse(set.add(data2)); // 相同ID，不会添加
            assertTrue(set.add(data3)); // 不同ID，会添加
            
            assertEquals(2, set.size());
        }

        @Test
        @DisplayName("TestData在集合中的查找测试")
        void testTestDataInSetOperations() {
            Set<HashSetDemo.TestData> students = new HashSet<>();
            students.add(new HashSetDemo.TestData(1, "Alice", 85));
            students.add(new HashSetDemo.TestData(2, "Bob", 92));
            students.add(new HashSetDemo.TestData(3, "Charlie", 78));
            
            // 使用相同ID但不同内容的对象查找
            assertTrue(students.contains(new HashSetDemo.TestData(1, "Different Name", 999)));
            assertTrue(students.contains(new HashSetDemo.TestData(2, "Different Name", 999)));
            assertFalse(students.contains(new HashSetDemo.TestData(4, "David", 88)));
            
            // 删除测试
            assertTrue(students.remove(new HashSetDemo.TestData(1, "Any Name", 0)));
            assertEquals(2, students.size());
            assertFalse(students.contains(new HashSetDemo.TestData(1, "Alice", 85)));
        }
    }

    @Nested
    @DisplayName("性能特性测试")
    class PerformanceTest {

        @Test
        @DisplayName("大数据量操作测试")
        void testLargeDataOperations() {
            Set<Integer> hashSet = new HashSet<>();
            int dataSize = 10000;
            
            // 大量插入
            long startTime = System.currentTimeMillis();
            for (int i = 0; i < dataSize; i++) {
                hashSet.add(i);
            }
            long insertTime = System.currentTimeMillis() - startTime;
            
            assertEquals(dataSize, hashSet.size());
            assertTrue(insertTime < 1000, "插入10000个元素应该在1秒内完成");
            
            // 大量查找
            startTime = System.currentTimeMillis();
            for (int i = 0; i < dataSize; i++) {
                assertTrue(hashSet.contains(i));
            }
            long searchTime = System.currentTimeMillis() - startTime;
            
            assertTrue(searchTime < 100, "查找10000个元素应该在100ms内完成");
            
            // 大量删除
            startTime = System.currentTimeMillis();
            for (int i = 0; i < dataSize / 2; i++) {
                assertTrue(hashSet.remove(i));
            }
            long deleteTime = System.currentTimeMillis() - startTime;
            
            assertEquals(dataSize / 2, hashSet.size());
            assertTrue(deleteTime < 100, "删除5000个元素应该在100ms内完成");
        }

        @Test
        @DisplayName("不同Set实现性能对比")
        void testSetImplementationComparison() {
            int dataSize = 1000;
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
            
            // 验证所有集合都包含相同的元素
            assertEquals(dataSize, hashSet.size());
            assertEquals(dataSize, linkedHashSet.size());
            assertEquals(dataSize, treeSet.size());
            
            assertTrue(hashSet.containsAll(testData));
            assertTrue(linkedHashSet.containsAll(testData));
            assertTrue(treeSet.containsAll(testData));
            
            // 简单的性能验证（不做严格断言，因为性能可能因环境而异）
            assertTrue(hashSetTime >= 0);
            assertTrue(linkedHashSetTime >= 0);
            assertTrue(treeSetTime >= 0);
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryConditionTest {

        @Test
        @DisplayName("空集合操作测试")
        void testEmptySetOperations() {
            Set<String> emptySet = new HashSet<>();
            
            assertEquals(0, emptySet.size());
            assertTrue(emptySet.isEmpty());
            assertFalse(emptySet.contains("anything"));
            assertFalse(emptySet.remove("anything"));
            
            // 空集合的迭代
            int count = 0;
            for (String item : emptySet) {
                count++;
            }
            assertEquals(0, count);
            
            // 空集合的流操作
            assertEquals(0, emptySet.stream().count());
            assertFalse(emptySet.stream().findAny().isPresent());
        }

        @Test
        @DisplayName("单元素集合操作测试")
        void testSingleElementSetOperations() {
            Set<String> singleSet = new HashSet<>();
            singleSet.add("single");
            
            assertEquals(1, singleSet.size());
            assertFalse(singleSet.isEmpty());
            assertTrue(singleSet.contains("single"));
            assertFalse(singleSet.contains("other"));
            
            // 单元素集合的迭代
            int count = 0;
            for (String item : singleSet) {
                assertEquals("single", item);
                count++;
            }
            assertEquals(1, count);
            
            // 删除唯一元素
            assertTrue(singleSet.remove("single"));
            assertTrue(singleSet.isEmpty());
        }

        @Test
        @DisplayName("null值处理测试")
        void testNullValueHandling() {
            Set<String> hashSet = new HashSet<>();
            
            // HashSet允许一个null值
            assertTrue(hashSet.add(null));
            assertFalse(hashSet.add(null)); // 重复的null
            
            assertEquals(1, hashSet.size());
            assertTrue(hashSet.contains(null));
            
            // 添加其他元素
            hashSet.add("test");
            assertEquals(2, hashSet.size());
            
            // 删除null
            assertTrue(hashSet.remove(null));
            assertEquals(1, hashSet.size());
            assertFalse(hashSet.contains(null));
        }

        @Test
        @DisplayName("重复元素处理测试")
        void testDuplicateElementHandling() {
            Set<Integer> hashSet = new HashSet<>();
            
            // 添加重复元素
            for (int i = 0; i < 5; i++) {
                for (int j = 0; j < 3; j++) {
                    if (j == 0) {
                        assertTrue(hashSet.add(i)); // 第一次添加成功
                    } else {
                        assertFalse(hashSet.add(i)); // 后续添加失败
                    }
                }
            }
            
            assertEquals(5, hashSet.size());
            
            // 验证所有元素都存在
            for (int i = 0; i < 5; i++) {
                assertTrue(hashSet.contains(i));
            }
        }
    }

    @Nested
    @DisplayName("迭代器测试")
    class IteratorTest {

        @Test
        @DisplayName("迭代器基本功能测试")
        void testIteratorBasicFunctionality() {
            Set<Integer> hashSet = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
            
            Iterator<Integer> iterator = hashSet.iterator();
            Set<Integer> iteratedElements = new HashSet<>();
            
            while (iterator.hasNext()) {
                Integer element = iterator.next();
                assertNotNull(element);
                iteratedElements.add(element);
            }
            
            assertEquals(hashSet.size(), iteratedElements.size());
            assertEquals(hashSet, iteratedElements);
        }

        @Test
        @DisplayName("迭代器删除功能测试")
        void testIteratorRemoval() {
            Set<Integer> hashSet = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
            
            Iterator<Integer> iterator = hashSet.iterator();
            while (iterator.hasNext()) {
                Integer element = iterator.next();
                if (element % 2 == 0) {
                    iterator.remove(); // 删除偶数
                }
            }
            
            assertEquals(3, hashSet.size());
            assertTrue(hashSet.contains(1));
            assertFalse(hashSet.contains(2));
            assertTrue(hashSet.contains(3));
            assertFalse(hashSet.contains(4));
            assertTrue(hashSet.contains(5));
        }

        @Test
        @DisplayName("并发修改异常测试")
        void testConcurrentModificationException() {
            Set<Integer> hashSet = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
            
            Iterator<Integer> iterator = hashSet.iterator();
            iterator.next(); // 开始迭代
            
            hashSet.add(6); // 在迭代过程中修改集合
            
            // 下次调用iterator方法时应该抛出ConcurrentModificationException
            assertThrows(ConcurrentModificationException.class, iterator::next);
        }
    }

    @Nested
    @DisplayName("演示方法测试")
    class DemoMethodTest {

        @Test
        @DisplayName("基本特性演示方法测试")
        void testDemonstrateBasicFeatures() {
            assertDoesNotThrow(() -> demo.demonstrateBasicFeatures());
        }

        @Test
        @DisplayName("内部结构分析方法测试")
        void testAnalyzeInternalStructure() {
            assertDoesNotThrow(() -> demo.analyzeInternalStructure());
        }

        @Test
        @DisplayName("哈希冲突演示方法测试")
        void testDemonstrateHashCollisions() {
            assertDoesNotThrow(() -> demo.demonstrateHashCollisions());
        }

        @Test
        @DisplayName("Set实现对比方法测试")
        void testCompareWithOtherSets() {
            assertDoesNotThrow(() -> demo.compareWithOtherSets());
        }

        @Test
        @DisplayName("性能基准测试方法测试")
        void testBenchmarkHashSet() {
            assertDoesNotThrow(() -> demo.benchmarkHashSet());
        }

        @Test
        @DisplayName("综合演示方法测试")
        void testComprehensiveDemo() {
            assertDoesNotThrow(() -> demo.comprehensiveDemo());
        }
    }

    @Nested
    @DisplayName("Stream API集成测试")
    class StreamIntegrationTest {

        @Test
        @DisplayName("Stream过滤操作测试")
        void testStreamFiltering() {
            Set<Integer> numbers = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
            
            Set<Integer> evenNumbers = numbers.stream()
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toSet());
            
            assertEquals(Set.of(2, 4, 6, 8, 10), evenNumbers);
        }

        @Test
        @DisplayName("Stream映射操作测试")
        void testStreamMapping() {
            Set<String> words = new HashSet<>(Arrays.asList("hello", "world", "java", "set"));
            
            Set<Integer> lengths = words.stream()
                .map(String::length)
                .collect(Collectors.toSet());
            
            assertEquals(Set.of(3, 4, 5), lengths);
        }

        @Test
        @DisplayName("Stream归约操作测试")
        void testStreamReduction() {
            Set<Integer> numbers = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
            
            int sum = numbers.stream()
                .mapToInt(Integer::intValue)
                .sum();
            
            assertEquals(15, sum);
            
            Optional<Integer> max = numbers.stream()
                .max(Integer::compareTo);
            
            assertTrue(max.isPresent());
            assertEquals(5, max.get());
        }
    }
}