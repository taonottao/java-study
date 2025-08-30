package com.trae.study.collections.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Iterator Fail-Fast 演示测试")
class IteratorFailFastDemoTest {

    @Test
    @DisplayName("List fail-fast 触发")
    void testFailFast() {
        IteratorFailFastDemo demo = new IteratorFailFastDemo();
        var r = demo.demonstrateFailFastOnList();
        assertTrue(r.isThrew());
        assertEquals("ConcurrentModificationException", r.getMessage());
    }

    @Test
    @DisplayName("迭代器安全删除")
    void testSafeRemove() {
        IteratorFailFastDemo demo = new IteratorFailFastDemo();
        var list = demo.safeRemoveViaIterator();
        assertEquals(java.util.Arrays.asList(1,3,5), list);
    }
}