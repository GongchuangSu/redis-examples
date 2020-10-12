package com.gongchuangsu.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gongchuangsu.pubsub.sub.TopicMessageListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import redis.clients.jedis.JedisPoolConfig;

import java.text.SimpleDateFormat;
import java.time.Duration;

/**
 * Redis配置
 * @author sugongchuang
 * @date 2020.10.05
 */
@Configuration
public class JedisRedisConfig {
	
	private static final String DEFAULT_TOPIC = "topic";
	
	@Value("${spring.redis.database}")
	private int database;
	@Value("${spring.redis.host}")
	private String host;
	@Value("${spring.redis.port}")
	private int port;
	@Value("${spring.redis.password}")
	private String password;
	@Value("${spring.redis.timeout}")
	private int timeout;
	@Value("${spring.redis.jedis.pool.max-active}")
	private int maxActive;
	@Value("${spring.redis.jedis.pool.max-wait}")
	private long maxWaitMillis;
	@Value("${spring.redis.jedis.pool.max-idle}")
	private int maxIdle;
	@Value("${spring.redis.jedis.pool.min-idle}")
	private int minIdle;
	
	/**
	 * 连接池配置信息
	 * @return JedisPoolConfig
	 */
	@Bean
	public JedisPoolConfig jedisPoolConfig(){
		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
		// 最大连接数
		jedisPoolConfig.setMaxTotal(maxActive);
		// 当池内没有可用连接时，最大等待时间
		jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
		// 最大空闲连接数
		jedisPoolConfig.setMinIdle(maxIdle);
		// 最小空闲连接数
		jedisPoolConfig.setMinIdle(minIdle);
		// 其他属性可以自行添加
		return jedisPoolConfig;
	}
	
	/**
	 * Jedis连接工厂
	 * @param jedisPoolConfig 连接池配置信息
	 * @return JedisConnectionFactory
	 */
	@Bean
	public JedisConnectionFactory jedisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
		JedisClientConfiguration jedisClientConfiguration = JedisClientConfiguration.builder().usePooling()
				                                                    .poolConfig(jedisPoolConfig).and().readTimeout(Duration.ofMillis(timeout)).build();
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName(host);
		redisStandaloneConfiguration.setPort(port);
		redisStandaloneConfiguration.setPassword(RedisPassword.of(password));
		return new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
	}
	
	/**
	 * ObjectMapper配置
	 * @return ObjectMapper
	 */
	@Bean
	public ObjectMapper objectMapper(){
		return new Jackson2ObjectMapperBuilder().build()
				       .enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL)
				       .setDateFormat(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"));
	}
	
	/**
	 * GenericJackson2JsonRedisSerializer配置
	 * @param objectMapper ObjectMapper
	 * @return GenericJackson2JsonRedisSerializer
	 */
	@Bean
	public GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer(ObjectMapper objectMapper){
		return new GenericJackson2JsonRedisSerializer(objectMapper);
	}
	
	/**
	 * redisTemplate
	 * @return RedisTemplate
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory, GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer) {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(genericJackson2JsonRedisSerializer);
		redisTemplate.setConnectionFactory(jedisConnectionFactory);
		return redisTemplate;
	}
	
	@Bean
	TopicMessageListener topicMessageListener(GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer){
		return new TopicMessageListener(genericJackson2JsonRedisSerializer);
	}
	
	@Bean
	MessageListenerAdapter listenerAdapter(TopicMessageListener topicMessageListener) {
		return new MessageListenerAdapter(topicMessageListener, "onMessage");
	}
	
	/**
	 * 消息监听容器
	 * @param jedisConnectionFactory 连接工厂
	 * @param listenerAdapter 消息监听适配器
	 * @return RedisMessageListenerContainer
	 */
	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer(JedisConnectionFactory jedisConnectionFactory,
	                                                                   MessageListenerAdapter listenerAdapter){
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(jedisConnectionFactory);
		container.addMessageListener(listenerAdapter, new PatternTopic(DEFAULT_TOPIC));
		return container;
	}
}
