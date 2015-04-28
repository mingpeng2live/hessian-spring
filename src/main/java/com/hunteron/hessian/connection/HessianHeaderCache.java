package com.hunteron.hessian.connection;

import static org.springframework.util.Assert.notNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.caucho.hessian.client.HessianConnection;

/**
 * 请求头缓存
 * 
 * @author rocca.peng@hunteron.com
 * @Description 
 * @Date  2015年4月15日 下午4:08:48
 */
public class HessianHeaderCache {

	private static final Logger logger = LoggerFactory.getLogger(HessianHeaderCache.class);
	
	private static Map<String, Map<String, String>> headers = new ConcurrentHashMap<>();
	
	public static void addHeaderCache(String url, String... headerStrArr){
		notNull(url, "paramenter 'url' is required");
		Map<String, String> headerEntity = new HashMap<>(headerStrArr.length);
		for (String expression : headerStrArr) {
			int separator = expression.indexOf("=");
			String headKey = expression.substring(0, separator);
			String headValue = expression.substring(separator + 1);
			headerEntity.put(headKey, headValue);
		}
		logger.info(url + " header: " + headerEntity);
		headers.put(url, headerEntity);
	}
	
	public static void addHeader(HessianConnection open, String url){
		Map<String, String> headerEntity = headers.get(url);
		if (MapUtils.isNotEmpty(headerEntity)) {
			for (Map.Entry<String, String> entry : headerEntity.entrySet()) {
				open.addHeader(entry.getKey(), entry.getValue());
			}
		}
	}

}