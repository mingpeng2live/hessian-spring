package com.jsonfilter;

import java.text.DateFormat;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

/**
 * Jackson2 json化帮助类
 * 
 * @author rocca.peng@hunteron.com
 * @Description 
 * @Date  2015年8月6日 上午9:44:00
 */
public class Jacksons {
	private ObjectMapper objectMapper;
	private SimpleFilterProvider filterProvider;

	public static Jacksons me() {
		return new Jacksons();
	}
	
	public ObjectMapper getObjectMapper(){
		return objectMapper;
	}

	private Jacksons() {
		objectMapper = new ObjectMapper();
		// 忽略json串中存在的属性
//		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		
		filterProvider = new SimpleFilterProvider();
	}

	public Jacksons filter(String filterName, String... properties) {
		FilterProvider filter = objectMapper.getSerializationConfig().getFilterProvider();
		if (filter == null) {
			objectMapper.setFilters(filterProvider);
		}
		filterProvider.addFilter(filterName, SimpleBeanPropertyFilter.serializeAllExcept(properties));
		return this;
	}

	public Jacksons setDateFormate(DateFormat dateFormat) {
		objectMapper.setDateFormat(dateFormat);
		return this;
	}

	public Jacksons addMixInAnnotations(Class<?> target, Class<?> mixinSource) {  
        objectMapper.addMixInAnnotations(target, mixinSource);  
        return this;  
    }  

	public <T> T json2Obj(String json, Class<T> clazz) {
		try {
			return objectMapper.readValue(json, clazz);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("解析json错误");
		}
	}

	public String readAsString(Object obj) {
		try {
			return objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("解析对象错误");
		}
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> json2List(String json) {
		try {
			return objectMapper.readValue(json, List.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("解析json错误");
		}
	}
}