package com.example.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@Slf4j
public class ExecutorConfig {

  /** 核心线程数 */
  private int corePoolSize = 0;
  /** 最大线程数 */
  private int maxPoolSize = 1;
  /** 队列数 */
  private int queueCapacity = Integer.MAX_VALUE;

  private final int keepAliveSeconds = 60;

  @Bean(name = "threadPoolTaskExecutor")
  public Executor threadPoolTaskExecutor(){
    log.info("创建线程池...");
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    //设置核心线程数
    executor.setCorePoolSize(corePoolSize);
    //设置最大线程数
    executor.setMaxPoolSize(maxPoolSize);
    //设置空闲时间
    executor.setKeepAliveSeconds(keepAliveSeconds);
    //设置队列数
    executor.setQueueCapacity(queueCapacity);
    //设置核心线程过期
    //executor.setAllowCoreThreadTimeOut(true);
    //设置线程名称前缀
    executor.setThreadNamePrefix("threadPoolTaskExecutor->>>");

    // rejection-policy：当pool已经达到max size的时候，如何处理新任务
    // CALLER_RUNS：不在新线程中执行任务，而是有调用者所在的线程来执行
    // 设置拒绝策略.当工作队列已满,线程数为最大线程数的时候,接收新任务抛出RejectedExecutionException异常
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
    // 执行初始化
    executor.initialize();
    return executor;
  }

}
