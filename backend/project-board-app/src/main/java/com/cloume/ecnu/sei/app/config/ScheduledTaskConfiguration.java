package com.cloume.ecnu.sei.app.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

@Log4j2
@Component
public class ScheduledTaskConfiguration implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        final ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setThreadNamePrefix("sca-scheduler-");
        taskScheduler.setPoolSize(30);
        taskScheduler.setErrorHandler(new ErrorHandler() {
            @Override
            public void handleError(Throwable throwable) {
                log.error("ScheduledTaskConfiguration error: {}", throwable);
            }
        });
        taskScheduler.setAwaitTerminationSeconds(900);
        taskScheduler.initialize();
        scheduledTaskRegistrar.setTaskScheduler(taskScheduler);
    }
}
