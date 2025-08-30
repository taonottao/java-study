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
 * LinkedHashSetDemo 单元测试
 * 
 * 测试 LinkedHashSet 的各种特性和功能，包括：
 * 1. 有序性特性测试
 * 2. 内部结构验证
 * 3. 性能特性测试
 * 4. 实际应用场景测试
 * 5. 边界条件测试
 * 
 * @author Trae
 * @since 2024-01-20
 */
@DisplayName("LinkedHashSet 演示测试")
class LinkedHashSetDemoTest {

    private LinkedHashSetDemo demo;

    @BeforeEach
    void setUp() {
        demo = new LinkedHashSetDemo();
    }

    @Nested
    @DisplayName("基本特性测试")
    class BasicFeaturesTest {

        @Test
        @DisplayName("插入顺序保持测试")
        void testInsertionOrderMaintenance() {
            LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
            String[] elements = {"First", "Second", "Third", "Fourth", "Fifth"};
            
            // 按顺序添加元素
            for (String element : elements) {
                linkedHashSet.add(element);
            }
            
            // 验证迭代顺序与插入顺序一致
            Iterator<String> iterator = linkedHashSet.iterator();
            for (String expected : elements) {
                assertTrue(iterator.hasNext());
                assertEquals(expected, iterator.next());
            }
            assertFalse(iterator.hasNext());
            
            // 转换为数组验证顺序
            String[] actualArray = linkedHashSet.toArray(new String[0]);
            assertArrayEquals(elements, actualArray);
        }

        @Test
        @DisplayName("重复元素不影响顺序测试")
        void testDuplicateElementsDoNotAffectOrder() {
            LinkedHashSet<Integer> set = new LinkedHashSet<>();
            
            // 添加元素
            set.add(1);
            set.add(2);
            set.add(3);
            
            List<Integer> originalOrder = new ArrayList<>(set);
            
            // 重复添加已存在的元素
            assertFalse(set.add(2)); // 应该返回false
            assertFalse(set.add(1)); // 应该返回false
            
            // 验证顺序没有改变
            List<Integer> currentOrder = new ArrayList<>(set);
            assertEquals(originalOrder, currentOrder);
            assertEquals(3, set.size());
        }

        @Test
        @DisplayName("删除和重新添加影响顺序测试")
        void testRemoveAndReAddAffectsOrder() {
            LinkedHashSet<String> set = new LinkedHashSet<>();
            set.addAll(Arrays.asList("A", "B", "C", "D"));
            
            // 原始顺序
            List<String> originalOrder = new ArrayList<>(set);
            assertEquals(Arrays.asList("A", "B", "C", "D"), originalOrder);
            
            // 删除中间元素
            assertTrue(set.remove("B"));
            assertEquals(Arrays.asList("A", "C", "D"), new ArrayList<>(set));
            
            // 重新添加，应该在末尾
            assertTrue(set.add("B"));
            assertEquals(Arrays.asList("A", "C", "D", "B"), new ArrayList<>(set));
        }

        @Test
        @DisplayName("与HashSet顺序对比测试")
        void testOrderComparisonWithHashSet() {
            LinkedHashSet<Integer> linkedHashSet = new LinkedHashSet<>();
            HashSet<Integer> hashSet = new HashSet<>();
            
            // 添加相同的元素
            List<Integer> elements = Arrays.asList(5, 2, 8, 1, 9, 3, 7, 4, 6);
            linkedHashSet.addAll(elements);
            hashSet.addAll(elements);
            
            // LinkedHashSet 保持插入顺序
            assertEquals(elements, new ArrayList<>(linkedHashSet));
            
            // HashSet 不保证顺序（通常与插入顺序不同）
            List<Integer> hashSetOrder = new ArrayList<>(hashSet);
            // 注意：这里不能断言hashSet的顺序，因为它是不确定的
            assertEquals(elements.size(), hashSetOrder.size());
            assertTrue(hashSetOrder.containsAll(elements));
        }
    }

    @Nested
    @DisplayName("内部结构测试")
    class InternalStructureTest {

        @Test
        @DisplayName("内部LinkedHashMap验证")
        void testInternalLinkedHashMap() throws Exception {
            LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
            linkedHashSet.addAll(Arrays.asList("X", "Y", "Z"));
            
            // 通过反射获取内部map
            Field mapField = HashSet.class.getDeclaredField("map");
            mapField.setAccessible(true);
            Object internalMap = mapField.get(linkedHashSet);
            
            // 验证是LinkedHashMap实例
            assertTrue(internalMap instanceof LinkedHashMap);
            
            LinkedHashMap<String, Object> linkedMap = (LinkedHashMap<String, Object>) internalMap;
            assertEquals(3, linkedMap.size());
            
            // 验证键的顺序
            List<String> keyOrder = new ArrayList<>(linkedMap.keySet());
            assertEquals(Arrays.asList("X", "Y", "Z"), keyOrder);
        }

        @Test
        @DisplayName("访问顺序模式检查")
        void testAccessOrderMode() throws Exception {
            LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
            
            Field mapField = HashSet.class.getDeclaredField("map");
            mapField.setAccessible(true);
            LinkedHashMap<String, Object> internalMap = 
                (LinkedHashMap<String, Object>) mapField.get(linkedHashSet);
            
            // 检查访问顺序模式
            try {
                Field accessOrderField = LinkedHashMap.class.getDeclaredField("accessOrder");
                accessOrderField.setAccessible(true);
                boolean accessOrder = accessOrderField.getBoolean(internalMap);
                
                // LinkedHashSet 使用插入顺序，不是访问顺序
                assertFalse(accessOrder, "LinkedHashSet应该使用插入顺序模式");
            } catch (NoSuchFieldException e) {
                // 在某些JVM实现中可能没有这个字段，跳过测试
                System.out.println("跳过访问顺序模式检查: " + e.getMessage());
            }
        }

        @Test
        @DisplayName("PRESENT对象一致性测试")
        void testPresentObjectConsistency() throws Exception {
            LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
            linkedHashSet.addAll(Arrays.asList("P", "Q", "R"));
            
            Field mapField = HashSet.class.getDeclaredField("map");
            mapField.setAccessible(true);
            LinkedHashMap<String, Object> internalMap = 
                (LinkedHashMap<String, Object>) mapField.get(linkedHashSet);
            
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
    @DisplayName("性能特性测试")
    class PerformanceTest {

        @Test
        @DisplayName("迭代性能测试")
        void testIterationPerformance() {
            int size = 1000;
            
            LinkedHashSet<Integer> linkedHashSet = new LinkedHashSet<>();
            HashSet<Integer> hashSet = new HashSet<>();
            
            // 填充数据
            for (int i = 0; i < size; i++) {
                linkedHashSet.add(i);
                hashSet.add(i);
            }
            
            // 测试LinkedHashSet迭代
            long startTime = System.nanoTime();
            int count = 0;
            for (Integer item : linkedHashSet) {
                count++;
            }
            long linkedHashSetTime = System.nanoTime() - startTime;
            assertEquals(size, count);
            
            // 测试HashSet迭代
            startTime = System.nanoTime();
            count = 0;
            for (Integer item : hashSet) {
                count++;
            }
            long hashSetTime = System.nanoTime() - startTime;
            assertEquals(size, count);
            
            // 验证两者都能正常迭代（不做严格的性能断言）
            assertTrue(linkedHashSetTime > 0);
            assertTrue(hashSetTime > 0);
        }

        @Test
        @DisplayName("插入性能测试")
        void testInsertionPerformance() {
            int size = 1000;
            List<Integer> data = IntStream.range(0, size).boxed().collect(Collectors.toList());
            Collections.shuffle(data);
            
            // 测试LinkedHashSet插入
            long startTime = System.nanoTime();
            LinkedHashSet<Integer> linkedHashSet = new LinkedHashSet<>();
            for (Integer item : data) {
                linkedHashSet.add(item);
            }
            long linkedHashSetTime = System.nanoTime() - startTime;
            
            // 测试HashSet插入
            startTime = System.nanoTime();
            HashSet<Integer> hashSet = new HashSet<>();
            for (Integer item : data) {
                hashSet.add(item);
            }
            long hashSetTime = System.nanoTime() - startTime;
            
            // 验证插入结果
            assertEquals(size, linkedHashSet.size());
            assertEquals(size, hashSet.size());
            
            // 验证性能测试完成（不做严格断言）
            assertTrue(linkedHashSetTime > 0);
            assertTrue(hashSetTime > 0);
        }

        @Test
        @DisplayName("查找性能测试")
        void testLookupPerformance() {
            int size = 1000;
            
            LinkedHashSet<Integer> linkedHashSet = new LinkedHashSet<>();
            HashSet<Integer> hashSet = new HashSet<>();
            
            // 填充数据
            for (int i = 0; i < size; i++) {
                linkedHashSet.add(i);
                hashSet.add(i);
            }
            
            // 准备查找数据
            List<Integer> lookupData = Arrays.asList(0, size/4, size/2, 3*size/4, size-1);
            
            // 测试LinkedHashSet查找
            long startTime = System.nanoTime();
            for (Integer item : lookupData) {
                assertTrue(linkedHashSet.contains(item));
            }
            long linkedHashSetTime = System.nanoTime() - startTime;
            
            // 测试HashSet查找
            startTime = System.nanoTime();
            for (Integer item : lookupData) {
                assertTrue(hashSet.contains(item));
            }
            long hashSetTime = System.nanoTime() - startTime;
            
            // 验证查找功能正常
            assertTrue(linkedHashSetTime > 0);
            assertTrue(hashSetTime > 0);
        }
    }

    @Nested
    @DisplayName("实际应用场景测试")
    class UseCaseTest {

        @Test
        @DisplayName("去重保持顺序测试")
        void testDeduplicationWithOrder() {
            List<String> originalList = Arrays.asList(
                "页面A", "页面B", "页面C", "页面A", "页面D", "页面B", "页面E"
            );
            
            // 使用LinkedHashSet去重但保持首次出现的顺序
            LinkedHashSet<String> uniquePages = new LinkedHashSet<>(originalList);
            
            List<String> result = new ArrayList<>(uniquePages);
            List<String> expected = Arrays.asList("页面A", "页面B", "页面C", "页面D", "页面E");
            
            assertEquals(expected, result);
            assertEquals(5, uniquePages.size());
        }

        @Test
        @DisplayName("TagManager功能测试")
        void testTagManager() {
            LinkedHashSetDemo.TagManager tagManager = new LinkedHashSetDemo.TagManager();
            
            // 添加标签
            tagManager.addTag("Java");
            tagManager.addTag("Spring");
            tagManager.addTag("Database");
            tagManager.addTag("Java"); // 重复
            tagManager.addTag("Redis");
            
            // 验证标签数量和顺序
            assertEquals(4, tagManager.getTagCount());
            List<String> allTags = tagManager.getAllTags();
            assertEquals(Arrays.asList("Java", "Spring", "Database", "Redis"), allTags);
            
            // 测试包含检查
            assertTrue(tagManager.hasTag("Java"));
            assertTrue(tagManager.hasTag("Spring"));
            assertFalse(tagManager.hasTag("Python"));
            
            // 测试获取前N个标签
            List<String> topTags = tagManager.getTopTags(2);
            assertEquals(Arrays.asList("Java", "Spring"), topTags);
            
            // 测试删除标签
            assertTrue(tagManager.removeTag("Database"));
            assertFalse(tagManager.removeTag("Python"));
            assertEquals(3, tagManager.getTagCount());
            assertFalse(tagManager.hasTag("Database"));
            
            // 测试清空
            tagManager.clear();
            assertEquals(0, tagManager.getTagCount());
            assertTrue(tagManager.getAllTags().isEmpty());
        }

        @Test
        @DisplayName("TagManager边界条件测试")
        void testTagManagerBoundaryConditions() {
            LinkedHashSetDemo.TagManager tagManager = new LinkedHashSetDemo.TagManager();
            
            // 测试空字符串和null
            tagManager.addTag("");
            tagManager.addTag(null);
            tagManager.addTag("   ");
            assertEquals(0, tagManager.getTagCount());
            
            // 测试空白字符处理
            tagManager.addTag("  Java  ");
            assertTrue(tagManager.hasTag("Java"));
            assertEquals(1, tagManager.getTagCount());
            
            // 测试获取超出范围的标签
            List<String> topTags = tagManager.getTopTags(10);
            assertEquals(1, topTags.size());
            assertEquals("Java", topTags.get(0));
        }

        @Test
        @DisplayName("ConfigurationManager功能测试")
        void testConfigurationManager() {
            LinkedHashSetDemo.ConfigurationManager configManager = 
                new LinkedHashSetDemo.ConfigurationManager();
            
            // 添加配置
            configManager.addConfig("db.url", "jdbc:mysql://localhost:3306/test");
            configManager.addConfig("db.username", "root");
            configManager.addConfig("db.password", "password");
            configManager.addConfig("server.port", "8080");
            
            // 验证配置数量和顺序
            assertEquals(4, configManager.getConfigCount());
            Set<String> keys = configManager.getConfigKeys();
            List<String> keyList = new ArrayList<>(keys);
            assertEquals(Arrays.asList("db.url", "db.username", "db.password", "server.port"), keyList);
            
            // 测试获取配置
            assertEquals("8080", configManager.getConfig("server.port"));
            assertEquals("root", configManager.getConfig("db.username"));
            assertNull(configManager.getConfig("nonexistent"));
            
            // 测试更新配置（不改变顺序）
            configManager.updateConfig("server.port", "9090");
            assertEquals("9090", configManager.getConfig("server.port"));
            
            // 验证顺序没有改变
            Set<String> updatedKeys = configManager.getConfigKeys();
            List<String> updatedKeyList = new ArrayList<>(updatedKeys);
            assertEquals(Arrays.asList("db.url", "db.username", "db.password", "server.port"), updatedKeyList);
            
            // 测试删除配置
            assertTrue(configManager.removeConfig("db.password"));
            assertFalse(configManager.removeConfig("nonexistent"));
            assertEquals(3, configManager.getConfigCount());
            assertNull(configManager.getConfig("db.password"));
            
            // 测试清空
            configManager.clear();
            assertEquals(0, configManager.getConfigCount());
            assertTrue(configManager.getConfigKeys().isEmpty());
        }

        @Test
        @DisplayName("ConfigurationManager边界条件测试")
        void testConfigurationManagerBoundaryConditions() {
            LinkedHashSetDemo.ConfigurationManager configManager = 
                new LinkedHashSetDemo.ConfigurationManager();
            
            // 测试空键和null键
            configManager.addConfig("", "value");
            configManager.addConfig(null, "value");
            configManager.addConfig("   ", "value");
            assertEquals(0, configManager.getConfigCount());
            
            // 测试有效配置
            configManager.addConfig("key1", "value1");
            assertEquals(1, configManager.getConfigCount());
            
            // 测试更新不存在的配置
            configManager.updateConfig("nonexistent", "value");
            assertEquals(1, configManager.getConfigCount());
            assertNull(configManager.getConfig("nonexistent"));
        }
    }

    @Nested
    @DisplayName("边界条件测试")
    class BoundaryConditionTest {

        @Test
        @DisplayName("空集合操作测试")
        void testEmptySetOperations() {
            LinkedHashSet<String> emptySet = new LinkedHashSet<>();
            
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
            
            // 转换为数组
            String[] array = emptySet.toArray(new String[0]);
            assertEquals(0, array.length);
        }

        @Test
        @DisplayName("单元素集合操作测试")
        void testSingleElementSetOperations() {
            LinkedHashSet<String> singleSet = new LinkedHashSet<>();
            singleSet.add("single");
            
            assertEquals(1, singleSet.size());
            assertFalse(singleSet.isEmpty());
            assertTrue(singleSet.contains("single"));
            
            // 单元素迭代
            int count = 0;
            for (String item : singleSet) {
                assertEquals("single", item);
                count++;
            }
            assertEquals(1, count);
            
            // 转换为列表验证顺序
            List<String> list = new ArrayList<>(singleSet);
            assertEquals(Arrays.asList("single"), list);
        }

        @Test
        @DisplayName("null值处理测试")
        void testNullValueHandling() {
            LinkedHashSet<String> set = new LinkedHashSet<>();
            
            // LinkedHashSet允许一个null值
            assertTrue(set.add(null));
            assertFalse(set.add(null)); // 重复的null
            
            assertEquals(1, set.size());
            assertTrue(set.contains(null));
            
            // 添加其他元素，验证null的位置
            set.add("first");
            set.add("second");
            
            List<String> list = new ArrayList<>(set);
            assertEquals(null, list.get(0)); // null在第一位
            assertEquals("first", list.get(1));
            assertEquals("second", list.get(2));
            
            // 删除null
            assertTrue(set.remove(null));
            assertEquals(2, set.size());
            assertFalse(set.contains(null));
        }

        @Test
        @DisplayName("大数据量操作测试")
        void testLargeDataOperations() {
            LinkedHashSet<Integer> set = new LinkedHashSet<>();
            int size = 1000;
            
            // 大量插入
            for (int i = 0; i < size; i++) {
                assertTrue(set.add(i));
            }
            assertEquals(size, set.size());
            
            // 验证顺序
            int expected = 0;
            for (Integer actual : set) {
                assertEquals(expected++, actual.intValue());
            }
            
            // 大量查找
            for (int i = 0; i < size; i++) {
                assertTrue(set.contains(i));
            }
            
            // 大量删除
            for (int i = 0; i < size / 2; i++) {
                assertTrue(set.remove(i));
            }
            assertEquals(size / 2, set.size());
            
            // 验证剩余元素的顺序
            expected = size / 2;
            for (Integer actual : set) {
                assertEquals(expected++, actual.intValue());
            }
        }
    }

    @Nested
    @DisplayName("迭代器测试")
    class IteratorTest {

        @Test
        @DisplayName("迭代器顺序测试")
        void testIteratorOrder() {
            LinkedHashSet<String> set = new LinkedHashSet<>();
            String[] elements = {"A", "B", "C", "D", "E"};
            
            for (String element : elements) {
                set.add(element);
            }
            
            Iterator<String> iterator = set.iterator();
            for (String expected : elements) {
                assertTrue(iterator.hasNext());
                assertEquals(expected, iterator.next());
            }
            assertFalse(iterator.hasNext());
        }

        @Test
        @DisplayName("迭代器删除功能测试")
        void testIteratorRemoval() {
            LinkedHashSet<Integer> set = new LinkedHashSet<>();
            set.addAll(Arrays.asList(1, 2, 3, 4, 5));
            
            Iterator<Integer> iterator = set.iterator();
            while (iterator.hasNext()) {
                Integer element = iterator.next();
                if (element % 2 == 0) {
                    iterator.remove(); // 删除偶数
                }
            }
            
            assertEquals(3, set.size());
            List<Integer> remaining = new ArrayList<>(set);
            assertEquals(Arrays.asList(1, 3, 5), remaining);
        }

        @Test
        @DisplayName("并发修改异常测试")
        void testConcurrentModificationException() {
            LinkedHashSet<Integer> set = new LinkedHashSet<>();
            set.addAll(Arrays.asList(1, 2, 3, 4, 5));
            
            Iterator<Integer> iterator = set.iterator();
            iterator.next(); // 开始迭代
            
            set.add(6); // 在迭代过程中修改集合
            
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
        @DisplayName("性能特性演示方法测试")
        void testDemonstratePerformanceCharacteristics() {
            assertDoesNotThrow(() -> demo.demonstratePerformanceCharacteristics());
        }

        @Test
        @DisplayName("应用场景演示方法测试")
        void testDemonstrateUseCases() {
            assertDoesNotThrow(() -> demo.demonstrateUseCases());
        }

        @Test
        @DisplayName("综合演示方法测试")
        void testComprehensiveDemo() {
            assertDoesNotThrow(() -> demo.comprehensiveDemo());
        }
    }

    @Nested
    @DisplayName("集合运算测试")
    class SetOperationsTest {

        @Test
        @DisplayName("交集运算测试")
        void testIntersection() {
            LinkedHashSet<Integer> set1 = new LinkedHashSet<>(Arrays.asList(1, 2, 3, 4, 5));
            LinkedHashSet<Integer> set2 = new LinkedHashSet<>(Arrays.asList(4, 5, 6, 7, 8));
            
            LinkedHashSet<Integer> intersection = new LinkedHashSet<>(set1);
            intersection.retainAll(set2);
            
            assertEquals(Arrays.asList(4, 5), new ArrayList<>(intersection));
        }

        @Test
        @DisplayName("并集运算测试")
        void testUnion() {
            LinkedHashSet<Integer> set1 = new LinkedHashSet<>(Arrays.asList(1, 2, 3));
            LinkedHashSet<Integer> set2 = new LinkedHashSet<>(Arrays.asList(3, 4, 5));
            
            LinkedHashSet<Integer> union = new LinkedHashSet<>(set1);
            union.addAll(set2);
            
            // 验证顺序：set1的元素在前，set2的新元素在后
            assertEquals(Arrays.asList(1, 2, 3, 4, 5), new ArrayList<>(union));
        }

        @Test
        @DisplayName("差集运算测试")
        void testDifference() {
            LinkedHashSet<Integer> set1 = new LinkedHashSet<>(Arrays.asList(1, 2, 3, 4, 5));
            LinkedHashSet<Integer> set2 = new LinkedHashSet<>(Arrays.asList(3, 4, 5, 6, 7));
            
            LinkedHashSet<Integer> difference = new LinkedHashSet<>(set1);
            difference.removeAll(set2);
            
            assertEquals(Arrays.asList(1, 2), new ArrayList<>(difference));
        }
    }
}