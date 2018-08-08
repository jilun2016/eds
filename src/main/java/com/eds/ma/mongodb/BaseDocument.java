package com.eds.ma.mongodb;


import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;


/**
 * mongo基础document
 * @Author gaoyan
 * @Date: 2018/7/26
 */
public abstract class BaseDocument {

	@Id
	protected String id = new ObjectId().toString();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
