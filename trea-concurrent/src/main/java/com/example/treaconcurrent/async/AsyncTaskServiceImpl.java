package com.example.treaconcurrent.async;

import com.example.treaconcurrent.model.TaskRequest;
import com.example.treaconcurrent.model.TaskResult;
import com.example.treaconcurrent.model.TaskStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

/**
 * 异步任务服务实现类
 * 提供各种异步任务处理的具体实现
 * 
 * @author 系统
 * @since 1.0
 */
@Slf4j
@Service
public class AsyncTaskServiceImpl implements AsyncTaskService {

    /**
     * 自定义线程池，用于异步任务执行
     */
    private final ExecutorService asyncExecutor = Executors.newFixedThreadPool(8, r -> {
        Thread thread = new Thread(r);
        thread.setName("async-service-" + thread.getId());
        thread.setDaemon(false);
        return thread;
    });

    /**
     * 模拟缓存存储
     */
    private final Map<String, CacheItem> cache = new ConcurrentHashMap<>();

    /**
     * 缓存项内部类
     */
    private static class CacheItem {
        private final String value;
        private final long expireTime;

        public CacheItem(String value, int expireSeconds) {
            this.value = value;
            this.expireTime = System.currentTimeMillis() + expireSeconds * 1000L;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }

        public String getValue() {
            return value;
        }
    }

    @Override
    public CompletableFuture<TaskResult> processTaskAsync(TaskRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("开始异步处理任务: {}, 线程: {}", request.getTaskName(), Thread.currentThread().getName());
            
            try {
                // 模拟任务处理时间（从参数中获取或使用默认值）
                Integer executionTimeParam = request.getIntParameter("executionTimeMs");
                int executionTime = executionTimeParam != null ? executionTimeParam : 1000;
                Thread.sleep(executionTime);
                
                LocalDateTime startTime = LocalDateTime.now().minusSeconds(executionTime / 1000);
                LocalDateTime endTime = LocalDateTime.now();
                
                TaskResult result = TaskResult.builder()
                    .taskId(UUID.randomUUID().toString())
                    .taskName(request.getTaskName())
                    .taskType(request.getTaskType())
                    .status(TaskStatus.COMPLETED)
                    .data("任务 " + request.getTaskName() + " 处理完成")
                    .startTime(startTime)
                    .endTime(endTime)
                    .duration((long) executionTime)
                    .threadName(Thread.currentThread().getName())
                    .build();
                
                log.info("任务处理完成: {}", request.getTaskName());
                return result;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("任务处理被中断: {}", request.getTaskName());
                
                LocalDateTime now = LocalDateTime.now();
                TaskResult result = TaskResult.builder()
                    .taskId(UUID.randomUUID().toString())
                    .taskName(request.getTaskName())
                    .taskType(request.getTaskType())
                    .status(TaskStatus.FAILED)
                    .data("任务被中断")
                    .startTime(now)
                    .endTime(now)
                    .duration(0L)
                    .threadName(Thread.currentThread().getName())
                    .errorMessage("任务被中断")
                    .build();
                
                return result;
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<List<TaskResult>> processBatchTasksAsync(List<TaskRequest> requests) {
        log.info("开始批量处理 {} 个任务", requests.size());
        
        // 为每个请求创建异步任务
        List<CompletableFuture<TaskResult>> futures = requests.stream()
            .map(this::processTaskAsync)
            .collect(Collectors.toList());
        
        // 等待所有任务完成并收集结果
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
            .thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()))
            .whenComplete((results, throwable) -> {
                if (throwable != null) {
                    log.error("批量任务处理异常", throwable);
                } else {
                    log.info("批量任务处理完成，共 {} 个任务", results.size());
                }
            });
    }

    @Override
    public CompletableFuture<Long> calculateFibonacciAsync(int n) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("开始异步计算斐波那契数列第 {} 项，线程: {}", n, Thread.currentThread().getName());
            
            long result = calculateFibonacci(n);
            
            log.info("斐波那契数列第 {} 项计算完成，结果: {}", n, result);
            return result;
        }, asyncExecutor);
    }

    /**
     * 递归计算斐波那契数列
     */
    private long calculateFibonacci(int n) {
        if (n <= 1) {
            return n;
        }
        return calculateFibonacci(n - 1) + calculateFibonacci(n - 2);
    }

    @Override
    public CompletableFuture<String> simulateNetworkRequestAsync(String url, int timeoutMs) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("开始模拟网络请求: {}, 超时时间: {}ms, 线程: {}", 
                url, timeoutMs, Thread.currentThread().getName());
            
            try {
                // 模拟网络延迟
                int actualDelay = (int) (Math.random() * timeoutMs * 0.8); // 80%概率在超时时间内完成
                Thread.sleep(actualDelay);
                
                // 模拟网络请求结果
                String response = String.format("响应来自 %s，延迟 %dms", url, actualDelay);
                log.info("网络请求完成: {}", response);
                
                return response;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("网络请求被中断: {}", url);
                return "请求被中断";
            }
        }, asyncExecutor)
        .orTimeout(timeoutMs, TimeUnit.MILLISECONDS)
        .exceptionally(throwable -> {
            log.error("网络请求超时或异常: {}", url, throwable);
            return "请求超时或失败: " + throwable.getMessage();
        });
    }

    @Override
    public CompletableFuture<String> processFileAsync(String fileName, String content) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("开始异步处理文件: {}, 内容长度: {}, 线程: {}", 
                fileName, content.length(), Thread.currentThread().getName());
            
            try {
                // 模拟文件处理时间
                Thread.sleep(500 + content.length() * 2); // 根据内容长度计算处理时间
                
                // 模拟文件处理结果
                String processedContent = content.toUpperCase().replace(" ", "_");
                String result = String.format("文件 %s 处理完成，原始长度: %d，处理后长度: %d", 
                    fileName, content.length(), processedContent.length());
                
                log.info("文件处理完成: {}", fileName);
                return result;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("文件处理被中断: {}", fileName);
                return "文件处理被中断";
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<List<String>> simulateDatabaseQueryAsync(String query) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("开始模拟数据库查询: {}, 线程: {}", query, Thread.currentThread().getName());
            
            try {
                // 模拟数据库查询时间
                Thread.sleep(300 + (int)(Math.random() * 500));
                
                // 模拟查询结果
                List<String> results = Arrays.asList(
                    "记录1: " + query + "_result_1",
                    "记录2: " + query + "_result_2",
                    "记录3: " + query + "_result_3"
                );
                
                log.info("数据库查询完成，返回 {} 条记录", results.size());
                return results;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("数据库查询被中断: {}", query);
                return Arrays.asList("查询被中断");
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<Boolean> sendEmailAsync(String to, String subject, String content) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("开始异步发送邮件，收件人: {}, 主题: {}, 线程: {}", 
                to, subject, Thread.currentThread().getName());
            
            try {
                // 模拟邮件发送时间
                Thread.sleep(800 + (int)(Math.random() * 400));
                
                // 模拟发送成功率（90%）
                boolean success = Math.random() > 0.1;
                
                if (success) {
                    log.info("邮件发送成功，收件人: {}", to);
                } else {
                    log.warn("邮件发送失败，收件人: {}", to);
                }
                
                return success;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("邮件发送被中断，收件人: {}", to);
                return false;
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<Boolean> setCacheAsync(String key, String value, int expireSeconds) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("开始异步设置缓存，键: {}, 过期时间: {}秒, 线程: {}", 
                key, expireSeconds, Thread.currentThread().getName());
            
            try {
                // 模拟缓存操作时间
                Thread.sleep(50 + (int)(Math.random() * 100));
                
                // 设置缓存
                cache.put(key, new CacheItem(value, expireSeconds));
                
                log.info("缓存设置成功，键: {}", key);
                return true;
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("缓存设置被中断，键: {}", key);
                return false;
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<String> getCacheAsync(String key) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("开始异步获取缓存，键: {}, 线程: {}", key, Thread.currentThread().getName());
            
            try {
                // 模拟缓存查询时间
                Thread.sleep(30 + (int)(Math.random() * 70));
                
                CacheItem item = cache.get(key);
                if (item == null) {
                    log.info("缓存未找到，键: {}", key);
                    return null;
                }
                
                if (item.isExpired()) {
                    cache.remove(key);
                    log.info("缓存已过期，键: {}", key);
                    return null;
                }
                
                log.info("缓存获取成功，键: {}", key);
                return item.getValue();
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("缓存获取被中断，键: {}", key);
                return null;
            }
        }, asyncExecutor);
    }

    @Override
    public CompletableFuture<String> compositeTaskAsync(String id) {
        log.info("开始执行组合异步任务，ID: {}", id);
        
        return simulateDatabaseQueryAsync("SELECT * FROM data WHERE id = " + id)
            .thenCompose(queryResults -> {
                log.info("查询完成，开始处理数据，ID: {}", id);
                return processFileAsync("data_" + id + ".txt", String.join(",", queryResults));
            })
            .thenCompose(processResult -> {
                log.info("数据处理完成，开始保存到缓存，ID: {}", id);
                return setCacheAsync("processed_" + id, processResult, 3600)
                    .thenApply(cacheResult -> {
                        if (cacheResult) {
                            return "组合任务完成：" + processResult;
                        } else {
                            return "组合任务部分完成（缓存失败）：" + processResult;
                        }
                    });
            })
            .whenComplete((result, throwable) -> {
                if (throwable != null) {
                    log.error("组合任务执行异常，ID: {}", id, throwable);
                } else {
                    log.info("组合任务执行完成，ID: {}, 结果: {}", id, result);
                }
            });
    }

    /**
     * 关闭线程池
     */
    public void shutdown() {
        if (asyncExecutor != null && !asyncExecutor.isShutdown()) {
            asyncExecutor.shutdown();
            try {
                if (!asyncExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    asyncExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                asyncExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}