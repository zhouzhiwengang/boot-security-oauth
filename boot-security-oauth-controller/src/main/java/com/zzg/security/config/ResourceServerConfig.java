package com.zzg.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
	
	/**
     * 跟数据库里配置的资源id一致
     */
    public static String RESOURCE_ID = "redis-token-test";
    
	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId(RESOURCE_ID).stateless(false);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
		.requestMatchers().antMatchers("/userInfo/**")
        .and()
		.authorizeRequests()
		.antMatchers("/userInfo/**")
		.authenticated(); // 配置必须认证才可访问路径
	}
	
	public static void main(String[] args) {
	    System.out.println(new org.apache.tomcat.util.codec.binary.Base64().encodeAsString("demoApp:123456".getBytes()));
	    System.out.println(java.util.Base64.getEncoder().encodeToString("demoApp:123456".getBytes()));
	}

}
