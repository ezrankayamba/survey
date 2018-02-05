package tz.co.nezatech.apps.survey.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
	@Autowired
	ViewLayoutBasedHandler hbsHandler;
	

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(hbsHandler);
	}
}
