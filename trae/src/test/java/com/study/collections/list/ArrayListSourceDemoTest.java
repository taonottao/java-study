package com.study.collections.list;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ArrayList 源码演示的单元测试
 * 验证各个演示功能的正确性和预期行为
 */
@Slf4j
@DisplayName("ArrayList 源码级演示测试")
class ArrayListSourceDemoTest {
    
    @BeforeEach
    void setUp() {
        log.info("开始 ArrayList 源码演示测试");
    }
    
    @Test
    @DisplayName("测试扩容机制分析")
    void testGrowthMechanism() {
        // 测试小规模扩容
        ArrayListSourceDemo.GrowthAnalysisResult result = 
            ArrayListSourceDemo.demonstrateGrowthMechanism(50);
        
        // 验证基本属性
        assertNotNull(result, "扩容分析结果不应为空");
        assertTrue(result.getInitialCapacity() > 0, "初始容量应大于0");
        assertTrue(result.getFinalCapacity() >= result.getInitialCapacity(), 
                  "最终容量应不小于初始容量");
        assertTrue(result.getGrowthCount() >= 0, "扩容次数应非负");
        assertNotNull(result.getCapacityHistory(), "容量历史不应为空");
        assertTrue(result.getTotalTimeMs() >= 0, "执行时间应非负");
        
        // 验证扩容历史的合理性
        List<Integer> history = result.getCapacityHistory();
        if (history.size() > 1) {
            for (int i = 1; i < history.size(); i++) {
                assertTrue(history.get(i) > history.get(i-1), 
                          "容量应该是递增的");
            }
        }
        
        log.info("扩容机制测试通过: 初始容量={}, 最终容量={}, 扩容次数={}", 
                result.getInitialCapacity(), result.getFinalCapacity(), result.getGrowthCount());
    }
    
    @Test
    @DisplayName("测试 ensureCapacity 性能优化")
    void testEnsureCapacityPerformance() {
        Map<String, Long> results = 
            ArrayListSourceDemo.demonstrateEnsureCapacityPerformance(10000);
        
        // 验证结果包含必要的键
        assertTrue(results.containsKey("withoutEnsureCapacity"), 
                  "应包含不使用ensureCapacity的结果");
        assertTrue(results.containsKey("withEnsureCapacity"), 
                  "应包含使用ensureCapacity的结果");
        assertTrue(results.containsKey("improvementPercent"), 
                  "应包含性能提升百分比");
        
        // 验证时间值的合理性
        Long timeWithout = results.get("withoutEnsureCapacity");
        Long timeWith = results.get("withEnsureCapacity");
        Long improvement = results.get("improvementPercent");
        
        assertTrue(timeWithout >= 0, "执行时间应非负");
        assertTrue(timeWith >= 0, "执行时间应非负");
        
        // 通常情况下，ensureCapacity 应该有性能提升（但不强制要求，因为在小数据量下可能差异不明显）
        log.info("性能对比测试通过: 不预分配={}ms, 预分配={}ms, 提升={}%", 
                timeWithout, timeWith, improvement);
    }
    
    @Test
    @DisplayName("测试 fail-fast 机制")
    void testFailFastBehavior() {
        ArrayListSourceDemo.FailFastResult result = 
            ArrayListSourceDemo.demonstrateFailFastBehavior();
        
        // 验证 fail-fast 异常被正确触发
        assertTrue(result.isExceptionThrown(), "应该触发 fail-fast 异常");
        assertEquals("ConcurrentModificationException", result.getExceptionType(), 
                    "异常类型应为 ConcurrentModificationException");
        assertNotNull(result.getExceptionMessage(), "异常消息不应为空");
        assertEquals("add during iteration", result.getOperationType(), 
                    "操作类型应为迭代中添加");
        
        log.info("fail-fast 机制测试通过: 异常类型={}, 操作类型={}", 
                result.getExceptionType(), result.getOperationType());
    }
    
    @Test
    @DisplayName("测试安全的迭代器删除")
    void testSafeIteratorRemoval() {
        List<String> result = ArrayListSourceDemo.demonstrateSafeIteratorRemoval();
        
        // 验证删除结果
        assertNotNull(result, "结果不应为空");
        assertEquals(3, result.size(), "应该剩余3个元素");
        
        // 验证保留的元素
        assertTrue(result.contains("keep1"), "应保留 keep1");
        assertTrue(result.contains("keep2"), "应保留 keep2");
        assertTrue(result.contains("keep3"), "应保留 keep3");
        
        // 验证删除的元素
        assertFalse(result.contains("remove1"), "应删除 remove1");
        assertFalse(result.contains("remove2"), "应删除 remove2");
        
        log.info("安全迭代器删除测试通过: 结果={}", result);
    }
    
    @Test
    @DisplayName("测试 subList 视图特性")
    void testSubListView() {
        ArrayListSourceDemo.SubListResult result = 
            ArrayListSourceDemo.demonstrateSubListView();
        
        // 验证基本属性
        assertNotNull(result.getOriginalList(), "原始列表不应为空");
        assertNotNull(result.getSubListView(), "子列表视图不应为空");
        assertNotNull(result.getOriginalAfterSubListModification(), 
                     "修改后的原始列表不应为空");
        
        // 验证视图特性
        assertTrue(result.isViewUpdated(), "子列表应该是视图，修改应影响原列表");
        assertEquals("subList.set(1, \"C_MODIFIED\")", result.getOperation(), 
                    "操作描述应正确");
        
        // 验证原始列表确实被修改
        List<String> originalAfter = result.getOriginalAfterSubListModification();
        assertTrue(originalAfter.contains("C_MODIFIED"), 
                  "原始列表应包含修改后的值");
        assertFalse(originalAfter.contains("C"), 
                   "原始列表不应包含原始值");
        
        log.info("subList 视图测试通过: 视图特性={}, 操作={}", 
                result.isViewUpdated(), result.getOperation());
    }
    
    @Test
    @DisplayName("测试内部数组操作")
    void testInternalArrayOperations() {
        Map<String, Object> results = 
            ArrayListSourceDemo.demonstrateInternalArrayOperations();
        
        // 验证结果包含必要的键
        assertTrue(results.containsKey("arrayLength"), "应包含数组长度");
        assertTrue(results.containsKey("listSize"), "应包含列表大小");
        assertTrue(results.containsKey("unusedSlots"), "应包含未使用槽位数");
        assertTrue(results.containsKey("arrayContent"), "应包含数组内容");
        
        // 验证数值的合理性
        Integer arrayLength = (Integer) results.get("arrayLength");
        Integer listSize = (Integer) results.get("listSize");
        Integer unusedSlots = (Integer) results.get("unusedSlots");
        
        assertTrue(arrayLength > 0, "数组长度应大于0");
        assertTrue(listSize > 0, "列表大小应大于0");
        assertTrue(unusedSlots >= 0, "未使用槽位应非负");
        assertEquals(arrayLength - listSize, unusedSlots.intValue(), 
                    "未使用槽位计算应正确");
        
        // 验证数组内容
        @SuppressWarnings("unchecked")
        List<String> arrayContent = (List<String>) results.get("arrayContent");
        assertEquals(arrayLength.intValue(), arrayContent.size(), 
                    "数组内容长度应等于数组长度");
        
        log.info("内部数组操作测试通过: 数组长度={}, 列表大小={}, 未使用槽位={}", 
                arrayLength, listSize, unusedSlots);
    }
    
    @Test
    @DisplayName("测试性能特性")
    void testPerformanceCharacteristics() {
        Map<String, Long> results = 
            ArrayListSourceDemo.demonstratePerformanceCharacteristics(1000);
        
        // 验证结果包含所有性能指标
        assertTrue(results.containsKey("randomAccessNanos"), "应包含随机访问时间");
        assertTrue(results.containsKey("tailInsertNanos"), "应包含尾部插入时间");
        assertTrue(results.containsKey("headInsertNanos"), "应包含头部插入时间");
        assertTrue(results.containsKey("middleRemoveNanos"), "应包含中间删除时间");
        
        // 验证所有时间都是非负的
        for (Map.Entry<String, Long> entry : results.entrySet()) {
            assertTrue(entry.getValue() >= 0, 
                      entry.getKey() + " 的时间应该非负");
        }
        
        // 验证性能特性的相对关系（通常情况下）
        Long randomAccess = results.get("randomAccessNanos");
        Long tailInsert = results.get("tailInsertNanos");
        Long headInsert = results.get("headInsertNanos");
        Long middleRemove = results.get("middleRemoveNanos");
        
        // ArrayList 的特性：随机访问快，头部插入慢
        // 注意：这些断言可能在某些情况下失败，因为性能测试受多种因素影响
        log.info("性能特性测试完成: 随机访问={}ns, 尾部插入={}ns, 头部插入={}ns, 中间删除={}ns", 
                randomAccess, tailInsert, headInsert, middleRemove);
        
        // 只验证基本的合理性，不做严格的性能比较
        assertTrue(randomAccess > 0, "随机访问应有耗时");
        assertTrue(tailInsert > 0, "尾部插入应有耗时");
        assertTrue(headInsert > 0, "头部插入应有耗时");
        assertTrue(middleRemove > 0, "中间删除应有耗时");
    }
    
    @Test
    @DisplayName("测试综合演示")
    void testRunAllDemonstrations() {
        // 这个测试主要验证综合演示方法不会抛出异常
        assertDoesNotThrow(() -> {
            ArrayListSourceDemo.runAllDemonstrations();
        }, "综合演示不应抛出异常");
        
        log.info("综合演示测试通过");
    }
    
    @Test
    @DisplayName("测试边界条件")
    void testEdgeCases() {
        // 测试空列表的扩容
        ArrayListSourceDemo.GrowthAnalysisResult emptyResult = 
            ArrayListSourceDemo.demonstrateGrowthMechanism(0);
        assertNotNull(emptyResult, "空列表扩容结果不应为空");
        
        // 测试单元素的扩容
        ArrayListSourceDemo.GrowthAnalysisResult singleResult = 
            ArrayListSourceDemo.demonstrateGrowthMechanism(1);
        assertNotNull(singleResult, "单元素扩容结果不应为空");
        assertTrue(singleResult.getInitialCapacity() > 0, "单元素时初始容量应大于0");
        
        // 测试小规模性能测试
        Map<String, Long> smallPerf = 
            ArrayListSourceDemo.demonstratePerformanceCharacteristics(10);
        assertNotNull(smallPerf, "小规模性能测试结果不应为空");
        
        log.info("边界条件测试通过");
    }
}