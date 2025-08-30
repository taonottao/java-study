package com.study.collections.map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.concurrent.ConcurrentModificationException;

/**
 * HashMap 源码演示测试类
 * 验证 HashMapSourceDemo 中各个演示方法的正确性
 */
class HashMapSourceDemoTest {
    
    @BeforeEach
    void setUp() {
        // 测试前的准备工作
    }
    
    @Test
    @DisplayName("测试哈希扰动函数演示")
    void testDemonstrateHashPerturbation() {
        // 执行哈希扰动演示
        Map<String, Object> results = HashMapSourceDemo.demonstrateHashPerturbation();
        
        // 验证返回结果的完整性
        assertNotNull(results, "哈希扰动结果不应为空");
        assertTrue(results.containsKey("originalHashes"), "应包含原始哈希值");
        assertTrue(results.containsKey("perturbedHashes"), "应包含扰动后哈希值");
        assertTrue(results.containsKey("bucketIndices"), "应包含桶索引");
        assertTrue(results.containsKey("bucketDistribution"), "应包含桶分布");
        
        // 验证哈希值映射
        @SuppressWarnings("unchecked")
        Map<String, Integer> originalHashes = (Map<String, Integer>) results.get("originalHashes");
        @SuppressWarnings("unchecked")
        Map<String, Integer> perturbedHashes = (Map<String, Integer>) results.get("perturbedHashes");
        @SuppressWarnings("unchecked")
        Map<String, Integer> bucketIndices = (Map<String, Integer>) results.get("bucketIndices");
        
        assertFalse(originalHashes.isEmpty(), "原始哈希值不应为空");
        assertEquals(originalHashes.size(), perturbedHashes.size(), "哈希值数量应一致");
        assertEquals(originalHashes.size(), bucketIndices.size(), "桶索引数量应一致");
        
        // 验证桶索引范围
        int capacity = 16; // 默认容量
        for (Integer bucketIndex : bucketIndices.values()) {
            assertTrue(bucketIndex >= 0 && bucketIndex < capacity, 
                    "桶索引应在有效范围内: " + bucketIndex);
        }
        
        // 验证统计信息
        assertTrue((Integer) results.get("maxCollisions") >= 1, "最大冲突数应 >= 1");
        assertTrue((Double) results.get("averageLoad") > 0, "平均负载应 > 0");
        assertEquals(16, results.get("totalBuckets"), "总桶数应为16");
        assertTrue((Integer) results.get("usedBuckets") > 0, "使用的桶数应 > 0");
    }
    
    @Test
    @DisplayName("测试Put操作流程演示")
    void testDemonstratePutOperations() {
        // 执行Put操作演示
        List<HashMapSourceDemo.PutOperationResult> results = HashMapSourceDemo.demonstratePutOperations();
        
        // 验证结果
        assertNotNull(results, "Put操作结果不应为空");
        assertFalse(results.isEmpty(), "Put操作结果应包含数据");
        
        // 验证每个操作结果
        for (HashMapSourceDemo.PutOperationResult result : results) {
            assertNotNull(result.getOperation(), "操作类型不应为空");
            assertTrue(Arrays.asList("new_entry", "update_existing", "collision_resolved")
                    .contains(result.getOperation()), "操作类型应为预定义值之一");
            
            assertTrue(result.getBucketIndex() >= 0, "桶索引应 >= 0");
            assertTrue(result.getOperationTimeNanos() >= 0, "操作时间应 >= 0");
        }
        
        // 验证是否有新条目插入
        long newEntryCount = results.stream()
                .filter(r -> "new_entry".equals(r.getOperation()))
                .count();
        assertTrue(newEntryCount > 0, "应该有新条目插入操作");
        
        // 验证是否有扩容发生
        boolean hasResize = results.stream().anyMatch(HashMapSourceDemo.PutOperationResult::isCausedResize);
        // 注意：扩容可能发生也可能不发生，取决于具体的键值和容量
    }
    
    @Test
    @DisplayName("测试扩容机制演示")
    void testDemonstrateResizeMechanism() {
        int targetSize = 50;
        
        // 执行扩容机制演示
        HashMapSourceDemo.ResizeAnalysisResult result = 
                HashMapSourceDemo.demonstrateResizeMechanism(targetSize);
        
        // 验证结果
        assertNotNull(result, "扩容分析结果不应为空");
        assertNotNull(result.getCapacityHistory(), "容量历史不应为空");
        assertNotNull(result.getSizeHistory(), "大小历史不应为空");
        assertNotNull(result.getLoadFactorHistory(), "负载因子历史不应为空");
        assertNotNull(result.getResizeDetails(), "扩容详情不应为空");
        
        // 验证扩容次数
        assertTrue(result.getResizeCount() >= 0, "扩容次数应 >= 0");
        
        // 如果发生了扩容，验证历史记录
        if (result.getResizeCount() > 0) {
            assertFalse(result.getCapacityHistory().isEmpty(), "容量历史应有记录");
            assertEquals(result.getCapacityHistory().size(), result.getSizeHistory().size(), 
                    "容量和大小历史记录数量应一致");
            assertEquals(result.getCapacityHistory().size(), result.getLoadFactorHistory().size(), 
                    "容量和负载因子历史记录数量应一致");
            
            // 验证容量递增
            List<Integer> capacities = result.getCapacityHistory();
            for (int i = 1; i < capacities.size(); i++) {
                assertTrue(capacities.get(i) > capacities.get(i-1), 
                        "容量应该递增: " + capacities.get(i-1) + " -> " + capacities.get(i));
            }
        }
        
        // 验证详细信息
        assertTrue((Integer) result.getResizeDetails().get("finalCapacity") > 0, "最终容量应 > 0");
        assertEquals(targetSize, result.getResizeDetails().get("finalSize"), "最终大小应等于目标大小");
        assertTrue((Double) result.getResizeDetails().get("finalLoadFactor") > 0, "最终负载因子应 > 0");
    }
    
    @Test
    @DisplayName("测试树化机制演示")
    void testDemonstrateTreeification() {
        // 执行树化机制演示
        HashMapSourceDemo.TreeifyAnalysisResult result = HashMapSourceDemo.demonstrateTreeification();
        
        // 验证结果
        assertNotNull(result, "树化分析结果不应为空");
        assertNotNull(result.getTreeStructureInfo(), "树结构信息不应为空");
        
        // 如果树化发生了，验证相关信息
        if (result.isTreeificationOccurred()) {
            assertTrue(result.getChainLengthBeforeTreeify() >= 8, 
                    "树化前链表长度应 >= 8: " + result.getChainLengthBeforeTreeify());
            assertTrue(result.getMapSizeAtTreeify() > 0, "树化时映射大小应 > 0");
            assertNotNull(result.getTriggerKey(), "触发键不应为空");
            assertTrue(result.getBucketIndex() >= 0, "桶索引应 >= 0");
        }
        
        // 验证树结构信息
        Map<String, Object> treeInfo = result.getTreeStructureInfo();
        if (!treeInfo.isEmpty()) {
            if (treeInfo.containsKey("treeNodes")) {
                assertTrue((Integer) treeInfo.get("treeNodes") >= 0, "树节点数应 >= 0");
            }
            if (treeInfo.containsKey("listNodes")) {
                assertTrue((Integer) treeInfo.get("listNodes") >= 0, "链表节点数应 >= 0");
            }
            if (treeInfo.containsKey("totalBuckets")) {
                assertTrue((Integer) treeInfo.get("totalBuckets") > 0, "总桶数应 > 0");
            }
        }
    }
    
    @Test
    @DisplayName("测试哈希分布分析")
    void testAnalyzeHashDistribution() {
        int elementCount = 100;
        
        // 执行哈希分布分析
        HashMapSourceDemo.HashDistributionResult result = 
                HashMapSourceDemo.analyzeHashDistribution(elementCount);
        
        // 验证结果
        assertNotNull(result, "哈希分布结果不应为空");
        assertTrue(result.getBucketCount() > 0, "桶数量应 > 0");
        assertEquals(elementCount, result.getTotalElements(), "总元素数应等于输入数量");
        assertTrue(result.getLoadFactor() > 0, "负载因子应 > 0");
        assertTrue(result.getMaxChainLength() >= 1, "最大链长应 >= 1");
        assertTrue(result.getAverageChainLength() > 0, "平均链长应 > 0");
        assertTrue(result.getEmptyBuckets() >= 0, "空桶数应 >= 0");
        
        // 验证桶大小映射
        assertNotNull(result.getBucketSizes(), "桶大小映射不应为空");
        
        // 验证哈希分析
        assertNotNull(result.getHashAnalysis(), "哈希分析不应为空");
        Map<String, Object> hashAnalysis = result.getHashAnalysis();
        if (hashAnalysis.containsKey("uniformityScore")) {
            Double uniformityScore = (Double) hashAnalysis.get("uniformityScore");
            assertTrue(uniformityScore >= 0 && uniformityScore <= 1, 
                    "均匀性得分应在[0,1]范围内: " + uniformityScore);
        }
        if (hashAnalysis.containsKey("collisionRate")) {
            Double collisionRate = (Double) hashAnalysis.get("collisionRate");
            assertTrue(collisionRate >= 0 && collisionRate <= 1, 
                    "冲突率应在[0,1]范围内: " + collisionRate);
        }
        
        // 验证数据一致性
        int totalElementsInBuckets = result.getBucketSizes().values().stream()
                .mapToInt(Integer::intValue).sum();
        assertEquals(elementCount, totalElementsInBuckets, "桶中元素总数应等于输入数量");
        
        assertTrue(result.getEmptyBuckets() + result.getBucketSizes().size() <= result.getBucketCount(),
                "空桶数 + 非空桶数应 <= 总桶数");
    }
    
    @Test
    @DisplayName("测试fail-fast机制演示")
    void testDemonstrateFailFast() {
        // 执行fail-fast演示
        boolean failFastTriggered = HashMapSourceDemo.demonstrateFailFast();
        
        // fail-fast应该被触发
        assertTrue(failFastTriggered, "fail-fast机制应该被触发");
    }
    
    @Test
    @DisplayName("测试CollidingKey类")
    void testCollidingKey() {
        // 创建测试键
        HashMapSourceDemo.CollidingKey key1 = new HashMapSourceDemo.CollidingKey("test1", 42);
        HashMapSourceDemo.CollidingKey key2 = new HashMapSourceDemo.CollidingKey("test2", 42);
        HashMapSourceDemo.CollidingKey key3 = new HashMapSourceDemo.CollidingKey("test1", 42);
        
        // 验证哈希值
        assertEquals(42, key1.hashCode(), "哈希值应为固定值");
        assertEquals(42, key2.hashCode(), "哈希值应为固定值");
        assertEquals(key1.hashCode(), key2.hashCode(), "相同固定哈希值的键应有相同哈希值");
        
        // 验证equals方法
        assertEquals(key1, key3, "相同名称的键应相等");
        assertNotEquals(key1, key2, "不同名称的键应不相等");
        assertNotEquals(key1, null, "键不应等于null");
        assertNotEquals(key1, "string", "键不应等于其他类型对象");
        
        // 验证getName方法
        assertEquals("test1", key1.getName(), "getName应返回正确名称");
        
        // 验证toString方法
        String toString = key1.toString();
        assertNotNull(toString, "toString不应为空");
        assertTrue(toString.contains("test1"), "toString应包含名称");
        assertTrue(toString.contains("42"), "toString应包含哈希值");
    }
    
    @Test
    @DisplayName("测试综合演示方法")
    void testRunAllDemonstrations() {
        // 这个测试主要验证综合演示方法能正常运行而不抛出异常
        assertDoesNotThrow(() -> {
            HashMapSourceDemo.runAllDemonstrations();
        }, "综合演示方法应该能正常运行");
    }
    
    @Test
    @DisplayName("测试边界条件")
    void testBoundaryConditions() {
        // 测试空HashMap的分析
        HashMapSourceDemo.HashDistributionResult emptyResult = 
                HashMapSourceDemo.analyzeHashDistribution(0);
        assertNotNull(emptyResult, "空HashMap分析结果不应为空");
        assertEquals(0, emptyResult.getTotalElements(), "空HashMap元素数应为0");
        
        // 测试单元素HashMap的分析
        HashMapSourceDemo.HashDistributionResult singleResult = 
                HashMapSourceDemo.analyzeHashDistribution(1);
        assertNotNull(singleResult, "单元素HashMap分析结果不应为空");
        assertEquals(1, singleResult.getTotalElements(), "单元素HashMap元素数应为1");
        assertEquals(1, singleResult.getMaxChainLength(), "单元素HashMap最大链长应为1");
        
        // 测试小规模扩容
        HashMapSourceDemo.ResizeAnalysisResult smallResize = 
                HashMapSourceDemo.demonstrateResizeMechanism(1);
        assertNotNull(smallResize, "小规模扩容结果不应为空");
        assertTrue(smallResize.getResizeCount() >= 0, "小规模扩容次数应 >= 0");
    }
    
    @Test
    @DisplayName("测试性能特性")
    void testPerformanceCharacteristics() {
        // 测试Put操作的时间记录
        List<HashMapSourceDemo.PutOperationResult> putResults = 
                HashMapSourceDemo.demonstratePutOperations();
        
        // 验证所有操作都有时间记录
        for (HashMapSourceDemo.PutOperationResult result : putResults) {
            assertTrue(result.getOperationTimeNanos() >= 0, 
                    "操作时间应 >= 0: " + result.getOperationTimeNanos());
        }
        
        // 测试扩容时间记录
        HashMapSourceDemo.ResizeAnalysisResult resizeResult = 
                HashMapSourceDemo.demonstrateResizeMechanism(20);
        assertTrue(resizeResult.getTotalResizeTimeMs() >= 0, 
                "总扩容时间应 >= 0: " + resizeResult.getTotalResizeTimeMs());
        
        if (resizeResult.getResizeCount() > 0) {
            Double avgResizeTime = (Double) resizeResult.getResizeDetails().get("averageResizeTime");
            assertTrue(avgResizeTime >= 0, "平均扩容时间应 >= 0: " + avgResizeTime);
        }
    }
    
    @Test
    @DisplayName("测试数据一致性")
    void testDataConsistency() {
        // 测试哈希扰动的数据一致性
        Map<String, Object> hashResults = HashMapSourceDemo.demonstrateHashPerturbation();
        
        @SuppressWarnings("unchecked")
        Map<String, Integer> bucketIndices = (Map<String, Integer>) hashResults.get("bucketIndices");
        @SuppressWarnings("unchecked")
        Map<Integer, Integer> bucketDistribution = (Map<Integer, Integer>) hashResults.get("bucketDistribution");
        
        // 验证桶分布的总数等于键的数量
        int totalInDistribution = bucketDistribution.values().stream().mapToInt(Integer::intValue).sum();
        assertEquals(bucketIndices.size(), totalInDistribution, 
                "桶分布中的总元素数应等于键的数量");
        
        // 验证分布中的桶索引都在bucketIndices中出现过
        Set<Integer> usedBuckets = new HashSet<>(bucketIndices.values());
        for (Integer bucketIndex : bucketDistribution.keySet()) {
            assertTrue(usedBuckets.contains(bucketIndex), 
                    "分布中的桶索引应在使用的桶中: " + bucketIndex);
        }
    }
}