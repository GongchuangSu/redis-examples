package com.gongchuangsu.pubsub.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * @author sugongchuang
 * @date 2020.10.10
 */
public class MsgBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String content;
	
	private Date createTime;
	
	public MsgBean (){}
	
	public MsgBean (String content, Date createTime) {
		this.content = content;
		this.createTime = createTime;
	}
	
	public String getContent () {
		return content;
	}
	
	public void setContent (String content) {
		this.content = content;
	}
	
	public Date getCreateTime () {
		return createTime;
	}
	
	public void setCreateTime (Date createTime) {
		this.createTime = createTime;
	}
	
	@Override
	public String toString () {
		return "Message{" +
				       "content='" + content + '\'' +
				       ", createTime=" + createTime +
				       '}';
	}
}
