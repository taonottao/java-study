package com.trae.study.collections.util;

import java.util.*;

/**
 * Collections 与 Arrays 工具方法演示
 *
 * 覆盖常见但易忽视的工具方法、不可变集合模式与防御性拷贝。
 */
public class CollectionsArraysUtilityDemo {

    /** 排序、二分查找与旋转/洗牌/填充等综合演示 */
    public static class OpsResultDTO {
        private final List<Integer> sorted;
        private final int indexOf42;
        private final List<Integer> rotated;
        private final List<Integer> shuffled;
        private final List<Integer> filled;
        public OpsResultDTO(List<Integer> sorted, int indexOf42, List<Integer> rotated,
                             List<Integer> shuffled, List<Integer> filled) {
            this.sorted = sorted; this.indexOf42 = indexOf42; this.rotated = rotated;
            this.shuffled = shuffled; this.filled = filled;
        }
        public List<Integer> getSorted() { return sorted; }
        public int getIndexOf42() { return indexOf42; }
        public List<Integer> getRotated() { return rotated; }
        public List<Integer> getShuffled() { return shuffled; }
        public List<Integer> getFilled() { return filled; }
    }

    /** 不可变集合 DTO */
    public static class ImmutableShowcaseDTO {
        private final List<String> listOf;
        private final Set<String> setOf;
        private final Map<String, Integer> mapOf;
        public ImmutableShowcaseDTO(List<String> listOf, Set<String> setOf, Map<String, Integer> mapOf) {
            this.listOf = listOf; this.setOf = setOf; this.mapOf = mapOf;
        }
        public List<String> getListOf() { return listOf; }
        public Set<String> getSetOf() { return setOf; }
        public Map<String, Integer> getMapOf() { return mapOf; }
    }

    /** Arrays 工具方法结果 */
    public static class ArraysOpsResultDTO {
        private final List<Integer> asListView;
        private final int[] copyOf;
        private final int pos;
        public ArraysOpsResultDTO(List<Integer> asListView, int[] copyOf, int pos) {
            this.asListView = asListView; this.copyOf = copyOf; this.pos = pos;
        }
        public List<Integer> getAsListView() { return asListView; }
        public int[] getCopyOf() { return copyOf; }
        public int getPos() { return pos; }
    }

    /** Collections 常用方法综合演示 */
    public OpsResultDTO demonstrateCollectionsOps(List<Integer> input) {
        List<Integer> list = new ArrayList<>(input); // 防御性拷贝
        Collections.sort(list); // 升序
        int idx = Collections.binarySearch(list, 42);
        List<Integer> rotated = new ArrayList<>(list);
        Collections.rotate(rotated, 2);
        List<Integer> shuffled = new ArrayList<>(list);
        Collections.shuffle(shuffled, new Random(1));
        List<Integer> filled = new ArrayList<>(Collections.nCopies(5, 0));
        Collections.fill(filled, 7);
        return new OpsResultDTO(list, idx, rotated, shuffled, filled);
    }

    /** 不可变集合与防御性拷贝的推荐模式 */
    public ImmutableShowcaseDTO demonstrateImmutableCollections() {
        List<String> listOf = List.of("A", "B", "C");
        Set<String> setOf = Set.of("x", "y", "z");
        Map<String, Integer> mapOf = Map.of("k1", 1, "k2", 2);
        return new ImmutableShowcaseDTO(listOf, setOf, mapOf);
    }

    /** Arrays 常见工具：asList 视图、copyOf 扩容、parallelSort 与二分查找 */
    public ArraysOpsResultDTO demonstrateArraysOps(int[] arr) {
        // Arrays.asList 原生类型会变成单个元素的 List<int[]>；因此演示引用类型
        Integer[] boxed = Arrays.stream(arr).boxed().toArray(Integer[]::new);
        List<Integer> asListView = Arrays.asList(boxed); // 固定大小视图，支持 set，不支持 add/remove
        int[] copy = Arrays.copyOf(arr, arr.length + 3);
        Arrays.fill(copy, arr.length, copy.length, -1);
        Arrays.parallelSort(copy);
        int pos = Arrays.binarySearch(copy, -1);
        return new ArraysOpsResultDTO(asListView, copy, pos);
    }
}