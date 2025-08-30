package com.trae.study.collections.deque;

import java.util.*;

/**
 * ArrayDeque 演示
 *
 * 展示环形数组的双端队列特点，支持栈/队列两种典型用法，对比 LinkedList 的 Deque 性能特性。
 */
public class ArrayDequeDemo {

    /** 简单基准结果 DTO */
    public static class DequeBenchmarkDTO {
        private final long arrayDequeNs;
        private final long linkedListNs;
        public DequeBenchmarkDTO(long arrayDequeNs, long linkedListNs) {
            this.arrayDequeNs = arrayDequeNs;
            this.linkedListNs = linkedListNs;
        }
        public long getArrayDequeNs() { return arrayDequeNs; }
        public long getLinkedListNs() { return linkedListNs; }
    }

    /** 基本双端操作顺序演示 */
    public List<Integer> demonstrateBasicDequeOps() {
        Deque<Integer> dq = new ArrayDeque<>();
        dq.addLast(1); // [1]
        dq.addLast(2); // [1,2]
        dq.addFirst(0); // [0,1,2]
        dq.addLast(3); // [0,1,2,3]
        List<Integer> res = new ArrayList<>();
        res.add(dq.removeFirst()); // 0
        res.add(dq.removeLast());  // 3
        res.add(dq.peekFirst());   // 1
        res.add(dq.peekLast());    // 2
        return res;
    }

    /** 使用 ArrayDeque 作为栈（更快且不允许 null） */
    public List<Integer> demonstrateAsStack(List<Integer> inputs) {
        Deque<Integer> stack = new ArrayDeque<>();
        for (Integer x : inputs) stack.push(x);
        List<Integer> pop = new ArrayList<>(inputs.size());
        while (!stack.isEmpty()) pop.add(stack.pop());
        return pop; // 后进先出
    }

    /** 使用 ArrayDeque 作为队列（更快且不允许 null） */
    public List<Integer> demonstrateAsQueue(List<Integer> inputs) {
        Deque<Integer> q = new ArrayDeque<>();
        for (Integer x : inputs) q.offerLast(x);
        List<Integer> out = new ArrayList<>(inputs.size());
        while (!q.isEmpty()) out.add(q.pollFirst());
        return out; // 先进先出
    }

    /** 与 LinkedList 作为 Deque 的简单性能对比（微基准） */
    public DequeBenchmarkDTO benchmarkSimple(int n) {
        int times = n;
        Deque<Integer> arrayDeque = new ArrayDeque<>();
        Deque<Integer> linkedList = new LinkedList<>();
        long t1 = System.nanoTime();
        for (int i = 0; i < times; i++) arrayDeque.addLast(i);
        while (!arrayDeque.isEmpty()) arrayDeque.removeFirst();
        long t2 = System.nanoTime();
        for (int i = 0; i < times; i++) linkedList.addLast(i);
        while (!linkedList.isEmpty()) linkedList.removeFirst();
        long t3 = System.nanoTime();
        return new DequeBenchmarkDTO(t2 - t1, t3 - t2);
    }
}