package com.trae.study.collections.queue;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * PriorityQueue 演示
 *
 * 展示最小堆的有序出队特性、自定义比较器、Top-K 问题与多路有序列表归并等典型用法。
 */
public class PriorityQueueDemo {

    /** 任务模型：数值越小优先级越高（最小堆） */
    public static class Task {
        private final String name;
        private final int priority;
        public Task(String name, int priority) { this.name = name; this.priority = priority; }
        public String getName() { return name; }
        public int getPriority() { return priority; }
        @Override public String toString() { return name + "(p=" + priority + ")"; }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Task)) return false;
            Task task = (Task) o;
            return priority == task.priority && Objects.equals(name, task.name);
        }
        @Override public int hashCode() { return Objects.hash(name, priority); }
    }

    /** TopK 结果 DTO */
    public static class TopKResultDTO {
        private final String algorithm;
        private final List<Integer> topK;
        private final long timeNs;
        public TopKResultDTO(String algorithm, List<Integer> topK, long timeNs) {
            this.algorithm = algorithm; this.topK = topK; this.timeNs = timeNs;
        }
        public String getAlgorithm() { return algorithm; }
        public List<Integer> getTopK() { return topK; }
        public long getTimeNs() { return timeNs; }
    }

    /** 多路归并结果 DTO */
    public static class MergeResultDTO {
        private final int inputLists;
        private final int totalCount;
        private final List<Integer> merged;
        private final long timeNs;
        public MergeResultDTO(int inputLists, int totalCount, List<Integer> merged, long timeNs) {
            this.inputLists = inputLists; this.totalCount = totalCount; this.merged = merged; this.timeNs = timeNs;
        }
        public int getInputLists() { return inputLists; }
        public int getTotalCount() { return totalCount; }
        public List<Integer> getMerged() { return merged; }
        public long getTimeNs() { return timeNs; }
    }

    /** 基本行为演示：入队/出队顺序由最小堆决定 */
    public List<Task> demonstrateBasicBehavior() {
        PriorityQueue<Task> pq = new PriorityQueue<>(Comparator.comparingInt(Task::getPriority));
        pq.offer(new Task("T1", 5));
        pq.offer(new Task("T2", 1));
        pq.offer(new Task("T3", 3));
        pq.offer(new Task("T4", 2));
        pq.offer(new Task("T5", 4));
        List<Task> order = new ArrayList<>();
        while (!pq.isEmpty()) { order.add(pq.poll()); }
        return order; // 期望优先级从小到大
    }

    /** 自定义比较器演示：字符串按长度升序，其次字典序 */
    public List<String> demonstrateCustomComparator(List<String> inputs) {
        PriorityQueue<String> pq = new PriorityQueue<>((a, b) -> {
            int c = Integer.compare(a.length(), b.length());
            return c != 0 ? c : a.compareTo(b);
        });
        pq.addAll(inputs);
        List<String> order = new ArrayList<>();
        while (!pq.isEmpty()) { order.add(pq.poll()); }
        return order;
    }

    /** Top-K 最大元素：维护大小为 k 的最小堆，时间复杂度 O(n log k) */
    public TopKResultDTO topKMax(int[] nums, int k) {
        long start = System.nanoTime();
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(k);
        for (int x : nums) {
            if (minHeap.size() < k) minHeap.offer(x);
            else if (x > minHeap.peek()) { minHeap.poll(); minHeap.offer(x); }
        }
        List<Integer> res = new ArrayList<>(minHeap);
        // 从小到大，反转得到从大到小
        res.sort(Comparator.reverseOrder());
        long end = System.nanoTime();
        return new TopKResultDTO("min-heap O(n log k)", res, end - start);
    }

    /** 合并 k 个有序升序列表，使用最小堆按当前元素最小出队 */
    public MergeResultDTO mergeKSortedLists(List<List<Integer>> sortedLists) {
        class Node { int listIdx; int elemIdx; int val; Node(int l, int e, int v){listIdx=l;elemIdx=e;val=v;} }
        long start = System.nanoTime();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.val));
        int total = 0;
        for (int i = 0; i < sortedLists.size(); i++) {
            List<Integer> lst = sortedLists.get(i);
            if (!lst.isEmpty()) { pq.offer(new Node(i, 0, lst.get(0))); }
            total += lst.size();
        }
        List<Integer> merged = new ArrayList<>(total);
        while (!pq.isEmpty()) {
            Node n = pq.poll();
            merged.add(n.val);
            int nextIdx = n.elemIdx + 1;
            List<Integer> src = sortedLists.get(n.listIdx);
            if (nextIdx < src.size()) pq.offer(new Node(n.listIdx, nextIdx, src.get(nextIdx)));
        }
        long end = System.nanoTime();
        return new MergeResultDTO(sortedLists.size(), total, merged, end - start);
    }

    /** 生成用于 Top-K/归并的示例数据 */
    public int[] randomInts(int n, int bound) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = ThreadLocalRandom.current().nextInt(bound);
        return a;
    }

    public List<List<Integer>> randomSortedLists(int listCount, int listLen, int bound) {
        List<List<Integer>> lists = new ArrayList<>();
        for (int i = 0; i < listCount; i++) {
            List<Integer> l = new ArrayList<>(listLen);
            for (int j = 0; j < listLen; j++) l.add(ThreadLocalRandom.current().nextInt(bound));
            Collections.sort(l);
            lists.add(l);
        }
        return lists;
    }
}