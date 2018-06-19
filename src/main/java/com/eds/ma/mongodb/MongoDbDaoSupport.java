package com.eds.ma.mongodb;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * mongodb封装工具类
 * @Author gaoyan
 * @Date: 2018/6/17
 */
@Repository
public class MongoDbDaoSupport{
	
	@Autowired
	protected MongoTemplate mongoTemplate;


	public Query createQuery() {
		return new Query();
	}

	public void save(Object object) {
		mongoTemplate.insert(object);
	}
	
	public <T> void batchSave(Collection<T> batchObjects,Class<T> tClass) {
		mongoTemplate.insert(batchObjects, tClass);
	}

	public <T> T get(String id,Class<T> tClass) {
		return mongoTemplate.findById(id, tClass);
	}
	
	public <T> T findOne(Query query,Class<T> tClass) {
		return mongoTemplate.findOne(query, tClass);
	}
	
	public <T> List<T> find(Query query,Class<T> tClass) {
		return mongoTemplate.find(query, tClass);
	}
}
