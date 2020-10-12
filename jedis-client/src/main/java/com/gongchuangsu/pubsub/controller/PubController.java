package com.gongchuangsu.pubsub.controller;

import com.gongchuangsu.pubsub.bean.MsgBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sugongchuang
 * @date 2020.10.05
 */
@RestController
@RequestMapping(value = "/pub")
public class PubController {

	@Autowired
	RedisTemplate<String, Object> redisTemplate;
	
	private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger();
	
	@GetMapping(value = "/message")
	public void sendMessage(){
		MsgBean msgBean = new MsgBean("消息" + ATOMIC_INTEGER.incrementAndGet(), new Date());
		redisTemplate.convertAndSend("topic", msgBean);
	}

}
