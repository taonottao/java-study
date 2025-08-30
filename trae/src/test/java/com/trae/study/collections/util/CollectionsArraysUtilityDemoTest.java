package com.trae.study.collections.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Collections/Arrays 工具演示测试")
class CollectionsArraysUtilityDemoTest {

    @Test
    @DisplayName("Collections 综合操作")
    void testCollectionsOps() {
        CollectionsArraysUtilityDemo demo = new CollectionsArraysUtilityDemo();
        var r = demo.demonstrateCollectionsOps(Arrays.asList(10, 42, 7, 9, 100));
        assertEquals(Arrays.asList(7,9,10,42,100), r.getSorted());
        assertTrue(r.getIndexOf42() >= 0);
        assertEquals(5, r.getFilled().size());
    }

    @Test
    @DisplayName("不可变集合")
    void testImmutable() {
        CollectionsArraysUtilityDemo demo = new CollectionsArraysUtilityDemo();
        var r = demo.demonstrateImmutableCollections();
        assertThrows(UnsupportedOperationException.class, () -> r.getListOf().add("X"));
        assertThrows(UnsupportedOperationException.class, () -> r.getSetOf().add("X"));
        assertThrows(UnsupportedOperationException.class, () -> r.getMapOf().put("k3", 3));
    }

    @Test
    @DisplayName("Arrays 常见工具")
    void testArraysOps() {
        CollectionsArraysUtilityDemo demo = new CollectionsArraysUtilityDemo();
        var r = demo.demonstrateArraysOps(new int[]{5,2,9});
        assertEquals(Arrays.asList(5,2,9), r.getAsListView());
        assertTrue(r.getCopyOf().length >= 3);
        assertTrue(r.getPos() >= 0);
    }
}