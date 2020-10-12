package com.gongchuangsu.pubsub.sub;

import com.gongchuangsu.pubsub.bean.MsgBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 * 消息监听器
 * @author sugongchuang
 * @date 2020.10.09
 */
public class TopicMessageListener implements MessageListener {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TopicMessageListener.class);
	
	private final GenericJackson2JsonRedisSerializer serializer;
	
	public TopicMessageListener(GenericJackson2JsonRedisSerializer serializer){
		this.serializer = serializer;
	}
	
	@Override
	public void onMessage (Message message, byte[] pattern) {
		MsgBean msgBean = (MsgBean) serializer.deserialize(message.getBody());
		String channel = new String(pattern);
		if(msgBean == null){
			LOGGER.info("Receive message is null!");
		}else{
			LOGGER.info("Receive Channel: {}, Message: {}", channel, msgBean.toString());
		}
	}
}
