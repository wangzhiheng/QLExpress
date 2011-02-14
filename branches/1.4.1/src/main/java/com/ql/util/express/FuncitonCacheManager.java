package com.ql.util.express;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * ��������cache������Ҫ����֧�� cache isVIP("����");���ƵĲ���
 * 
 * @author xuannan
 * 
 */
public class FuncitonCacheManager {
	private static final Log log = LogFactory.getLog(FuncitonCacheManager.class);
	
	public Map<String, Object> functionCallCache = new HashMap<String, Object>();

	public static String genKey(String name, Object[] parameters) {
		StringBuilder builder = new StringBuilder();
		builder.append(name).append("(");
		if (parameters != null) {
			for (int i = 0; i < parameters.length; i++) {
				if (i > 0) {
					builder.append(",");
				}
				builder.append(parameters[i]);
			}
		}
		builder.append(")");
		return builder.toString();
	}

	public boolean containsKey(String key) {
		return functionCallCache.containsKey(key);
	}

	public Object get(String key) {
		if(log.isDebugEnabled()){
		    log.debug("get���ݵ������л�ȡ���ݣ�key =" + key);
		}
		return functionCallCache.get(key);
	}

	public void put(String key, Object value) {
		if(log.isDebugEnabled()){
		    log.debug("put���ݵ������л�ȡ���ݣ�key =" + key);
		}
		functionCallCache.put(key, value);
	}
	public void clearCache(){
		functionCallCache.clear();
	}
	public Map getCacheData(){
		return this.functionCallCache;
	}
}
 