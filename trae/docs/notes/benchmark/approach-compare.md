# 基准方法对比：Hutool StopWatch vs JMH

目标：以小规模功能验证用 Hutool StopWatch；以更严谨的微基准采用 JMH，并比较两者差异与适用场景。

- Hutool StopWatch：
  - 使用简单，适合功能性对比与粗略耗时估计。
  - 易受 JIT、逃逸分析、GC、CPU 噪声影响，结果波动较大。
- JMH：
  - 由 OpenJDK 团队维护，内建 warmup、fork、measurement、基准模式选择（Throughput、AverageTime 等）。
  - 能更好地避免常见的基准陷阱（如常量折叠、消除死码）。
- 建议：
  - 学习/演示阶段：先用 StopWatch 感知数量级，再用 JMH 做严谨验证。
  - 输出统一为 BenchmarkResultDTO 以便对比。