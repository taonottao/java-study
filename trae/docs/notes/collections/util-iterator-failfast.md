# Iterator / Iterable 的 fail-fast 机制与安全删除

> 关联代码：
> - src/main/java/com/trae/study/collections/util/IteratorFailFastDemo.java
> - src/test/java/com/trae/study/collections/util/IteratorFailFastDemoTest.java
> - 配图：docs/notes/collections/svg/iterator-failfast.svg

## 1. fail-fast 的本质
- 大多数非并发集合（ArrayList/HashMap 等）在迭代期间检测结构性修改（modCount 变化），一旦检测到就抛出 ConcurrentModificationException
- 目的：快速暴露并发修改或错误用法，避免返回错误结果
- 结构性修改：改变集合大小的操作，如 add/remove/clear；替换某个已有元素通常不算结构性修改

## 2. 触发示例
- 在 for-each（语法糖：基于 Iterator）遍历 ArrayList 时，直接调用 list.add/remove 破坏迭代一致性 → 触发 fail-fast
- 推荐做法：在迭代时使用 Iterator.remove 进行删除，或先收集待操作元素，迭代完成后统一修改

## 3. 安全删除示例
- 使用 Iterator.remove() 删除满足条件的元素，可以在迭代中安全修改集合大小
- 只允许删除最近一次 next() 返回的元素，连续调用 remove() 会抛出 IllegalStateException

## 4. 与并发集合的区别
- ConcurrentHashMap、CopyOnWriteArrayList 等采用弱一致性迭代器（不抛 CME），能容忍并发修改，但遍历结果可能不反映最新状态

## 5. 小结
- 原则：迭代期间不要直接修改原集合；需要删除时使用 Iterator.remove
- 若有并发修改需求，使用并发集合或在外层加锁