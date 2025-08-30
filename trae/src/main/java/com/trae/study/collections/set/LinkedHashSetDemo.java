package com.trae.study.collections.set;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * LinkedHashSet 深度解析与演示
 * 
 * LinkedHashSet 是 HashSet 的子类，维护了元素的插入顺序或访问顺序。
 * 它结合了 HashMap 的快速访问特性和 LinkedList 的有序性。
 * 
 * 核心特性：
 * 1. 基于 LinkedHashMap 实现
 * 2. 维护插入顺序
 * 3. 允许一个 null 值
 * 4. 非线程安全
 * 5. 迭代性能优于 HashSet（当容量远大于元素数量时）
 * 
 * @author Trae
 * @since 2024-01-20
 */
public class LinkedHashSetDemo {

    /**
 * 演示 LinkedHashSet 的基本特性
 * 重点展示与 HashSet 的区别：有序性
 */
    public void demonstrateBasicFeatures() {
        System.out.println("=== LinkedHashSet 基本特性演示 ===");
        
        // 1. 插入顺序保持
        System.out.println("\n1. 插入顺序保持：");
        Set<String> linkedHashSet = new LinkedHashSet<>();
        Set<String> hashSet = new HashSet<>();
        
        String[] fruits = {"Apple", "Banana", "Cherry", "Date", "Elderberry"};
        
        for (String fruit : fruits) {
            linkedHashSet.add(fruit);
            hashSet.add(fruit);
        }
        
        System.out.println("LinkedHashSet (保持插入顺序): " + linkedHashSet);
        System.out.println("HashSet (无序): " + hashSet);
        
        // 2. 迭代顺序一致性
        System.out.println("\n2. 迭代顺序一致性：");
        System.out.print("LinkedHashSet 迭代: ");
        for (String fruit : linkedHashSet) {
            System.out.print(fruit + " ");
        }
        System.out.println();
        
        // 3. 重复元素不影响顺序
        System.out.println("\n3. 重复元素不影响顺序：");
        linkedHashSet.add("Apple"); // 重复添加
        System.out.println("再次添加 Apple 后: " + linkedHashSet);
        
        // 4. 删除和重新添加会改变顺序
        System.out.println("\n4. 删除和重新添加的影响：");
        linkedHashSet.remove("Banana");
        linkedHashSet.add("Banana");
        System.out.println("删除并重新添加 Banana 后: " + linkedHashSet);
    }

    /**
     * 分析 LinkedHashSet 的内部结构
     * 通过反射查看其基于 LinkedHashMap 的实现
     */
    public void analyzeInternalStructure() {
        System.out.println("\n=== LinkedHashSet 内部结构分析 ===");
        
        try {
            LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
            linkedHashSet.addAll(Arrays.asList("A", "B", "C", "D"));
            
            // 1. 获取内部 LinkedHashMap
            Field mapField = HashSet.class.getDeclaredField("map");
            mapField.setAccessible(true);
            LinkedHashMap<String, Object> internalMap = 
                (LinkedHashMap<String, Object>) mapField.get(linkedHashSet);
            
            System.out.println("\n1. 内部 LinkedHashMap 信息：");
            System.out.println("类型: " + internalMap.getClass().getSimpleName());
            System.out.println("大小: " + internalMap.size());
            System.out.println("键集合: " + internalMap.keySet());
            
            // 2. 验证访问顺序模式
            System.out.println("\n2. 访问顺序模式检查：");
            Field accessOrderField = LinkedHashMap.class.getDeclaredField("accessOrder");
            accessOrderField.setAccessible(true);
            boolean accessOrder = accessOrderField.getBoolean(internalMap);
            System.out.println("访问顺序模式: " + (accessOrder ? "是" : "否（插入顺序）"));
            
            // 3. 链表结构信息
            System.out.println("\n3. 链表结构信息：");
            Field headerField = LinkedHashMap.class.getDeclaredField("header");
            if (headerField != null) {
                headerField.setAccessible(true);
                Object header = headerField.get(internalMap);
                System.out.println("头节点: " + (header != null ? "存在" : "不存在"));
            }
            
        } catch (Exception e) {
            System.out.println("反射分析失败: " + e.getMessage());
            // 降级到基本信息展示
            LinkedHashSet<String> set = new LinkedHashSet<>();
            System.out.println("LinkedHashSet 类层次: " + set.getClass().getSuperclass().getSimpleName());
        }
    }

    /**
     * 演示 LinkedHashSet 的性能特性
     * 对比不同场景下的性能表现
     */
    public void demonstratePerformanceCharacteristics() {
        System.out.println("\n=== LinkedHashSet 性能特性演示 ===");
        
        // 1. 迭代性能对比
        System.out.println("\n1. 迭代性能对比：");
        benchmarkIteration();
        
        // 2. 插入性能对比
        System.out.println("\n2. 插入性能对比：");
        benchmarkInsertion();
        
        // 3. 查找性能对比
        System.out.println("\n3. 查找性能对比：");
        benchmarkLookup();
        
        // 4. 内存使用对比
        System.out.println("\n4. 内存使用分析：");
        analyzeMemoryUsage();
    }

    /**
     * 迭代性能基准测试
     */
    private void benchmarkIteration() {
        int size = 10000;
        
        // 准备数据
        List<Integer> data = IntStream.range(0, size).boxed().collect(Collectors.toList());
        Collections.shuffle(data);
        
        Set<Integer> linkedHashSet = new LinkedHashSet<>(data);
        Set<Integer> hashSet = new HashSet<>(data);
        Set<Integer> treeSet = new TreeSet<>(data);
        
        // LinkedHashSet 迭代
        long startTime = System.nanoTime();
        int count = 0;
        for (Integer item : linkedHashSet) {
            count++;
        }
        long linkedHashSetTime = System.nanoTime() - startTime;
        
        // HashSet 迭代
        startTime = System.nanoTime();
        count = 0;
        for (Integer item : hashSet) {
            count++;
        }
        long hashSetTime = System.nanoTime() - startTime;
        
        // TreeSet 迭代
        startTime = System.nanoTime();
        count = 0;
        for (Integer item : treeSet) {
            count++;
        }
        long treeSetTime = System.nanoTime() - startTime;
        
        System.out.printf("LinkedHashSet 迭代: %,d ns%n", linkedHashSetTime);
        System.out.printf("HashSet 迭代: %,d ns%n", hashSetTime);
        System.out.printf("TreeSet 迭代: %,d ns%n", treeSetTime);
        
        // 性能比较
        double linkedVsHash = (double) linkedHashSetTime / hashSetTime;
        System.out.printf("LinkedHashSet vs HashSet: %.2fx%n", linkedVsHash);
    }

    /**
     * 插入性能基准测试
     */
    private void benchmarkInsertion() {
        int size = 10000;
        List<Integer> data = IntStream.range(0, size).boxed().collect(Collectors.toList());
        Collections.shuffle(data);
        
        // LinkedHashSet 插入
        long startTime = System.nanoTime();
        Set<Integer> linkedHashSet = new LinkedHashSet<>();
        for (Integer item : data) {
            linkedHashSet.add(item);
        }
        long linkedHashSetTime = System.nanoTime() - startTime;
        
        // HashSet 插入
        startTime = System.nanoTime();
        Set<Integer> hashSet = new HashSet<>();
        for (Integer item : data) {
            hashSet.add(item);
        }
        long hashSetTime = System.nanoTime() - startTime;
        
        System.out.printf("LinkedHashSet 插入: %,d ns%n", linkedHashSetTime);
        System.out.printf("HashSet 插入: %,d ns%n", hashSetTime);
        
        double ratio = (double) linkedHashSetTime / hashSetTime;
        System.out.printf("性能比率 (LinkedHashSet/HashSet): %.2fx%n", ratio);
    }

    /**
     * 查找性能基准测试
     */
    private void benchmarkLookup() {
        int size = 10000;
        List<Integer> data = IntStream.range(0, size).boxed().collect(Collectors.toList());
        
        Set<Integer> linkedHashSet = new LinkedHashSet<>(data);
        Set<Integer> hashSet = new HashSet<>(data);
        
        // 准备查找数据
        List<Integer> lookupData = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            lookupData.add(RandomUtil.randomInt(0, size));
        }
        
        // LinkedHashSet 查找
        long startTime = System.nanoTime();
        int found = 0;
        for (Integer item : lookupData) {
            if (linkedHashSet.contains(item)) {
                found++;
            }
        }
        long linkedHashSetTime = System.nanoTime() - startTime;
        
        // HashSet 查找
        startTime = System.nanoTime();
        found = 0;
        for (Integer item : lookupData) {
            if (hashSet.contains(item)) {
                found++;
            }
        }
        long hashSetTime = System.nanoTime() - startTime;
        
        System.out.printf("LinkedHashSet 查找: %,d ns%n", linkedHashSetTime);
        System.out.printf("HashSet 查找: %,d ns%n", hashSetTime);
        
        double ratio = (double) linkedHashSetTime / hashSetTime;
        System.out.printf("性能比率 (LinkedHashSet/HashSet): %.2fx%n", ratio);
    }

    /**
     * 内存使用分析
     */
    private void analyzeMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        int size = 10000;
        
        // 测试 LinkedHashSet
        runtime.gc();
        long beforeLinkedHashSet = runtime.totalMemory() - runtime.freeMemory();
        
        Set<Integer> linkedHashSet = new LinkedHashSet<>();
        for (int i = 0; i < size; i++) {
            linkedHashSet.add(i);
        }
        
        long afterLinkedHashSet = runtime.totalMemory() - runtime.freeMemory();
        long linkedHashSetMemory = afterLinkedHashSet - beforeLinkedHashSet;
        
        // 测试 HashSet
        runtime.gc();
        long beforeHashSet = runtime.totalMemory() - runtime.freeMemory();
        
        Set<Integer> hashSet = new HashSet<>();
        for (int i = 0; i < size; i++) {
            hashSet.add(i);
        }
        
        long afterHashSet = runtime.totalMemory() - runtime.freeMemory();
        long hashSetMemory = afterHashSet - beforeHashSet;
        
        System.out.printf("LinkedHashSet 内存使用: %,d bytes%n", linkedHashSetMemory);
        System.out.printf("HashSet 内存使用: %,d bytes%n", hashSetMemory);
        
        if (hashSetMemory > 0) {
            double ratio = (double) linkedHashSetMemory / hashSetMemory;
            System.out.printf("内存使用比率 (LinkedHashSet/HashSet): %.2fx%n", ratio);
        }
        
        // 每个元素的平均内存使用
        System.out.printf("LinkedHashSet 每元素内存: %.2f bytes%n", 
            (double) linkedHashSetMemory / size);
        System.out.printf("HashSet 每元素内存: %.2f bytes%n", 
            (double) hashSetMemory / size);
    }

    /**
     * 演示 LinkedHashSet 的实际应用场景
     */
    public void demonstrateUseCases() {
        System.out.println("\n=== LinkedHashSet 实际应用场景 ===");
        
        // 1. 去重但保持顺序
        System.out.println("\n1. 去重但保持顺序：");
        demonstrateDeduplicationWithOrder();
        
        // 2. 缓存最近访问的项目
        System.out.println("\n2. 有序的唯一元素集合：");
        demonstrateOrderedUniqueCollection();
        
        // 3. 配置项管理
        System.out.println("\n3. 配置项管理：");
        demonstrateConfigurationManagement();
    }

    /**
     * 去重但保持顺序的应用
     */
    private void demonstrateDeduplicationWithOrder() {
        // 模拟用户浏览历史
        List<String> browsingHistory = Arrays.asList(
            "首页", "产品页", "详情页", "首页", "购物车", "详情页", "结算页", "首页"
        );
        
        System.out.println("原始浏览历史: " + browsingHistory);
        
        // 使用 LinkedHashSet 去重但保持首次访问顺序
        Set<String> uniquePages = new LinkedHashSet<>(browsingHistory);
        System.out.println("去重后的页面访问顺序: " + uniquePages);
        
        // 转换回 List 以便进一步处理
        List<String> orderedUniquePages = new ArrayList<>(uniquePages);
        System.out.println("有序唯一页面列表: " + orderedUniquePages);
    }

    /**
     * 有序的唯一元素集合应用
     */
    private void demonstrateOrderedUniqueCollection() {
        // 模拟标签系统
        TagManager tagManager = new TagManager();
        
        tagManager.addTag("Java");
        tagManager.addTag("Spring");
        tagManager.addTag("Database");
        tagManager.addTag("Java"); // 重复
        tagManager.addTag("Redis");
        tagManager.addTag("Spring"); // 重复
        tagManager.addTag("Docker");
        
        System.out.println("标签管理器状态:");
        tagManager.displayTags();
        
        // 获取前3个标签
        List<String> topTags = tagManager.getTopTags(3);
        System.out.println("前3个标签: " + topTags);
    }

    /**
     * 配置项管理应用
     */
    private void demonstrateConfigurationManagement() {
        ConfigurationManager configManager = new ConfigurationManager();
        
        // 添加配置项（按重要性顺序）
        configManager.addConfig("database.url", "jdbc:mysql://localhost:3306/test");
        configManager.addConfig("database.username", "root");
        configManager.addConfig("database.password", "password");
        configManager.addConfig("server.port", "8080");
        configManager.addConfig("logging.level", "INFO");
        
        System.out.println("配置管理器状态:");
        configManager.displayConfigs();
        
        // 更新配置（不改变顺序）
        configManager.updateConfig("server.port", "9090");
        System.out.println("\n更新端口后:");
        configManager.displayConfigs();
        
        // 删除并重新添加（会改变顺序）
        configManager.removeConfig("logging.level");
        configManager.addConfig("logging.level", "DEBUG");
        System.out.println("\n删除并重新添加日志级别后:");
        configManager.displayConfigs();
    }

    /**
     * 综合演示
     */
    public void comprehensiveDemo() {
        System.out.println("\n=== LinkedHashSet 综合演示 ===");
        
        demonstrateBasicFeatures();
        analyzeInternalStructure();
        demonstratePerformanceCharacteristics();
        demonstrateUseCases();
        
        System.out.println("\n=== 总结 ===");
        System.out.println("LinkedHashSet 适用场景:");
        System.out.println("1. 需要去重但保持插入顺序的场景");
        System.out.println("2. 频繁迭代且对顺序有要求的场景");
        System.out.println("3. 缓存最近使用项目（配合 LinkedHashMap 的访问顺序）");
        System.out.println("4. 配置项、标签等需要有序管理的场景");
        
        System.out.println("\n性能特点:");
        System.out.println("1. 插入、删除、查找: O(1) 平均时间复杂度");
        System.out.println("2. 迭代性能优于 HashSet（当容量 >> 元素数量时）");
        System.out.println("3. 内存开销略高于 HashSet（维护链表结构）");
        System.out.println("4. 非线程安全，需要外部同步");
    }

    /**
     * 标签管理器 - 演示 LinkedHashSet 在实际应用中的使用
     */
    public static class TagManager {
        private final Set<String> tags = new LinkedHashSet<>();
        
        public void addTag(String tag) {
            if (StrUtil.isNotBlank(tag)) {
                tags.add(tag.trim());
            }
        }
        
        public boolean removeTag(String tag) {
            return tags.remove(tag);
        }
        
        public boolean hasTag(String tag) {
            return tags.contains(tag);
        }
        
        public List<String> getAllTags() {
            return new ArrayList<>(tags);
        }
        
        public List<String> getTopTags(int count) {
            return tags.stream()
                .limit(count)
                .collect(Collectors.toList());
        }
        
        public int getTagCount() {
            return tags.size();
        }
        
        public void displayTags() {
            System.out.println("标签列表 (" + tags.size() + "): " + tags);
        }
        
        public void clear() {
            tags.clear();
        }
    }

    /**
     * 配置管理器 - 演示有序配置项管理
     */
    public static class ConfigurationManager {
        private final Map<String, String> configs = new LinkedHashMap<>();
        private final Set<String> configKeys = new LinkedHashSet<>();
        
        public void addConfig(String key, String value) {
            if (StrUtil.isNotBlank(key)) {
                configs.put(key, value);
                configKeys.add(key);
            }
        }
        
        public void updateConfig(String key, String value) {
            if (configs.containsKey(key)) {
                configs.put(key, value);
                // 不改变 key 的顺序
            }
        }
        
        public boolean removeConfig(String key) {
            boolean removed = configs.remove(key) != null;
            configKeys.remove(key);
            return removed;
        }
        
        public String getConfig(String key) {
            return configs.get(key);
        }
        
        public Set<String> getConfigKeys() {
            return new LinkedHashSet<>(configKeys);
        }
        
        public void displayConfigs() {
            System.out.println("配置项 (" + configs.size() + "):");
            for (String key : configKeys) {
                System.out.println("  " + key + " = " + configs.get(key));
            }
        }
        
        public int getConfigCount() {
            return configs.size();
        }
        
        public void clear() {
            configs.clear();
            configKeys.clear();
        }
    }

    /**
     * 主方法 - 运行所有演示
     */
    public static void main(String[] args) {
        LinkedHashSetDemo demo = new LinkedHashSetDemo();
        demo.comprehensiveDemo();
    }
}