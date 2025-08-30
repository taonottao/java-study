package com.study.collections.list;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentModificationException;

/**
 * ArrayList 源码级演示
 * 深入理解 ArrayList 的核心机制：
 * 1. 扩容机制（grow、ensureCapacity）
 * 2. fail-fast 行为（modCount）
 * 3. subList 视图
 * 4. 内部数组操作
 * 5. 性能特性分析
 */
@Slf4j
public class ArrayListSourceDemo {
    
    /**
     * ArrayList 扩容机制演示结果
     */
    @Data
    public static class GrowthAnalysisResult {
        private int initialCapacity;
        private int finalCapacity;
        private int growthCount;
        private List<Integer> capacityHistory;
        private long totalTimeMs;
        
        public GrowthAnalysisResult() {
            this.capacityHistory = new ArrayList<>();
        }
    }
    
    /**
     * fail-fast 行为演示结果
     */
    @Data
    public static class FailFastResult {
        private boolean exceptionThrown;
        private String exceptionType;
        private String exceptionMessage;
        private int modCountAtException;
        private String operationType;
    }
    
    /**
     * subList 视图演示结果
     */
    @Data
    public static class SubListResult {
        private List<String> originalList;
        private List<String> subListView;
        private List<String> originalAfterSubListModification;
        private boolean isViewUpdated;
        private String operation;
    }
    
    /**
     * 演示 ArrayList 的扩容机制
     * 通过反射获取内部数组容量，观察扩容过程
     */
    public static GrowthAnalysisResult demonstrateGrowthMechanism(int targetSize) {
        log.info("开始演示 ArrayList 扩容机制，目标大小: {}", targetSize);
        
        GrowthAnalysisResult result = new GrowthAnalysisResult();
        long startTime = System.currentTimeMillis();
        
        // 创建空的 ArrayList（默认容量为10）
        ArrayList<Integer> list = new ArrayList<>();
        
        try {
            // 通过反射获取内部数组
            java.lang.reflect.Field elementDataField = ArrayList.class.getDeclaredField("elementData");
            elementDataField.setAccessible(true);
            
            int previousCapacity = 0;
            int growthCount = 0;
            
            for (int i = 0; i < targetSize; i++) {
                list.add(i);
                
                // 获取当前容量
                Object[] elementData = (Object[]) elementDataField.get(list);
                int currentCapacity = elementData.length;
                
                // 检查是否发生了扩容
                if (currentCapacity != previousCapacity) {
                    if (i == 0) {
                        result.setInitialCapacity(currentCapacity);
                    }
                    result.getCapacityHistory().add(currentCapacity);
                    
                    if (previousCapacity > 0) {
                        growthCount++;
                        log.debug("第{}次扩容: {} -> {}, 扩容因子: {:.2f}", 
                                growthCount, previousCapacity, currentCapacity, 
                                (double) currentCapacity / previousCapacity);
                    }
                    
                    previousCapacity = currentCapacity;
                }
            }
            
            result.setFinalCapacity(previousCapacity);
            result.setGrowthCount(growthCount);
            
        } catch (Exception e) {
            log.error("反射获取容量失败", e);
        }
        
        result.setTotalTimeMs(System.currentTimeMillis() - startTime);
        
        log.info("扩容分析完成: 初始容量={}, 最终容量={}, 扩容次数={}, 耗时={}ms", 
                result.getInitialCapacity(), result.getFinalCapacity(), 
                result.getGrowthCount(), result.getTotalTimeMs());
        
        return result;
    }
    
    /**
     * 演示 ensureCapacity 的性能优化效果
     */
    public static Map<String, Long> demonstrateEnsureCapacityPerformance(int targetSize) {
        log.info("开始演示 ensureCapacity 性能优化，目标大小: {}", targetSize);
        
        Map<String, Long> results = new HashMap<>();
        
        // 测试1: 不使用 ensureCapacity
        long startTime = System.currentTimeMillis();
        ArrayList<Integer> listWithoutEnsure = new ArrayList<>();
        for (int i = 0; i < targetSize; i++) {
            listWithoutEnsure.add(i);
        }
        long timeWithoutEnsure = System.currentTimeMillis() - startTime;
        results.put("withoutEnsureCapacity", timeWithoutEnsure);
        
        // 测试2: 使用 ensureCapacity
        startTime = System.currentTimeMillis();
        ArrayList<Integer> listWithEnsure = new ArrayList<>();
        listWithEnsure.ensureCapacity(targetSize); // 预分配容量
        for (int i = 0; i < targetSize; i++) {
            listWithEnsure.add(i);
        }
        long timeWithEnsure = System.currentTimeMillis() - startTime;
        results.put("withEnsureCapacity", timeWithEnsure);
        
        // 计算性能提升
        double improvement = ((double) timeWithoutEnsure - timeWithEnsure) / timeWithoutEnsure * 100;
        results.put("improvementPercent", (long) improvement);
        
        log.info("性能对比: 不预分配={}ms, 预分配={}ms, 性能提升={:.1f}%", 
                timeWithoutEnsure, timeWithEnsure, improvement);
        
        return results;
    }
    
    /**
     * 演示 fail-fast 机制
     * 在迭代过程中修改集合，触发 ConcurrentModificationException
     */
    public static FailFastResult demonstrateFailFastBehavior() {
        log.info("开始演示 fail-fast 机制");
        
        FailFastResult result = new FailFastResult();
        ArrayList<String> list = new ArrayList<>();
        list.add("item1");
        list.add("item2");
        list.add("item3");
        list.add("item4");
        
        try {
            // 获取迭代器
            Iterator<String> iterator = list.iterator();
            
            // 开始迭代
            while (iterator.hasNext()) {
                String item = iterator.next();
                log.debug("迭代到: {}", item);
                
                // 在迭代过程中修改集合（这会触发 fail-fast）
                if ("item2".equals(item)) {
                    list.add("newItem"); // 直接修改集合，而不是通过迭代器
                    result.setOperationType("add during iteration");
                }
            }
            
        } catch (ConcurrentModificationException e) {
            result.setExceptionThrown(true);
            result.setExceptionType(e.getClass().getSimpleName());
            result.setExceptionMessage(e.getMessage());
            
            // 尝试通过反射获取 modCount（仅用于演示）
            try {
                java.lang.reflect.Field modCountField = ArrayList.class.getSuperclass().getDeclaredField("modCount");
                modCountField.setAccessible(true);
                result.setModCountAtException((Integer) modCountField.get(list));
            } catch (Exception ex) {
                log.warn("无法获取 modCount", ex);
            }
            
            log.info("成功触发 fail-fast: {}", e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 演示正确的迭代中删除方式
     */
    public static List<String> demonstrateSafeIteratorRemoval() {
        log.info("演示安全的迭代器删除操作");
        
        ArrayList<String> list = new ArrayList<>();
        list.add("keep1");
        list.add("remove1");
        list.add("keep2");
        list.add("remove2");
        list.add("keep3");
        
        log.debug("原始列表: {}", list);
        
        // 使用迭代器安全删除
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String item = iterator.next();
            if (item.startsWith("remove")) {
                iterator.remove(); // 使用迭代器的 remove 方法
                log.debug("安全删除: {}", item);
            }
        }
        
        log.info("删除后列表: {}", list);
        return new ArrayList<>(list);
    }
    
    /**
     * 演示 subList 视图的特性
     * subList 返回的是原列表的视图，修改会相互影响
     */
    public static SubListResult demonstrateSubListView() {
        log.info("开始演示 subList 视图特性");
        
        SubListResult result = new SubListResult();
        
        // 创建原始列表
        ArrayList<String> originalList = new ArrayList<>();
        originalList.add("A");
        originalList.add("B");
        originalList.add("C");
        originalList.add("D");
        originalList.add("E");
        
        result.setOriginalList(new ArrayList<>(originalList));
        log.debug("原始列表: {}", originalList);
        
        // 获取子列表视图（索引 1 到 3，不包含 3）
        List<String> subList = originalList.subList(1, 4); // [B, C, D]
        result.setSubListView(new ArrayList<>(subList));
        log.debug("子列表视图: {}", subList);
        
        // 修改子列表
        subList.set(1, "C_MODIFIED"); // 修改 C 为 C_MODIFIED
        result.setOperation("subList.set(1, \"C_MODIFIED\")");
        
        // 检查原始列表是否受影响
        result.setOriginalAfterSubListModification(new ArrayList<>(originalList));
        result.setIsViewUpdated(!originalList.get(2).equals("C"));
        
        log.info("修改子列表后，原始列表: {}", originalList);
        log.info("子列表确实是视图: {}", result.isIsViewUpdated());
        
        return result;
    }
    
    /**
     * 演示 ArrayList 的内部数组操作
     */
    public static Map<String, Object> demonstrateInternalArrayOperations() {
        log.info("演示 ArrayList 内部数组操作");
        
        Map<String, Object> results = new HashMap<>();
        ArrayList<String> list = new ArrayList<>();
        
        // 添加元素
        list.add("first");
        list.add("second");
        list.add("third");
        
        try {
            // 通过反射访问内部数组
            java.lang.reflect.Field elementDataField = ArrayList.class.getDeclaredField("elementData");
            elementDataField.setAccessible(true);
            Object[] elementData = (Object[]) elementDataField.get(list);
            
            results.put("arrayLength", elementData.length);
            results.put("listSize", list.size());
            results.put("unusedSlots", elementData.length - list.size());
            
            // 显示数组内容（包括未使用的槽位）
            List<String> arrayContent = new ArrayList<>();
            for (int i = 0; i < elementData.length; i++) {
                arrayContent.add(i < list.size() ? (String) elementData[i] : "null");
            }
            results.put("arrayContent", arrayContent);
            
            log.info("内部数组长度: {}, 列表大小: {}, 未使用槽位: {}", 
                    elementData.length, list.size(), elementData.length - list.size());
            log.debug("数组内容: {}", arrayContent);
            
        } catch (Exception e) {
            log.error("反射访问失败", e);
        }
        
        return results;
    }
    
    /**
     * 演示 ArrayList 的性能特性
     */
    public static Map<String, Long> demonstratePerformanceCharacteristics(int size) {
        log.info("演示 ArrayList 性能特性，数据量: {}", size);
        
        Map<String, Long> results = new HashMap<>();
        ArrayList<Integer> list = new ArrayList<>();
        
        // 填充数据
        for (int i = 0; i < size; i++) {
            list.add(i);
        }
        
        // 测试随机访问性能 (get)
        long startTime = System.nanoTime();
        Random random = new Random(42); // 固定种子保证可重复性
        for (int i = 0; i < 10000; i++) {
            int index = random.nextInt(size);
            list.get(index);
        }
        long randomAccessTime = System.nanoTime() - startTime;
        results.put("randomAccessNanos", randomAccessTime);
        
        // 测试尾部插入性能 (add)
        startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            list.add(size + i);
        }
        long tailInsertTime = System.nanoTime() - startTime;
        results.put("tailInsertNanos", tailInsertTime);
        
        // 测试头部插入性能 (add(0, element))
        startTime = System.nanoTime();
        for (int i = 0; i < 100; i++) { // 较少次数，因为头部插入很慢
            list.add(0, -i);
        }
        long headInsertTime = System.nanoTime() - startTime;
        results.put("headInsertNanos", headInsertTime);
        
        // 测试中间删除性能 (remove)
        startTime = System.nanoTime();
        for (int i = 0; i < 100; i++) {
            if (!list.isEmpty()) {
                list.remove(list.size() / 2); // 删除中间元素
            }
        }
        long middleRemoveTime = System.nanoTime() - startTime;
        results.put("middleRemoveNanos", middleRemoveTime);
        
        log.info("性能测试完成 - 随机访问: {}ns, 尾部插入: {}ns, 头部插入: {}ns, 中间删除: {}ns", 
                randomAccessTime, tailInsertTime, headInsertTime, middleRemoveTime);
        
        return results;
    }
    
    /**
     * 综合演示方法
     */
    public static void runAllDemonstrations() {
        log.info("=== ArrayList 源码级演示开始 ===");
        
        // 1. 扩容机制演示
        GrowthAnalysisResult growthResult = demonstrateGrowthMechanism(100);
        
        // 2. ensureCapacity 性能优化
        Map<String, Long> capacityResults = demonstrateEnsureCapacityPerformance(50000);
        
        // 3. fail-fast 机制
        FailFastResult failFastResult = demonstrateFailFastBehavior();
        
        // 4. 安全的迭代器删除
        List<String> safeRemovalResult = demonstrateSafeIteratorRemoval();
        
        // 5. subList 视图
        SubListResult subListResult = demonstrateSubListView();
        
        // 6. 内部数组操作
        Map<String, Object> arrayResults = demonstrateInternalArrayOperations();
        
        // 7. 性能特性
        Map<String, Long> performanceResults = demonstratePerformanceCharacteristics(10000);
        
        log.info("=== ArrayList 源码级演示完成 ===");
        
        // 输出总结
        log.info("\n=== 演示结果总结 ===");
        log.info("扩容次数: {}, 最终容量: {}", growthResult.getGrowthCount(), growthResult.getFinalCapacity());
        log.info("ensureCapacity 性能提升: {}%", capacityResults.get("improvementPercent"));
        log.info("fail-fast 异常: {}", failFastResult.isExceptionThrown() ? "已触发" : "未触发");
        log.info("subList 视图特性: {}", subListResult.isIsViewUpdated() ? "确认" : "异常");
        log.info("安全删除结果: {}", safeRemovalResult);
    }
}