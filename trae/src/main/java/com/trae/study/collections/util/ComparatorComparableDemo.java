package com.trae.study.collections.util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Comparator / Comparable 演示
 *
 * - Comparable：定义对象的“自然顺序”
 * - Comparator：定义外部比较规则，支持链式组合、空值处理、反转等
 * - TreeSet/TreeMap 等有序容器依赖比较器进行去重与排序
 */
public class ComparatorComparableDemo {

    /**
     * 示例实体：Person，按 age 升序，其次按 name 升序 的自然顺序
     */
    public static class Person implements Comparable<Person> {
        private final String name;
        private final int age;
        public Person(String name, int age) {
            this.name = name; this.age = age;
        }
        public String getName() { return name; }
        public int getAge() { return age; }
        @Override
        public int compareTo(Person o) {
            int c = Integer.compare(this.age, o.age);
            if (c != 0) return c;
            return this.name.compareTo(o.name);
        }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Person person = (Person) o;
            return age == person.age && Objects.equals(name, person.name);
        }
        @Override
        public int hashCode() { return Objects.hash(name, age); }
        @Override
        public String toString() { return "Person{" + name + "," + age + '}'; }
    }

    /** Comparator 工具使用的结果 DTO */
    public static class ComparatorOpsResultDTO {
        private final List<Person> naturalSorted;
        private final List<String> byLengthThenLexi;
        private final List<Integer> nullsFirstSorted;
        private final List<String> reversedSorted;
        public ComparatorOpsResultDTO(List<Person> naturalSorted,
                                      List<String> byLengthThenLexi,
                                      List<Integer> nullsFirstSorted,
                                      List<String> reversedSorted) {
            this.naturalSorted = naturalSorted;
            this.byLengthThenLexi = byLengthThenLexi;
            this.nullsFirstSorted = nullsFirstSorted;
            this.reversedSorted = reversedSorted;
        }
        public List<Person> getNaturalSorted() { return naturalSorted; }
        public List<String> getByLengthThenLexi() { return byLengthThenLexi; }
        public List<Integer> getNullsFirstSorted() { return nullsFirstSorted; }
        public List<String> getReversedSorted() { return reversedSorted; }
    }

    /** TreeSet 结果 DTO */
    public static class TreeSetResultDTO {
        private final int size;
        private final List<String> elements;
        public TreeSetResultDTO(int size, List<String> elements) {
            this.size = size; this.elements = elements;
        }
        public int getSize() { return size; }
        public List<String> getElements() { return elements; }
    }

    /** 使用 Comparable 的自然顺序排序 Person */
    public List<Person> demonstrateComparableNaturalOrder(List<Person> input) {
        List<Person> list = new ArrayList<>(input); // 防御性拷贝
        Collections.sort(list); // 使用 Person#compareTo，自然顺序
        return list;
    }

    /** 演示常用 Comparator 工具：thenComparing、nullsFirst、reverseOrder */
    public ComparatorOpsResultDTO demonstrateComparatorUtilities(List<String> strings,
                                                                 List<Integer> numbersWithNulls) {
        // 长度优先，其次字典序
        List<String> byLengthThenLexi = strings.stream()
            .sorted(Comparator.comparingInt(String::length)
                .thenComparing(Comparator.naturalOrder()))
            .collect(Collectors.toList());

        // nullsFirst + 自然顺序
        List<Integer> nullsFirstSorted = new ArrayList<>(numbersWithNulls);
        nullsFirstSorted.sort(Comparator.nullsFirst(Integer::compareTo));

        // 自然序反转
        List<String> reversedSorted = strings.stream()
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());

        // 自然顺序排序 Person
        List<Person> naturalSorted = demonstrateComparableNaturalOrder(Arrays.asList(
            new Person("Alice", 30), new Person("Bob", 25), new Person("Aaron", 30)
        ));

        return new ComparatorOpsResultDTO(naturalSorted, byLengthThenLexi, nullsFirstSorted, reversedSorted);
    }

    /** 使用仅“长度”作为比较器的 TreeSet，展示“按比较器定义去重” */
    public TreeSetResultDTO demonstrateTreeSetWithComparator(List<String> input) {
        // 仅按长度比较：长度相同视为“相等”，会被 TreeSet 去重
        Comparator<String> byLengthOnly = Comparator.comparingInt(String::length);
        TreeSet<String> set = new TreeSet<>(byLengthOnly);
        set.addAll(input);
        return new TreeSetResultDTO(set.size(), new ArrayList<>(set)); // 按长度升序
    }
}