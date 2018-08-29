package com.ddv.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.CronTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

@Configuration
public class ScheduledTaskConfiguration implements SchedulingConfigurer {

	@Autowired
	private CheckTask checkTask;
	
	@Value("${schedule.cron}")
	private String cronValue;
	
	@Override
	public void configureTasks(ScheduledTaskRegistrar aRegistrar) {
		aRegistrar.scheduleCronTask(new CronTask(checkTask, cronValue));
	}
	
}
