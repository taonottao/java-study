package com.trae.study.collections.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Comparator/Comparable 工具演示测试")
class ComparatorComparableDemoTest {

    @Test
    @DisplayName("Comparable 自然顺序排序")
    void testComparableNaturalOrder() {
        ComparatorComparableDemo.Person p1 = new ComparatorComparableDemo.Person("Bob", 25);
        ComparatorComparableDemo.Person p2 = new ComparatorComparableDemo.Person("Alice", 30);
        ComparatorComparableDemo.Person p3 = new ComparatorComparableDemo.Person("Aaron", 30);
        List<ComparatorComparableDemo.Person> sorted = new ComparatorComparableDemo()
                .demonstrateComparableNaturalOrder(Arrays.asList(p1, p2, p3));
        assertEquals(Arrays.asList(p1, p3, p2), sorted); // age 升序，age 相同按 name 升序
    }

    @Test
    @DisplayName("Comparator 工具：thenComparing/nullsFirst/reverseOrder")
    void testComparatorUtilities() {
        List<String> strings = Arrays.asList("bbb", "a", "cc", "dd", "eee", "bb");
        List<Integer> numbers = Arrays.asList(null, 3, 1, null, 2);
        ComparatorComparableDemo.ComparatorOpsResultDTO r = new ComparatorComparableDemo()
                .demonstrateComparatorUtilities(strings, numbers);

        // 验证长度优先其次字典序（Java 11 兼容 Collectors.toList）
        List<String> expectedByLenThenLexi = strings.stream()
                .sorted(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder()))
                .collect(Collectors.toList());
        assertEquals(expectedByLenThenLexi, r.getByLengthThenLexi());

        // 验证 nullsFirst
        assertEquals(Arrays.asList(null, null, 1, 2, 3), r.getNullsFirstSorted());

        // 验证反转
        List<String> reversed = new ArrayList<>(strings);
        reversed.sort(Comparator.reverseOrder());
        assertEquals(reversed, r.getReversedSorted());

        // 验证 Person 自然顺序
        assertEquals(Arrays.asList(
                new ComparatorComparableDemo.Person("Bob", 25),
                new ComparatorComparableDemo.Person("Aaron", 30),
                new ComparatorComparableDemo.Person("Alice", 30)
        ), r.getNaturalSorted());
    }

    @Test
    @DisplayName("TreeSet 使用自定义比较器进行去重")
    void testTreeSetWithComparator() {
        List<String> input = Arrays.asList("a", "bb", "cc", "ddd", "ee", "fff", "gg");
        ComparatorComparableDemo.TreeSetResultDTO r = new ComparatorComparableDemo()
                .demonstrateTreeSetWithComparator(input);
        // 仅按长度比较，长度相同会被去重，长度集合应为 [1,2,3]
        assertEquals(3, r.getSize());
        assertEquals(Arrays.asList("a", "bb", "ddd"), r.getElements());
    }
}