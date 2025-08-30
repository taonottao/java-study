# Collections 与 Arrays 常用工具与不可变集合模式

> 关联代码：
> - src/main/java/com/trae/study/collections/util/CollectionsArraysUtilityDemo.java
> - src/test/java/com/trae/study/collections/util/CollectionsArraysUtilityDemoTest.java
> - 配图：docs/notes/collections/svg/collections-arrays.svg

## 1. 常用操作清单（Collections）
- 排序：Collections.sort(list, comparator) 或 list.sort(comparator)
- 二分查找：Collections.binarySearch(sortedList, key)
- 旋转：Collections.rotate(list, distance)
- 洗牌：Collections.shuffle(list)
- 填充：Collections.fill(list, value)
- 同步包装：Collections.synchronizedList/Set/Map

## 2. Arrays 常用操作
- 视图：Arrays.asList(array) 返回定长视图，修改元素会反映到原数组，不能增删
- 扩容：Arrays.copyOf(arr, newLen)
- 并行排序：Arrays.parallelSort(arr)
- 二分查找：Arrays.binarySearch(arr, key)

## 3. 不可变集合与防御性拷贝
- JDK 9+ 工厂方法：List.of/Set.of/Map.of 创建不可变集合
- 防御性拷贝：对外暴露集合时返回副本或不可变视图，避免外部修改内部状态

## 4. 容易忽视的点
- Arrays.asList 返回的是固定大小的列表，调用 add/remove 会抛异常
- binarySearch 前提是有序（按相同比较规则），否则结果未定义
- Collections.shuffle/rotate 等操作会原地修改传入列表，必要时先拷贝

## 5. 小结
- 善用工具类可极大简化日常集合处理；不可变集合和防御性拷贝是可靠 API 的基础