package itqGroupTestApp.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "submitExecutor")
    public Executor submitExecutor() {
        ThreadPoolTaskExecutor e = new ThreadPoolTaskExecutor();
        e.setCorePoolSize(2);
        e.setMaxPoolSize(4);
        e.setQueueCapacity(10);
        e.setThreadNamePrefix("submit-");
        e.initialize();
        return e;
    }

    @Bean(name = "approveExecutor")
    public Executor approveExecutor() {
        ThreadPoolTaskExecutor e = new ThreadPoolTaskExecutor();
        e.setCorePoolSize(2);
        e.setMaxPoolSize(4);
        e.setQueueCapacity(10);
        e.setThreadNamePrefix("approve-");
        e.initialize();
        return e;
    }
}