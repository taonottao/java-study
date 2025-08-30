# 集合框架总览（源码级视角）

目标：梳理 Collection/Map/Iterator/Iterable 的关系与 fail-fast 原理（modCount），并给出源码入口与常见选择指南。

- 层次结构：Collection（List/Set/Queue）与 Map 两大分支；Iterator 基于集合内部 modCount 做 fail-fast 检测。
- 源码入口建议：
  - java.util.ArrayList：动态数组、扩容 ensureCapacityInternal、subList 视图
  - java.util.HashMap：扰动函数、putVal、resize、treeifyBin/untreeify
  - java.util.LinkedHashMap：访问顺序 + removeEldestEntry 钩子（可实现 LRU）
  - java.util.concurrent.ConcurrentHashMap：桶级 CAS、树化
- fail-fast：迭代器记录 expectedModCount，与容器 modCount 不一致时抛 ConcurrentModificationException。
- 选择指南：
  - 读多写少：CopyOnWriteArrayList
  - 无序去重：HashSet；有序：LinkedHashSet；排序：TreeSet
  - 需要 LRU：LinkedHashMap(accessOrder=true)+removeEldestEntry
  - 并发 Map：高并发读写选 ConcurrentHashMap；需要有序并发选 ConcurrentSkipListMap