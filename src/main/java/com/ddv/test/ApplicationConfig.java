package com.ddv.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class ApplicationConfig {

	@Autowired
	private ApplicationArguments appArgs;
	@Autowired
	private Environment environment;
	@Autowired
	private ResourceIdConfiguration resourceIdConfig;
	
	@Bean
	public CheckTask createCheckTask() {
		String[] args = appArgs.getSourceArgs();
		String userName = args[0];
		String userPassword = args[1];
		
		return new CheckTask(userName, userPassword, resourceIdConfig.getList(), environment);
	}
	
}
