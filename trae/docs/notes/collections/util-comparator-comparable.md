# Comparable 与 Comparator：自然顺序与外部比较器

> 关联代码：
> - src/main/java/com/trae/study/collections/util/ComparatorComparableDemo.java
> - src/test/java/com/trae/study/collections/util/ComparatorComparableDemoTest.java
> - 配图：docs/notes/collections/svg/comparator-comparable.svg

## 1. 两者定位
- Comparable：对象自身定义“自然顺序”（compareTo），常用于排序、TreeSet/TreeMap 的默认顺序
- Comparator：外部比较器，支持链式组合（thenComparing）、空值处理（nullsFirst/nullsLast）、反转（reverseOrder）等

## 2. TreeSet 的去重语义
- HashSet 根据 equals/hashCode 去重
- TreeSet 根据比较器的“相等”（compare 返回 0）去重：例如仅按长度比较的字符串集合，会把长度相同的字符串视为重复

## 3. 常用技巧
- 链式比较：Comparator.comparing(keyExtractor).thenComparing(...)
- 空值处理：Comparator.nullsFirst/nulsLast
- 反转：Comparator.reverseOrder 或 comparator.reversed()

## 4. 设计建议
- 保证比较器满足自反性、对称性、传递性，避免违反导致树结构异常
- 对可变对象，避免用于排序关键字段在放入集合后被修改
- equals/hashCode 与 compareTo 的一致性：如果使用在 TreeSet/TreeMap，建议定义相同的“相等”语义

## 5. 小结
- 自然顺序用于领域模型的默认排序；外部比较器用于灵活排序需求
- TreeSet 的去重语义取决于比较器定义，应配合测试验证