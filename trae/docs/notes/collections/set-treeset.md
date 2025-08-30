# TreeSet 源码与用法进阶

> 关联代码：
> - src/main/java/com/trae/study/collections/set/TreeSetDemo.java
> - 配图：docs/notes/collections/svg/treeset.svg

## 1. 核心结构
- 基于 TreeMap（红黑树）实现，按可比较性排序并去重
- 去重依据 compareTo/Comparator 的“等价为 0”

## 2. 典型用法
- 有序去重：自然顺序或自定义 Comparator
- 截取区间：subSet/headSet/tailSet（半开区间语义）
- 有序视图：descendingSet、迭代器逆序

## 3. 关键点与注意事项
- compareTo 一致性：与 equals 一致有助于避免“看似相等却能共存”的困惑
- 自定义 Comparator：明确空值处理、字段权重与稳定性
- 视图是活的：视图上的修改会反映到原集合

## 4. 小结
当需要“排序 + 去重 + 有序视图”时，TreeSet 是首选。注意比较器的一致性与视图的语义。