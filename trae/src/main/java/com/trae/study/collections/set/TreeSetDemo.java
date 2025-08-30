package com.trae.study.collections.set;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * TreeSet 深度演示
 * 
 * TreeSet 是基于红黑树（Red-Black Tree）实现的有序集合，提供了 O(log n) 的插入、删除和查找性能。
 * 本演示涵盖：
 * 1. 基于红黑树的有序性特性
 * 2. 自定义 Comparator 的使用
 * 3. 范围查询功能（subSet、headSet、tailSet）
 * 4. 与其他 Set 实现的性能对比
 * 5. 内部结构分析
 * 6. 实际应用场景演示
 * 
 * @author Trae
 * @since 2024-01-20
 */
public class TreeSetDemo {

    /**
     * 演示 TreeSet 的基本有序特性
     */
    public void demonstrateBasicOrdering() {
        System.out.println("\n=== TreeSet 基本有序特性演示 ===");
        
        // 创建 TreeSet 并添加无序数据
        TreeSet<Integer> treeSet = new TreeSet<>();
        int[] randomNumbers = {5, 2, 8, 1, 9, 3, 7, 4, 6};
        
        System.out.println("插入顺序: " + Arrays.toString(randomNumbers));
        
        for (int num : randomNumbers) {
            treeSet.add(num);
        }
        
        System.out.println("TreeSet 自动排序结果: " + treeSet);
        
        // 演示重复元素处理
        treeSet.add(5); // 重复元素
        treeSet.add(3); // 重复元素
        System.out.println("添加重复元素后: " + treeSet + " (大小: " + treeSet.size() + ")");
        
        // 演示迭代顺序
        System.out.print("迭代顺序: ");
        for (Integer num : treeSet) {
            System.out.print(num + " ");
        }
        System.out.println();
    }

    /**
     * 演示自定义 Comparator 的使用
     */
    public void demonstrateCustomComparator() {
        System.out.println("\n=== 自定义 Comparator 演示 ===");
        
        // 1. 逆序排列
        TreeSet<Integer> reverseSet = new TreeSet<>(Collections.reverseOrder());
        reverseSet.addAll(Arrays.asList(5, 2, 8, 1, 9, 3, 7, 4, 6));
        System.out.println("逆序排列: " + reverseSet);
        
        // 2. 字符串长度排序
        TreeSet<String> lengthSortedSet = new TreeSet<>(Comparator.comparing(String::length)
                .thenComparing(String::compareTo));
        lengthSortedSet.addAll(Arrays.asList("Java", "C", "Python", "Go", "JavaScript", "Rust"));
        System.out.println("按长度排序: " + lengthSortedSet);
        
        // 3. 复杂对象排序
        TreeSet<Student> studentSet = new TreeSet<>(Comparator
                .comparing(Student::getGrade).reversed()
                .thenComparing(Student::getName));
        
        studentSet.add(new Student("Alice", 85));
        studentSet.add(new Student("Bob", 92));
        studentSet.add(new Student("Charlie", 78));
        studentSet.add(new Student("David", 92)); // 同分数
        studentSet.add(new Student("Eve", 88));
        
        System.out.println("学生按成绩排序 (成绩降序，姓名升序):");
        studentSet.forEach(System.out::println);
        
        // 4. 自定义排序规则：奇数在前，偶数在后，各自升序
        TreeSet<Integer> customSet = new TreeSet<>((a, b) -> {
            boolean aIsOdd = a % 2 == 1;
            boolean bIsOdd = b % 2 == 1;
            
            if (aIsOdd && !bIsOdd) return -1; // a是奇数，b是偶数，a在前
            if (!aIsOdd && bIsOdd) return 1;  // a是偶数，b是奇数，b在前
            return Integer.compare(a, b);     // 同奇偶性，按数值排序
        });
        
        customSet.addAll(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
        System.out.println("自定义排序 (奇数在前): " + customSet);
    }

    /**
     * 演示 TreeSet 的范围查询功能
     */
    public void demonstrateRangeQueries() {
        System.out.println("\n=== TreeSet 范围查询演示 ===");
        
        TreeSet<Integer> treeSet = new TreeSet<>();
        // 添加 1-20 的数字
        IntStream.rangeClosed(1, 20).forEach(treeSet::add);
        
        System.out.println("原始集合: " + treeSet);
        
        // 1. subSet - 获取指定范围的子集
        SortedSet<Integer> subSet = treeSet.subSet(5, 15); // [5, 15)
        System.out.println("subSet(5, 15): " + subSet);
        
        SortedSet<Integer> subSetInclusive = treeSet.subSet(5, true, 15, true); // [5, 15]
        System.out.println("subSet(5, true, 15, true): " + subSetInclusive);
        
        // 2. headSet - 获取小于指定值的元素
        SortedSet<Integer> headSet = treeSet.headSet(10); // < 10
        System.out.println("headSet(10): " + headSet);
        
        SortedSet<Integer> headSetInclusive = treeSet.headSet(10, true); // <= 10
        System.out.println("headSet(10, true): " + headSetInclusive);
        
        // 3. tailSet - 获取大于等于指定值的元素
        SortedSet<Integer> tailSet = treeSet.tailSet(15); // >= 15
        System.out.println("tailSet(15): " + tailSet);
        
        SortedSet<Integer> tailSetExclusive = treeSet.tailSet(15, false); // > 15
        System.out.println("tailSet(15, false): " + tailSetExclusive);
        
        // 4. 边界查询
        System.out.println("\n--- 边界查询 ---");
        System.out.println("first(): " + treeSet.first()); // 最小元素
        System.out.println("last(): " + treeSet.last());   // 最大元素
        
        System.out.println("lower(10): " + treeSet.lower(10));   // < 10 的最大元素
        System.out.println("floor(10): " + treeSet.floor(10));   // <= 10 的最大元素
        System.out.println("ceiling(10): " + treeSet.ceiling(10)); // >= 10 的最小元素
        System.out.println("higher(10): " + treeSet.higher(10)); // > 10 的最小元素
        
        // 5. 范围查询的实际应用
        demonstrateRangeQueryPerformance();
    }

    /**
     * 演示范围查询的性能特性
     */
    private void demonstrateRangeQueryPerformance() {
        System.out.println("\n--- 范围查询性能测试 ---");
        
        TreeSet<Integer> largeTreeSet = new TreeSet<>();
        HashSet<Integer> largeHashSet = new HashSet<>();
        
        // 填充大量数据
        int size = 100000;
        for (int i = 0; i < size; i++) {
            int value = ThreadLocalRandom.current().nextInt(size * 2);
            largeTreeSet.add(value);
            largeHashSet.add(value);
        }
        
        int rangeStart = size / 4;
        int rangeEnd = size * 3 / 4;
        
        // TreeSet 范围查询
        long startTime = System.nanoTime();
        SortedSet<Integer> rangeResult = largeTreeSet.subSet(rangeStart, rangeEnd);
        int treeSetRangeSize = rangeResult.size();
        long treeSetTime = System.nanoTime() - startTime;
        
        // HashSet 模拟范围查询（需要遍历所有元素）
        startTime = System.nanoTime();
        Set<Integer> hashSetRange = largeHashSet.stream()
                .filter(x -> x >= rangeStart && x < rangeEnd)
                .collect(Collectors.toSet());
        int hashSetRangeSize = hashSetRange.size();
        long hashSetTime = System.nanoTime() - startTime;
        
        System.out.printf("数据规模: %d, 查询范围: [%d, %d)%n", largeTreeSet.size(), rangeStart, rangeEnd);
        System.out.printf("TreeSet 范围查询: %d 个元素, 耗时: %.2f ms%n", 
                treeSetRangeSize, treeSetTime / 1_000_000.0);
        System.out.printf("HashSet 模拟范围查询: %d 个元素, 耗时: %.2f ms%n", 
                hashSetRangeSize, hashSetTime / 1_000_000.0);
        System.out.printf("TreeSet 性能优势: %.2fx%n", (double) hashSetTime / treeSetTime);
    }

    /**
     * 分析 TreeSet 的内部结构
     */
    public void analyzeInternalStructure() {
        System.out.println("\n=== TreeSet 内部结构分析 ===");
        
        TreeSet<Integer> treeSet = new TreeSet<>();
        treeSet.addAll(Arrays.asList(5, 2, 8, 1, 9, 3, 7, 4, 6));
        
        try {
            // 通过反射查看内部 TreeMap
            Field mapField = TreeSet.class.getDeclaredField("m");
            mapField.setAccessible(true);
            TreeMap<Integer, Object> internalMap = (TreeMap<Integer, Object>) mapField.get(treeSet);
            
            System.out.println("TreeSet 内部使用 TreeMap: " + (internalMap != null));
            System.out.println("内部 TreeMap 大小: " + internalMap.size());
            
            // 查看 PRESENT 对象
            Field presentField = TreeSet.class.getDeclaredField("PRESENT");
            presentField.setAccessible(true);
            Object present = presentField.get(null);
            
            System.out.println("PRESENT 对象: " + present);
            System.out.println("所有值都是 PRESENT: " + 
                    internalMap.values().stream().allMatch(v -> v == present));
            
            // 分析比较器
            Comparator<? super Integer> comparator = internalMap.comparator();
            System.out.println("使用的比较器: " + 
                    (comparator == null ? "自然排序 (Comparable)" : comparator.getClass().getSimpleName()));
            
            // 演示红黑树的平衡性
            demonstrateRedBlackTreeBalance();
            
        } catch (Exception e) {
            System.err.println("反射分析失败: " + e.getMessage());
        }
    }

    /**
     * 演示红黑树的平衡性特征
     */
    private void demonstrateRedBlackTreeBalance() {
        System.out.println("\n--- 红黑树平衡性演示 ---");
        
        // 创建不同大小的 TreeSet，观察性能特征
        int[] sizes = {100, 1000, 10000, 100000};
        
        for (int size : sizes) {
            TreeSet<Integer> treeSet = new TreeSet<>();
            
            // 顺序插入（最坏情况对于普通二叉搜索树）
            long startTime = System.nanoTime();
            for (int i = 0; i < size; i++) {
                treeSet.add(i);
            }
            long insertTime = System.nanoTime() - startTime;
            
            // 随机查找
            startTime = System.nanoTime();
            for (int i = 0; i < 1000; i++) {
                treeSet.contains(ThreadLocalRandom.current().nextInt(size));
            }
            long searchTime = System.nanoTime() - startTime;
            
            System.out.printf("大小: %6d, 插入耗时: %8.2f ms, 1000次查找: %6.2f ms%n", 
                    size, insertTime / 1_000_000.0, searchTime / 1_000_000.0);
        }
        
        System.out.println("注意：即使是顺序插入，TreeSet 的性能依然保持 O(log n)，这证明了红黑树的自平衡特性。");
    }

    /**
     * 与其他 Set 实现的性能对比
     */
    public void compareWithOtherSets() {
        System.out.println("\n=== TreeSet 与其他 Set 实现性能对比 ===");
        
        int size = 50000;
        List<Integer> testData = IntStream.range(0, size)
                .boxed()
                .collect(Collectors.toList());
        Collections.shuffle(testData);
        
        // 1. 插入性能对比
        System.out.println("\n--- 插入性能对比 ---");
        
        // HashSet 插入
        long startTime = System.nanoTime();
        HashSet<Integer> hashSet = new HashSet<>();
        testData.forEach(hashSet::add);
        long hashSetInsertTime = System.nanoTime() - startTime;
        
        // LinkedHashSet 插入
        startTime = System.nanoTime();
        LinkedHashSet<Integer> linkedHashSet = new LinkedHashSet<>();
        testData.forEach(linkedHashSet::add);
        long linkedHashSetInsertTime = System.nanoTime() - startTime;
        
        // TreeSet 插入
        startTime = System.nanoTime();
        TreeSet<Integer> treeSet = new TreeSet<>();
        testData.forEach(treeSet::add);
        long treeSetInsertTime = System.nanoTime() - startTime;
        
        System.out.printf("HashSet 插入 %d 个元素: %.2f ms%n", size, hashSetInsertTime / 1_000_000.0);
        System.out.printf("LinkedHashSet 插入 %d 个元素: %.2f ms%n", size, linkedHashSetInsertTime / 1_000_000.0);
        System.out.printf("TreeSet 插入 %d 个元素: %.2f ms%n", size, treeSetInsertTime / 1_000_000.0);
        
        // 2. 查找性能对比
        System.out.println("\n--- 查找性能对比 ---");
        
        List<Integer> searchData = IntStream.range(0, 1000)
                .map(i -> ThreadLocalRandom.current().nextInt(size))
                .boxed()
                .collect(Collectors.toList());
        
        // HashSet 查找
        startTime = System.nanoTime();
        int hashSetFound = 0;
        for (Integer value : searchData) {
            if (hashSet.contains(value)) hashSetFound++;
        }
        long hashSetSearchTime = System.nanoTime() - startTime;
        
        // LinkedHashSet 查找
        startTime = System.nanoTime();
        int linkedHashSetFound = 0;
        for (Integer value : searchData) {
            if (linkedHashSet.contains(value)) linkedHashSetFound++;
        }
        long linkedHashSetSearchTime = System.nanoTime() - startTime;
        
        // TreeSet 查找
        startTime = System.nanoTime();
        int treeSetFound = 0;
        for (Integer value : searchData) {
            if (treeSet.contains(value)) treeSetFound++;
        }
        long treeSetSearchTime = System.nanoTime() - startTime;
        
        System.out.printf("HashSet 查找 1000 次: %.2f ms (找到 %d 个)%n", 
                hashSetSearchTime / 1_000_000.0, hashSetFound);
        System.out.printf("LinkedHashSet 查找 1000 次: %.2f ms (找到 %d 个)%n", 
                linkedHashSetSearchTime / 1_000_000.0, linkedHashSetFound);
        System.out.printf("TreeSet 查找 1000 次: %.2f ms (找到 %d 个)%n", 
                treeSetSearchTime / 1_000_000.0, treeSetFound);
        
        // 3. 迭代性能对比
        System.out.println("\n--- 迭代性能对比 ---");
        
        // HashSet 迭代
        startTime = System.nanoTime();
        int hashSetSum = hashSet.stream().mapToInt(Integer::intValue).sum();
        long hashSetIterateTime = System.nanoTime() - startTime;
        
        // LinkedHashSet 迭代
        startTime = System.nanoTime();
        int linkedHashSetSum = linkedHashSet.stream().mapToInt(Integer::intValue).sum();
        long linkedHashSetIterateTime = System.nanoTime() - startTime;
        
        // TreeSet 迭代
        startTime = System.nanoTime();
        int treeSetSum = treeSet.stream().mapToInt(Integer::intValue).sum();
        long treeSetIterateTime = System.nanoTime() - startTime;
        
        System.out.printf("HashSet 迭代求和: %.2f ms (结果: %d)%n", 
                hashSetIterateTime / 1_000_000.0, hashSetSum);
        System.out.printf("LinkedHashSet 迭代求和: %.2f ms (结果: %d)%n", 
                linkedHashSetIterateTime / 1_000_000.0, linkedHashSetSum);
        System.out.printf("TreeSet 迭代求和: %.2f ms (结果: %d)%n", 
                treeSetIterateTime / 1_000_000.0, treeSetSum);
        
        // 4. 内存使用分析
        analyzeMemoryUsage(hashSet, linkedHashSet, treeSet);
    }

    /**
     * 分析不同 Set 实现的内存使用
     */
    private void analyzeMemoryUsage(Set<Integer> hashSet, Set<Integer> linkedHashSet, Set<Integer> treeSet) {
        System.out.println("\n--- 内存使用分析 ---");
        
        Runtime runtime = Runtime.getRuntime();
        
        // 强制垃圾回收
        System.gc();
        long baseMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // 创建大量对象来观察内存使用差异
        int testSize = 10000;
        
        System.gc();
        long beforeHashSet = runtime.totalMemory() - runtime.freeMemory();
        Set<Integer> testHashSet = new HashSet<>();
        for (int i = 0; i < testSize; i++) {
            testHashSet.add(i);
        }
        long afterHashSet = runtime.totalMemory() - runtime.freeMemory();
        
        System.gc();
        long beforeLinkedHashSet = runtime.totalMemory() - runtime.freeMemory();
        Set<Integer> testLinkedHashSet = new LinkedHashSet<>();
        for (int i = 0; i < testSize; i++) {
            testLinkedHashSet.add(i);
        }
        long afterLinkedHashSet = runtime.totalMemory() - runtime.freeMemory();
        
        System.gc();
        long beforeTreeSet = runtime.totalMemory() - runtime.freeMemory();
        Set<Integer> testTreeSet = new TreeSet<>();
        for (int i = 0; i < testSize; i++) {
            testTreeSet.add(i);
        }
        long afterTreeSet = runtime.totalMemory() - runtime.freeMemory();
        
        System.out.printf("HashSet (%d 元素) 内存使用: ~%d KB%n", 
                testSize, (afterHashSet - beforeHashSet) / 1024);
        System.out.printf("LinkedHashSet (%d 元素) 内存使用: ~%d KB%n", 
                testSize, (afterLinkedHashSet - beforeLinkedHashSet) / 1024);
        System.out.printf("TreeSet (%d 元素) 内存使用: ~%d KB%n", 
                testSize, (afterTreeSet - beforeTreeSet) / 1024);
        
        System.out.println("\n注意：内存使用测量可能不够精确，仅供参考。");
        System.out.println("一般来说：TreeSet > LinkedHashSet > HashSet (内存使用)");
    }

    /**
     * 演示 TreeSet 的实际应用场景
     */
    public void demonstrateUseCases() {
        System.out.println("\n=== TreeSet 实际应用场景演示 ===");
        
        // 1. 排行榜系统
        demonstrateLeaderboard();
        
        // 2. 时间窗口数据管理
        demonstrateTimeWindowData();
        
        // 3. 范围查询应用
        demonstrateRangeQueryApplication();
    }

    /**
     * 排行榜系统演示
     */
    private void demonstrateLeaderboard() {
        System.out.println("\n--- 排行榜系统 ---");
        
        // 使用 TreeSet 实现自动排序的排行榜
        TreeSet<PlayerScore> leaderboard = new TreeSet<>(Comparator
                .comparing(PlayerScore::getScore).reversed()
                .thenComparing(PlayerScore::getPlayerName));
        
        // 添加玩家分数
        leaderboard.add(new PlayerScore("Alice", 1500));
        leaderboard.add(new PlayerScore("Bob", 1200));
        leaderboard.add(new PlayerScore("Charlie", 1800));
        leaderboard.add(new PlayerScore("David", 1200)); // 同分
        leaderboard.add(new PlayerScore("Eve", 1600));
        leaderboard.add(new PlayerScore("Frank", 1100));
        
        System.out.println("排行榜 (按分数降序，同分按姓名升序):");
        int rank = 1;
        for (PlayerScore player : leaderboard) {
            System.out.printf("%d. %s - %d 分%n", rank++, player.getPlayerName(), player.getScore());
        }
        
        // 获取前三名
        System.out.println("\n前三名:");
        leaderboard.stream().limit(3).forEach(player -> 
                System.out.println(player.getPlayerName() + ": " + player.getScore()));
        
        // 获取分数在 1200-1600 之间的玩家
        PlayerScore lowerBound = new PlayerScore("", 1200);
        PlayerScore upperBound = new PlayerScore("~", 1600); // 使用特殊字符确保包含所有同分玩家
        
        SortedSet<PlayerScore> midRangePlayer = leaderboard.subSet(upperBound, true, lowerBound, true);
        System.out.println("\n分数在 1200-1600 之间的玩家:");
        midRangePlayer.forEach(player -> 
                System.out.println(player.getPlayerName() + ": " + player.getScore()));
    }

    /**
     * 时间窗口数据管理演示
     */
    private void demonstrateTimeWindowData() {
        System.out.println("\n--- 时间窗口数据管理 ---");
        
        // 使用 TreeSet 管理时间序列数据
        TreeSet<TimestampedEvent> eventWindow = new TreeSet<>(Comparator
                .comparing(TimestampedEvent::getTimestamp)
                .thenComparing(TimestampedEvent::getEventId));
        
        long currentTime = System.currentTimeMillis();
        
        // 添加一些事件
        eventWindow.add(new TimestampedEvent("E001", currentTime - 5000, "用户登录"));
        eventWindow.add(new TimestampedEvent("E002", currentTime - 4000, "页面访问"));
        eventWindow.add(new TimestampedEvent("E003", currentTime - 3000, "商品查看"));
        eventWindow.add(new TimestampedEvent("E004", currentTime - 2000, "添加购物车"));
        eventWindow.add(new TimestampedEvent("E005", currentTime - 1000, "下单"));
        eventWindow.add(new TimestampedEvent("E006", currentTime, "支付"));
        
        System.out.println("所有事件 (按时间排序):");
        eventWindow.forEach(event -> 
                System.out.printf("%s: %s (时间: %d)%n", 
                        event.getEventId(), event.getDescription(), event.getTimestamp()));
        
        // 获取最近 3 秒内的事件
        long threeSecondsAgo = currentTime - 3000;
        TimestampedEvent timeThreshold = new TimestampedEvent("", threeSecondsAgo, "");
        
        SortedSet<TimestampedEvent> recentEvents = eventWindow.tailSet(timeThreshold);
        System.out.println("\n最近 3 秒内的事件:");
        recentEvents.forEach(event -> 
                System.out.printf("%s: %s%n", event.getEventId(), event.getDescription()));
        
        // 清理过期事件（保留最近 2 秒）
        long twoSecondsAgo = currentTime - 2000;
        TimestampedEvent cleanupThreshold = new TimestampedEvent("", twoSecondsAgo, "");
        
        SortedSet<TimestampedEvent> expiredEvents = eventWindow.headSet(cleanupThreshold);
        System.out.println("\n清理过期事件 (2秒前): " + expiredEvents.size() + " 个");
        eventWindow.removeAll(new ArrayList<>(expiredEvents));
        
        System.out.println("清理后剩余事件: " + eventWindow.size() + " 个");
    }

    /**
     * 范围查询应用演示
     */
    private void demonstrateRangeQueryApplication() {
        System.out.println("\n--- 范围查询应用：价格区间搜索 ---");
        
        // 商品价格管理
        TreeSet<Product> productsByPrice = new TreeSet<>(Comparator
                .comparing(Product::getPrice)
                .thenComparing(Product::getName));
        
        // 添加商品
        productsByPrice.add(new Product("笔记本电脑", 5999.0));
        productsByPrice.add(new Product("台式机", 3999.0));
        productsByPrice.add(new Product("平板电脑", 2999.0));
        productsByPrice.add(new Product("智能手机", 1999.0));
        productsByPrice.add(new Product("智能手表", 1299.0));
        productsByPrice.add(new Product("蓝牙耳机", 299.0));
        productsByPrice.add(new Product("充电宝", 99.0));
        productsByPrice.add(new Product("数据线", 29.0));
        
        System.out.println("所有商品 (按价格排序):");
        productsByPrice.forEach(product -> 
                System.out.printf("%s: ¥%.2f%n", product.getName(), product.getPrice()));
        
        // 价格区间查询
        double minPrice = 1000.0;
        double maxPrice = 4000.0;
        
        Product lowerBound = new Product("", minPrice);
        Product upperBound = new Product("~", maxPrice);
        
        SortedSet<Product> priceRangeProducts = productsByPrice.subSet(lowerBound, true, upperBound, true);
        
        System.out.printf("\n价格在 ¥%.2f - ¥%.2f 之间的商品:%n", minPrice, maxPrice);
        priceRangeProducts.forEach(product -> 
                System.out.printf("%s: ¥%.2f%n", product.getName(), product.getPrice()));
        
        // 查找最便宜和最贵的商品
        System.out.println("\n最便宜的商品: " + productsByPrice.first().getName() + 
                " (¥" + productsByPrice.first().getPrice() + ")");
        System.out.println("最贵的商品: " + productsByPrice.last().getName() + 
                " (¥" + productsByPrice.last().getPrice() + ")");
        
        // 查找价格刚好低于/高于某个值的商品
        double targetPrice = 2000.0;
        Product target = new Product("", targetPrice);
        
        Product lowerProduct = productsByPrice.lower(target);
        Product higherProduct = productsByPrice.higher(target);
        
        if (lowerProduct != null) {
            System.out.printf("价格低于 ¥%.2f 的最贵商品: %s (¥%.2f)%n", 
                    targetPrice, lowerProduct.getName(), lowerProduct.getPrice());
        }
        
        if (higherProduct != null) {
            System.out.printf("价格高于 ¥%.2f 的最便宜商品: %s (¥%.2f)%n", 
                    targetPrice, higherProduct.getName(), higherProduct.getPrice());
        }
    }

    /**
     * 综合演示
     */
    public void comprehensiveDemo() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("TreeSet 综合演示");
        System.out.println("=".repeat(50));
        
        demonstrateBasicOrdering();
        demonstrateCustomComparator();
        demonstrateRangeQueries();
        analyzeInternalStructure();
        compareWithOtherSets();
        demonstrateUseCases();
        
        System.out.println("\n" + "=".repeat(50));
        System.out.println("TreeSet 特性总结:");
        System.out.println("1. 基于红黑树实现，保证 O(log n) 的性能");
        System.out.println("2. 自动维护元素的有序性");
        System.out.println("3. 支持自定义比较器");
        System.out.println("4. 提供强大的范围查询功能");
        System.out.println("5. 不允许 null 元素");
        System.out.println("6. 非线程安全");
        System.out.println("7. 适用于需要有序集合和范围查询的场景");
        System.out.println("=".repeat(50));
    }

    // 内部类和数据类
    
    /**
     * 学生类，用于演示复杂对象排序
     */
    static class Student {
        private final String name;
        private final int grade;
        
        public Student(String name, int grade) {
            this.name = name;
            this.grade = grade;
        }
        
        public String getName() { return name; }
        public int getGrade() { return grade; }
        
        @Override
        public String toString() {
            return String.format("Student{name='%s', grade=%d}", name, grade);
        }
    }
    
    /**
     * 玩家分数类，用于排行榜演示
     */
    static class PlayerScore {
        private final String playerName;
        private final int score;
        
        public PlayerScore(String playerName, int score) {
            this.playerName = playerName;
            this.score = score;
        }
        
        public String getPlayerName() { return playerName; }
        public int getScore() { return score; }
        
        @Override
        public String toString() {
            return String.format("PlayerScore{playerName='%s', score=%d}", playerName, score);
        }
    }
    
    /**
     * 时间戳事件类，用于时间窗口演示
     */
    static class TimestampedEvent {
        private final String eventId;
        private final long timestamp;
        private final String description;
        
        public TimestampedEvent(String eventId, long timestamp, String description) {
            this.eventId = eventId;
            this.timestamp = timestamp;
            this.description = description;
        }
        
        public String getEventId() { return eventId; }
        public long getTimestamp() { return timestamp; }
        public String getDescription() { return description; }
        
        @Override
        public String toString() {
            return String.format("TimestampedEvent{eventId='%s', timestamp=%d, description='%s'}", 
                    eventId, timestamp, description);
        }
    }
    
    /**
     * 商品类，用于价格区间查询演示
     */
    static class Product {
        private final String name;
        private final double price;
        
        public Product(String name, double price) {
            this.name = name;
            this.price = price;
        }
        
        public String getName() { return name; }
        public double getPrice() { return price; }
        
        @Override
        public String toString() {
            return String.format("Product{name='%s', price=%.2f}", name, price);
        }
    }
    
    /**
     * 主方法，运行所有演示
     */
    public static void main(String[] args) {
        TreeSetDemo demo = new TreeSetDemo();
        demo.comprehensiveDemo();
    }
}