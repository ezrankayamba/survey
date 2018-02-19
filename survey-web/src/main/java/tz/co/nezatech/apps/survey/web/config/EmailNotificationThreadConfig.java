package tz.co.nezatech.apps.survey.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class EmailNotificationThreadConfig {
	@Bean(name = "emailNotificationTaskExecutor")
    public TaskExecutor specificTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.initialize();
        executor.setMaxPoolSize(10);
        return executor;
    }
}
