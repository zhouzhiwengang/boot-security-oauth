package com.zzg.security.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

import com.zzg.security.user.CustomerUserService;

import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 * @ClassName: OAuth2ServerConfig
 * @Description: 授权服务器配置
 * @author: 世纪伟图 -zzg
 * @date: 2019年4月30日 上午11:37:50
 * 
 * @Copyright: 2019 www.digipower.cn 注意：本内容仅限于深圳市世纪伟图科技开发有限公司内部使用，禁止用于其他的商业目的
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2ServerConfig extends AuthorizationServerConfigurerAdapter {
	// 数据库连接
	@Autowired
	private DataSource dataSource;

	// redis 连接工厂
	
	 @Bean
	 public RedisConnectionFactory redisConnectionFactory() {
	        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
	        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
	        jedisConnectionFactory.setHostName("192.168.1.73");
	        jedisConnectionFactory.setPort(Integer.parseInt("6379"));
	        jedisConnectionFactory.setPassword("abc123");
	        jedisConnectionFactory.setDatabase(Integer.parseInt("0"));
	        jedisConnectionFactory.setUsePool(true);    
	        jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
	        return jedisConnectionFactory;
	    }


	// 用户信息
	@Autowired
	private CustomerUserService customerUserService;
//	@BeanCustomerUserService
//	UserDetailsService customUserService() {
//		return new CustomerUserService();
//	}

	// 授权管理器
	@Autowired
	private AuthenticationManager authenticationManager;

	/**
	 * 分配token存入redis
	 *
	 * @return
	 */
	@Bean
	public TokenStore tokenStore() {
		RedisTokenStore redisTokenStore = new RedisTokenStore(redisConnectionFactory());
		return redisTokenStore;
	}

	// 授权服务器配置
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		// TODO Auto-generated method stub
		endpoints
		.tokenStore(tokenStore())  // 授权存储类型
		.authenticationManager(authenticationManager)  //认证授权管理器
		.userDetailsService(customerUserService);  // 用户详情信息
	}
	
	/**
     * 配置oauth2_client_details数据库表元数据的细节
     *
     * @return
     */
    @Bean
    public ClientDetailsService clientDetails() {
        return new JdbcClientDetailsService(dataSource);
    }
    
    /**
     * 从数据库里获得具体的配置项(加载上面的方法)
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetails());
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security.
		        //允许所有资源服务器访问公钥端点（/oauth/token_key）
		        //只允许验证用户访问令牌解析端点（/oauth/check_token）
                tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()")
                // 允许客户端发送表单来进行权限认证来获取令牌
                .allowFormAuthenticationForClients();
    }
    
    // 设置授权服务器的密码格式
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
