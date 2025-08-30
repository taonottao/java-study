package com.example.treaconcurrent.basics;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 线程基础概念演示类
 * 演示Thread和Runnable的基本用法，包括线程创建、启动、等待等核心概念
 * 
 * @author SOLO Coding
 * @since 2024-01-20
 */
@Slf4j
public class ThreadBasicsDemo {

    public static void main(String[] args) {
        log.info("=== 线程基础概念演示开始 ===");
        
        // 1. 演示继承Thread类的方式创建线程
        demonstrateThreadClass();
        
        // 2. 演示实现Runnable接口的方式创建线程
        demonstrateRunnableInterface();
        
        // 3. 演示Lambda表达式创建线程
        demonstrateLambdaThread();
        
        // 4. 演示线程的基本操作
        demonstrateThreadOperations();
        
        log.info("=== 线程基础概念演示结束 ===");
    }

    /**
     * 演示基本的线程创建方式
     * 包括继承Thread类、实现Runnable接口和Lambda表达式三种方式
     */
    public static void demonstrateBasicThreadCreation() {
        log.info("\n=== 基本线程创建方式演示 ===");
        
        // 1. 演示继承Thread类的方式创建线程
        demonstrateThreadClass();
        
        // 2. 演示实现Runnable接口的方式创建线程
        demonstrateRunnableInterface();
        
        // 3. 演示Lambda表达式创建线程
        demonstrateLambdaThread();
        
        log.info("=== 基本线程创建方式演示结束 ===");
    }

    /**
     * 演示继承Thread类的方式创建线程
     * 优点：代码简单，直接继承Thread类
     * 缺点：Java单继承限制，无法继承其他类
     */
    private static void demonstrateThreadClass() {
        log.info("\n--- 1. 继承Thread类创建线程 ---");
        
        // 创建自定义线程类
        class MyThread extends Thread {
            private final String threadName;
            
            public MyThread(String threadName) {
                this.threadName = threadName;
                // 设置线程名称
                setName(threadName);
            }
            
            @Override
            public void run() {
                for (int i = 1; i <= 3; i++) {
                    log.info("线程 [{}] 正在执行第 {} 次任务，当前时间：{}", 
                            threadName, i, DateUtil.now());
                    // 模拟任务执行时间
                    ThreadUtil.sleep(1000);
                }
                log.info("线程 [{}] 执行完成", threadName);
            }
        }
        
        // 创建并启动线程
        MyThread thread1 = new MyThread("Thread-A");
        MyThread thread2 = new MyThread("Thread-B");
        
        thread1.start();
        thread2.start();
        
        // 等待线程执行完成
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            log.error("线程等待被中断", e);
        }
    }

    /**
     * 演示实现Runnable接口的方式创建线程
     * 优点：避免单继承限制，代码更灵活
     * 缺点：需要额外创建Thread对象
     */
    public static void demonstrateRunnableInterface() {
        log.info("\n--- 2. 实现Runnable接口创建线程 ---");
        
        // 创建实现Runnable接口的任务类
        class MyTask implements Runnable {
            private final String taskName;
            
            public MyTask(String taskName) {
                this.taskName = taskName;
            }
            
            @Override
            public void run() {
                for (int i = 1; i <= 3; i++) {
                    log.info("任务 [{}] 正在执行第 {} 次操作，线程名：{}，当前时间：{}", 
                            taskName, i, Thread.currentThread().getName(), DateUtil.now());
                    ThreadUtil.sleep(800);
                }
                log.info("任务 [{}] 执行完成", taskName);
            }
        }
        
        // 创建任务实例
        MyTask task1 = new MyTask("Task-X");
        MyTask task2 = new MyTask("Task-Y");
        
        // 创建线程并启动
        Thread thread1 = new Thread(task1, "Worker-1");
        Thread thread2 = new Thread(task2, "Worker-2");
        
        thread1.start();
        thread2.start();
        
        // 等待线程执行完成
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            log.error("线程等待被中断", e);
        }
    }

    /**
     * 演示使用Lambda表达式创建线程
     * 优点：代码简洁，适合简单任务
     * 缺点：不适合复杂逻辑
     */
    public static void demonstrateLambdaThread() {
        log.info("\n--- 3. Lambda表达式创建线程 ---");
        
        // 使用Lambda表达式创建线程
        Thread lambdaThread1 = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                log.info("Lambda线程-1 执行第 {} 次，线程ID：{}，当前时间：{}", 
                        i, Thread.currentThread().getId(), DateUtil.now());
                ThreadUtil.sleep(600);
            }
        }, "Lambda-Thread-1");
        
        Thread lambdaThread2 = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                log.info("Lambda线程-2 执行第 {} 次，线程状态：{}，当前时间：{}", 
                        i, Thread.currentThread().getState(), DateUtil.now());
                ThreadUtil.sleep(600);
            }
        }, "Lambda-Thread-2");
        
        lambdaThread1.start();
        lambdaThread2.start();
        
        // 等待线程执行完成
        try {
            lambdaThread1.join();
            lambdaThread2.join();
        } catch (InterruptedException e) {
            log.error("线程等待被中断", e);
        }
    }

    /**
     * 演示线程的基本操作和属性
     * 包括线程优先级、守护线程、线程状态等
     */
    public static void demonstrateThreadOperations() {
        log.info("\n--- 4. 线程基本操作演示 ---");
        
        // 获取当前线程信息
        Thread currentThread = Thread.currentThread();
        log.info("当前线程名称：{}", currentThread.getName());
        log.info("当前线程ID：{}", currentThread.getId());
        log.info("当前线程优先级：{}", currentThread.getPriority());
        log.info("当前线程是否为守护线程：{}", currentThread.isDaemon());
        
        // 创建不同优先级的线程
        Thread highPriorityThread = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                log.info("高优先级线程执行第 {} 次", i);
                ThreadUtil.sleep(200);
            }
        }, "High-Priority-Thread");
        
        Thread lowPriorityThread = new Thread(() -> {
            for (int i = 1; i <= 5; i++) {
                log.info("低优先级线程执行第 {} 次", i);
                ThreadUtil.sleep(200);
            }
        }, "Low-Priority-Thread");
        
        // 设置线程优先级
        highPriorityThread.setPriority(Thread.MAX_PRIORITY);
        lowPriorityThread.setPriority(Thread.MIN_PRIORITY);
        
        log.info("启动不同优先级的线程...");
        highPriorityThread.start();
        lowPriorityThread.start();
        
        // 演示守护线程
        Thread daemonThread = new Thread(() -> {
            int count = 0;
            while (true) {
                count++;
                log.info("守护线程运行中... 计数：{}", count);
                ThreadUtil.sleep(1000);
                // 守护线程会在主线程结束时自动结束
                if (count >= 3) {
                    break;
                }
            }
        }, "Daemon-Thread");
        
        // 设置为守护线程
        daemonThread.setDaemon(true);
        daemonThread.start();
        
        // 等待非守护线程完成
        try {
            highPriorityThread.join();
            lowPriorityThread.join();
        } catch (InterruptedException e) {
            log.error("线程等待被中断", e);
        }
        
        log.info("主线程即将结束，守护线程也会随之结束");
    }
    
    /**
     * 演示线程状态
     */
    public void demonstrateThreadStates() {
        log.info("\n--- 演示线程状态 ---");
        
        Thread thread = new Thread(() -> {
            log.info("线程开始执行");
            ThreadUtil.sleep(2000);
            log.info("线程执行完成");
        }, "StateDemo-Thread");
        
        log.info("线程创建后状态: {}", thread.getState());
        
        thread.start();
        log.info("线程启动后状态: {}", thread.getState());
        
        ThreadUtil.sleep(500);
        log.info("线程运行中状态: {}", thread.getState());
        
        try {
            thread.join();
            log.info("线程结束后状态: {}", thread.getState());
        } catch (InterruptedException e) {
            log.error("线程等待被中断", e);
        }
    }
    
    /**
     * 演示线程优先级
     */
    public void demonstrateThreadPriority() {
        log.info("\n--- 演示线程优先级 ---");
        
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                log.info("高优先级线程执行: {}", i);
                ThreadUtil.sleep(100);
            }
        }, "HighPriority");
        
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                log.info("低优先级线程执行: {}", i);
                ThreadUtil.sleep(100);
            }
        }, "LowPriority");
        
        thread1.setPriority(Thread.MAX_PRIORITY);
        thread2.setPriority(Thread.MIN_PRIORITY);
        
        log.info("线程1优先级: {}", thread1.getPriority());
        log.info("线程2优先级: {}", thread2.getPriority());
        
        thread1.start();
        thread2.start();
        
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            log.error("线程等待被中断", e);
        }
    }
    
    /**
     * 演示守护线程
     */
    public void demonstrateDaemonThread() {
        log.info("\n--- 演示守护线程 ---");
        
        Thread userThread = new Thread(() -> {
            for (int i = 0; i < 3; i++) {
                log.info("用户线程执行: {}", i);
                ThreadUtil.sleep(1000);
            }
        }, "UserThread");
        
        Thread daemonThread = new Thread(() -> {
            int count = 0;
            while (true) {
                log.info("守护线程执行: {}", count++);
                ThreadUtil.sleep(500);
            }
        }, "DaemonThread");
        
        daemonThread.setDaemon(true);
        
        log.info("用户线程是否为守护线程: {}", userThread.isDaemon());
        log.info("守护线程是否为守护线程: {}", daemonThread.isDaemon());
        
        userThread.start();
        daemonThread.start();
        
        try {
            userThread.join();
        } catch (InterruptedException e) {
            log.error("线程等待被中断", e);
        }
        
        log.info("用户线程结束，守护线程也会结束");
    }
    
    /**
     * 演示线程中断
     */
    public void demonstrateThreadInterrupt() {
        log.info("\n--- 演示线程中断 ---");
        
        Thread thread = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    if (Thread.currentThread().isInterrupted()) {
                        log.info("线程被中断，退出循环");
                        break;
                    }
                    log.info("线程执行: {}", i);
                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                log.info("线程在sleep时被中断");
                Thread.currentThread().interrupt(); // 重新设置中断标志
            }
        }, "InterruptDemo");
        
        thread.start();
        
        ThreadUtil.sleep(2000);
        log.info("中断线程");
        thread.interrupt();
        
        try {
            thread.join();
        } catch (InterruptedException e) {
            log.error("线程等待被中断", e);
        }
    }
    
    /**
     * 演示线程同步
     */
    public void demonstrateThreadSynchronization() {
        log.info("\n--- 演示线程同步 ---");
        
        class Counter {
            private int count = 0;
            
            public synchronized void increment() {
                count++;
            }
            
            public synchronized int getCount() {
                return count;
            }
        }
        
        Counter counter = new Counter();
        
        Thread thread1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        }, "Counter1");
        
        Thread thread2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment();
            }
        }, "Counter2");
        
        thread1.start();
        thread2.start();
        
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            log.error("线程等待被中断", e);
        }
        
        log.info("最终计数值: {}", counter.getCount());
    }
    
    /**
     * 运行所有演示
     */
    public void runAllDemonstrations() {
        log.info("=== 开始运行所有线程基础演示 ===");
        
        demonstrateThreadStates();
        ThreadUtil.sleep(1000);
        
        demonstrateThreadPriority();
        ThreadUtil.sleep(1000);
        
        demonstrateDaemonThread();
        ThreadUtil.sleep(1000);
        
        demonstrateThreadInterrupt();
        ThreadUtil.sleep(1000);
        
        demonstrateThreadSynchronization();
        
        log.info("=== 所有线程基础演示完成 ===");
    }
}