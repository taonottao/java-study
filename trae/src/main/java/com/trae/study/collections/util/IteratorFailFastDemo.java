package com.trae.study.collections.util;

import java.util.*;

/**
 * Iterator/Iterable 的 fail-fast 机制演示
 *
 * 通过 modCount 变化导致迭代时结构性修改抛出 ConcurrentModificationException。
 */
public class IteratorFailFastDemo {

    /** 结果 DTO */
    public static class FailFastResultDTO {
        private final boolean threw;
        private final String message;
        public FailFastResultDTO(boolean threw, String message) { this.threw = threw; this.message = message; }
        public boolean isThrew() { return threw; }
        public String getMessage() { return message; }
    }

    /** 在 for-each 过程中调用集合 add 触发 fail-fast */
    public FailFastResultDTO demonstrateFailFastOnList() {
        List<Integer> list = new ArrayList<>(Arrays.asList(1,2,3));
        try {
            for (Integer x : list) {
                if (x == 2) list.add(99); // 结构性修改，导致 modCount 变化
            }
            return new FailFastResultDTO(false, "no exception");
        } catch (ConcurrentModificationException e) {
            return new FailFastResultDTO(true, e.getClass().getSimpleName());
        }
    }

    /** 使用 Iterator.remove 在迭代中安全删除 */
    public List<Integer> safeRemoveViaIterator() {
        List<Integer> list = new ArrayList<>(Arrays.asList(1,2,3,4,5));
        Iterator<Integer> it = list.iterator();
        while (it.hasNext()) {
            if (it.next() % 2 == 0) it.remove();
        }
        return list; // 保留奇数
    }
}