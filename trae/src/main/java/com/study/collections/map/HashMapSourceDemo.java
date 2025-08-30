package com.study.collections.map;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentModificationException;
import java.lang.reflect.Field;

/**
 * HashMap 源码级演示
 * 深入理解 HashMap 的核心机制：
 * 1. 哈希扰动函数（hash perturbation）
 * 2. put 操作流程（链表 -> 红黑树转换）
 * 3. resize 扩容机制
 * 4. 树化与退化（treeify/untreeify）
 * 5. 负载因子与性能影响
 * 6. fail-fast 行为
 */
@Slf4j
public class HashMapSourceDemo {
    
    /**
     * 哈希分布分析结果
     */
    @Data
    public static class HashDistributionResult {
        private int bucketCount;
        private int totalElements;
        private Map<Integer, Integer> bucketSizes; // 桶索引 -> 元素数量
        private double loadFactor;
        private int maxChainLength;
        private double averageChainLength;
        private int emptyBuckets;
        private Map<String, Object> hashAnalysis; // 哈希值分析
    }
    
    /**
     * 扩容分析结果
     */
    @Data
    public static class ResizeAnalysisResult {
        private List<Integer> capacityHistory;
        private List<Integer> sizeHistory;
        private List<Double> loadFactorHistory;
        private int resizeCount;
        private long totalResizeTimeMs;
        private Map<String, Object> resizeDetails;
    }
    
    /**
     * 树化分析结果
     */
    @Data
    public static class TreeifyAnalysisResult {
        private boolean treeificationOccurred;
        private int chainLengthBeforeTreeify;
        private int bucketIndex;
        private int mapSizeAtTreeify;
        private String triggerKey;
        private Map<String, Object> treeStructureInfo;
    }
    
    /**
     * put 操作分析结果
     */
    @Data
    public static class PutOperationResult {
        private String operation; // "new_entry", "update_existing", "collision_resolved"
        private int bucketIndex;
        private int hashValue;
        private boolean causedResize;
        private boolean causedTreeify;
        private int chainLengthAfter;
        private long operationTimeNanos;
    }
    
    /**
     * 演示 HashMap 的哈希扰动函数
     * 分析 hash() 方法如何减少哈希冲突
     */
    public static Map<String, Object> demonstrateHashPerturbation() {
        log.info("开始演示 HashMap 哈希扰动函数");
        
        Map<String, Object> results = new HashMap<>();
        
        // 测试不同类型的键的哈希值分布
        String[] testKeys = {
            "key1", "key2", "key3", "key4", "key5",
            "user_001", "user_002", "user_003", "user_004", "user_005",
            "data_a", "data_b", "data_c", "data_d", "data_e"
        };
        
        Map<String, Integer> originalHashes = new HashMap<>();
        Map<String, Integer> perturbedHashes = new HashMap<>();
        Map<String, Integer> bucketIndices = new HashMap<>();
        
        int capacity = 16; // 默认初始容量
        
        for (String key : testKeys) {
            // 原始 hashCode
            int originalHash = key.hashCode();
            originalHashes.put(key, originalHash);
            
            // HashMap 的扰动函数：hash(key)
            int perturbedHash = hash(key);
            perturbedHashes.put(key, perturbedHash);
            
            // 计算桶索引：(n-1) & hash
            int bucketIndex = (capacity - 1) & perturbedHash;
            bucketIndices.put(key, bucketIndex);
            
            log.debug("Key: {}, Original: {}, Perturbed: {}, Bucket: {}", 
                    key, originalHash, perturbedHash, bucketIndex);
        }
        
        results.put("originalHashes", originalHashes);
        results.put("perturbedHashes", perturbedHashes);
        results.put("bucketIndices", bucketIndices);
        
        // 分析哈希分布的均匀性
        Map<Integer, Integer> bucketDistribution = new HashMap<>();
        for (Integer bucketIndex : bucketIndices.values()) {
            bucketDistribution.merge(bucketIndex, 1, Integer::sum);
        }
        results.put("bucketDistribution", bucketDistribution);
        
        // 计算分布统计
        int maxCollisions = bucketDistribution.values().stream().mapToInt(Integer::intValue).max().orElse(0);
        double averageLoad = (double) testKeys.length / capacity;
        
        results.put("maxCollisions", maxCollisions);
        results.put("averageLoad", averageLoad);
        results.put("totalBuckets", capacity);
        results.put("usedBuckets", bucketDistribution.size());
        
        log.info("哈希扰动分析完成: 最大冲突={}, 平均负载={:.2f}, 使用桶数={}/{}", 
                maxCollisions, averageLoad, bucketDistribution.size(), capacity);
        
        return results;
    }
    
    /**
     * 模拟 HashMap 的 hash() 方法
     */
    private static int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
    
    /**
     * 演示 HashMap 的 put 操作流程
     * 包括链表插入、冲突解决、树化等
     */
    public static List<PutOperationResult> demonstratePutOperations() {
        log.info("开始演示 HashMap put 操作流程");
        
        List<PutOperationResult> results = new ArrayList<>();
        HashMap<String, String> map = new HashMap<>(4); // 小容量便于观察扩容
        
        // 测试数据：故意制造哈希冲突
        String[] keys = {
            "key1", "key2", "key3", "key4", "key5", "key6", "key7", "key8",
            "collision1", "collision2", "collision3", "collision4"
        };
        
        for (String key : keys) {
            long startTime = System.nanoTime();
            
            PutOperationResult result = new PutOperationResult();
            result.setHashValue(hash(key));
            
            // 记录操作前的状态
            int sizeBefore = map.size();
            boolean existedBefore = map.containsKey(key);
            
            // 执行 put 操作
            String oldValue = map.put(key, "value_" + key);
            
            // 分析操作结果
            if (oldValue == null && !existedBefore) {
                result.setOperation("new_entry");
            } else if (oldValue != null) {
                result.setOperation("update_existing");
            } else {
                result.setOperation("collision_resolved");
            }
            
            // 检查是否触发了扩容
            result.setCausedResize(map.size() > sizeBefore && map.size() > getCapacity(map) * 0.75);
            
            result.setOperationTimeNanos(System.nanoTime() - startTime);
            
            // 尝试获取桶索引（通过反射）
            try {
                int capacity = getCapacity(map);
                result.setBucketIndex((capacity - 1) & result.getHashValue());
            } catch (Exception e) {
                log.warn("无法获取桶索引", e);
            }
            
            results.add(result);
            
            log.debug("Put操作: key={}, operation={}, bucket={}, resize={}", 
                    key, result.getOperation(), result.getBucketIndex(), result.isCausedResize());
        }
        
        log.info("Put操作演示完成，共执行 {} 次操作", results.size());
        return results;
    }
    
    /**
     * 通过反射获取 HashMap 的容量
     */
    private static int getCapacity(HashMap<?, ?> map) {
        try {
            Field tableField = HashMap.class.getDeclaredField("table");
            tableField.setAccessible(true);
            Object[] table = (Object[]) tableField.get(map);
            return table == null ? 0 : table.length;
        } catch (Exception e) {
            return 16; // 默认值
        }
    }
    
    /**
     * 演示 HashMap 的扩容机制
     */
    public static ResizeAnalysisResult demonstrateResizeMechanism(int targetSize) {
        log.info("开始演示 HashMap 扩容机制，目标大小: {}", targetSize);
        
        ResizeAnalysisResult result = new ResizeAnalysisResult();
        result.setCapacityHistory(new ArrayList<>());
        result.setSizeHistory(new ArrayList<>());
        result.setLoadFactorHistory(new ArrayList<>());
        result.setResizeDetails(new HashMap<>());
        
        HashMap<String, String> map = new HashMap<>(4); // 从小容量开始
        
        long totalResizeTime = 0;
        int resizeCount = 0;
        int previousCapacity = getCapacity(map);
        
        for (int i = 0; i < targetSize; i++) {
            long startTime = System.nanoTime();
            
            String key = "key_" + i;
            map.put(key, "value_" + i);
            
            long operationTime = System.nanoTime() - startTime;
            
            // 检查是否发生了扩容
            int currentCapacity = getCapacity(map);
            if (currentCapacity != previousCapacity) {
                resizeCount++;
                totalResizeTime += operationTime;
                
                result.getCapacityHistory().add(currentCapacity);
                result.getSizeHistory().add(map.size());
                result.getLoadFactorHistory().add((double) map.size() / currentCapacity);
                
                log.debug("第{}次扩容: {} -> {}, 当前大小: {}, 负载因子: {:.2f}", 
                        resizeCount, previousCapacity, currentCapacity, 
                        map.size(), (double) map.size() / currentCapacity);
                
                previousCapacity = currentCapacity;
            }
        }
        
        result.setResizeCount(resizeCount);
        result.setTotalResizeTimeMs(totalResizeTime / 1_000_000);
        
        // 添加详细信息
        result.getResizeDetails().put("finalCapacity", getCapacity(map));
        result.getResizeDetails().put("finalSize", map.size());
        result.getResizeDetails().put("finalLoadFactor", (double) map.size() / getCapacity(map));
        result.getResizeDetails().put("averageResizeTime", 
                resizeCount > 0 ? totalResizeTime / resizeCount / 1_000_000.0 : 0);
        
        log.info("扩容分析完成: 扩容次数={}, 最终容量={}, 总扩容时间={}ms", 
                resizeCount, getCapacity(map), result.getTotalResizeTimeMs());
        
        return result;
    }
    
    /**
     * 演示链表到红黑树的转换（树化）
     * 需要构造特殊的哈希冲突来触发树化
     */
    public static TreeifyAnalysisResult demonstrateTreeification() {
        log.info("开始演示 HashMap 树化机制");
        
        TreeifyAnalysisResult result = new TreeifyAnalysisResult();
        result.setTreeStructureInfo(new HashMap<>());
        
        // 创建一个容量较小的 HashMap
        HashMap<CollidingKey, String> map = new HashMap<>(64); // 确保容量 >= 64 才能树化
        
        // 创建会产生相同哈希值的键（故意制造冲突）
        List<CollidingKey> collidingKeys = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            collidingKeys.add(new CollidingKey("key_" + i, 42)); // 相同哈希值
        }
        
        int chainLength = 0;
        String triggerKey = null;
        
        // 逐个添加冲突键，观察链表长度
        for (CollidingKey key : collidingKeys) {
            map.put(key, "value_" + key.getName());
            chainLength++;
            
            // 检查是否触发了树化（链表长度 >= 8 且容量 >= 64）
            if (chainLength >= 8 && !result.isTreeificationOccurred()) {
                result.setTreeificationOccurred(true);
                result.setChainLengthBeforeTreeify(chainLength);
                result.setMapSizeAtTreeify(map.size());
                result.setTriggerKey(key.getName());
                
                // 计算桶索引
                int capacity = getCapacity(map);
                int bucketIndex = (capacity - 1) & hash(key);
                result.setBucketIndex(bucketIndex);
                
                log.info("树化触发: 链表长度={}, 触发键={}, 桶索引={}, 映射大小={}", 
                        chainLength, key.getName(), bucketIndex, map.size());
                break;
            }
        }
        
        // 分析树结构信息（通过反射）
        try {
            analyzeTreeStructure(map, result);
        } catch (Exception e) {
            log.warn("无法分析树结构", e);
        }
        
        if (!result.isTreeificationOccurred()) {
            log.info("未触发树化，最大链表长度: {}", chainLength);
        }
        
        return result;
    }
    
    /**
     * 分析树结构（通过反射）
     */
    private static void analyzeTreeStructure(HashMap<?, ?> map, TreeifyAnalysisResult result) {
        try {
            Field tableField = HashMap.class.getDeclaredField("table");
            tableField.setAccessible(true);
            Object[] table = (Object[]) tableField.get(map);
            
            if (table != null) {
                int treeNodes = 0;
                int listNodes = 0;
                
                for (Object node : table) {
                    if (node != null) {
                        String nodeType = node.getClass().getSimpleName();
                        if (nodeType.contains("TreeNode")) {
                            treeNodes++;
                        } else {
                            listNodes++;
                        }
                    }
                }
                
                result.getTreeStructureInfo().put("treeNodes", treeNodes);
                result.getTreeStructureInfo().put("listNodes", listNodes);
                result.getTreeStructureInfo().put("totalBuckets", table.length);
                result.getTreeStructureInfo().put("emptyBuckets", table.length - treeNodes - listNodes);
            }
        } catch (Exception e) {
            log.warn("分析树结构失败", e);
        }
    }
    
    /**
     * 演示 HashMap 的哈希分布分析
     */
    public static HashDistributionResult analyzeHashDistribution(int elementCount) {
        log.info("开始分析 HashMap 哈希分布，元素数量: {}", elementCount);
        
        HashDistributionResult result = new HashDistributionResult();
        HashMap<String, String> map = new HashMap<>();
        
        // 添加元素
        for (int i = 0; i < elementCount; i++) {
            map.put("key_" + i, "value_" + i);
        }
        
        try {
            // 通过反射分析内部结构
            Field tableField = HashMap.class.getDeclaredField("table");
            tableField.setAccessible(true);
            Object[] table = (Object[]) tableField.get(map);
            
            if (table != null) {
                result.setBucketCount(table.length);
                result.setTotalElements(map.size());
                result.setLoadFactor((double) map.size() / table.length);
                
                Map<Integer, Integer> bucketSizes = new HashMap<>();
                int maxChainLength = 0;
                int totalChainLength = 0;
                int emptyBuckets = 0;
                
                for (int i = 0; i < table.length; i++) {
                    Object node = table[i];
                    int chainLength = 0;
                    
                    if (node == null) {
                        emptyBuckets++;
                    } else {
                        // 计算链表/树的长度
                        chainLength = countChainLength(node);
                        bucketSizes.put(i, chainLength);
                        maxChainLength = Math.max(maxChainLength, chainLength);
                        totalChainLength += chainLength;
                    }
                }
                
                result.setBucketSizes(bucketSizes);
                result.setMaxChainLength(maxChainLength);
                result.setAverageChainLength((double) totalChainLength / (table.length - emptyBuckets));
                result.setEmptyBuckets(emptyBuckets);
                
                // 哈希分析
                Map<String, Object> hashAnalysis = new HashMap<>();
                hashAnalysis.put("uniformityScore", calculateUniformityScore(bucketSizes, table.length));
                hashAnalysis.put("collisionRate", (double) (map.size() - bucketSizes.size()) / map.size());
                result.setHashAnalysis(hashAnalysis);
            }
        } catch (Exception e) {
            log.error("分析哈希分布失败", e);
        }
        
        log.info("哈希分布分析完成: 桶数={}, 最大链长={}, 平均链长={:.2f}, 空桶数={}", 
                result.getBucketCount(), result.getMaxChainLength(), 
                result.getAverageChainLength(), result.getEmptyBuckets());
        
        return result;
    }
    
    /**
     * 计算链表/树的长度
     */
    private static int countChainLength(Object node) {
        try {
            int count = 1;
            Field nextField = node.getClass().getDeclaredField("next");
            nextField.setAccessible(true);
            Object next = nextField.get(node);
            
            while (next != null) {
                count++;
                next = nextField.get(next);
            }
            
            return count;
        } catch (Exception e) {
            return 1; // 无法计算时返回1
        }
    }
    
    /**
     * 计算分布均匀性得分
     */
    private static double calculateUniformityScore(Map<Integer, Integer> bucketSizes, int totalBuckets) {
        if (bucketSizes.isEmpty()) return 0.0;
        
        double expectedSize = (double) bucketSizes.values().stream().mapToInt(Integer::intValue).sum() / totalBuckets;
        double variance = bucketSizes.values().stream()
                .mapToDouble(size -> Math.pow(size - expectedSize, 2))
                .average().orElse(0.0);
        
        // 均匀性得分：方差越小，得分越高
        return 1.0 / (1.0 + variance);
    }
    
    /**
     * 演示 HashMap 的 fail-fast 行为
     */
    public static boolean demonstrateFailFast() {
        log.info("演示 HashMap fail-fast 机制");
        
        HashMap<String, String> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key3", "value3");
        
        try {
            // 获取迭代器
            Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
            
            // 开始迭代
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                log.debug("迭代到: {} = {}", entry.getKey(), entry.getValue());
                
                // 在迭代过程中修改 map
                if ("key2".equals(entry.getKey())) {
                    map.put("newKey", "newValue"); // 这会触发 fail-fast
                }
            }
            return false; // 如果没有异常，返回 false
        } catch (ConcurrentModificationException e) {
            log.info("成功触发 fail-fast: {}", e.getMessage());
            return true;
        }
    }
    
    /**
     * 综合演示方法
     */
    public static void runAllDemonstrations() {
        log.info("=== HashMap 源码级演示开始 ===");
        
        // 1. 哈希扰动函数
        Map<String, Object> hashResults = demonstrateHashPerturbation();
        
        // 2. put 操作流程
        List<PutOperationResult> putResults = demonstratePutOperations();
        
        // 3. 扩容机制
        ResizeAnalysisResult resizeResult = demonstrateResizeMechanism(100);
        
        // 4. 树化机制
        TreeifyAnalysisResult treeifyResult = demonstrateTreeification();
        
        // 5. 哈希分布分析
        HashDistributionResult distributionResult = analyzeHashDistribution(1000);
        
        // 6. fail-fast 机制
        boolean failFastTriggered = demonstrateFailFast();
        
        log.info("=== HashMap 源码级演示完成 ===");
        
        // 输出总结
        log.info("\n=== 演示结果总结 ===");
        log.info("哈希扰动: 最大冲突={}, 使用桶数={}", 
                hashResults.get("maxCollisions"), hashResults.get("usedBuckets"));
        log.info("Put操作: 共{}次操作", putResults.size());
        log.info("扩容机制: {}次扩容, 最终容量={}", 
                resizeResult.getResizeCount(), resizeResult.getResizeDetails().get("finalCapacity"));
        log.info("树化机制: {}", treeifyResult.isTreeificationOccurred() ? "已触发" : "未触发");
        log.info("哈希分布: 最大链长={}, 平均链长={:.2f}", 
                distributionResult.getMaxChainLength(), distributionResult.getAverageChainLength());
        log.info("fail-fast: {}", failFastTriggered ? "已触发" : "未触发");
    }
    
    /**
     * 用于制造哈希冲突的测试键
     */
    public static class CollidingKey {
        private final String name;
        private final int fixedHash;
        
        public CollidingKey(String name, int fixedHash) {
            this.name = name;
            this.fixedHash = fixedHash;
        }
        
        public String getName() {
            return name;
        }
        
        @Override
        public int hashCode() {
            return fixedHash; // 固定哈希值，制造冲突
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            CollidingKey that = (CollidingKey) obj;
            return Objects.equals(name, that.name);
        }
        
        @Override
        public String toString() {
            return "CollidingKey{name='" + name + "', hash=" + fixedHash + "}";
        }
    }
}