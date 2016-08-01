package com.jsonfilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.util.NameTransformer;

/**
 * 修改bean属性序列化时的名称
 * @author pengming
 * @Description 
 *
 * @Date  2016年7月28日 下午2:59:35
 */
public class JacSerializerModifier extends BeanSerializerModifier {
	
	private PropertyNameTransformer propertyNameTransformer = new PropertyNameTransformer();

	/**
	 * 变更Jackson封装bean属性对象
	 */
	@Override
	public List<BeanPropertyWriter> changeProperties(
			SerializationConfig config, BeanDescription beanDesc,
			List<BeanPropertyWriter> beanProperties) {
		List<BeanPropertyWriter> vbeanProperties = new ArrayList<BeanPropertyWriter>(beanProperties);
		beanProperties.clear();
		for (BeanPropertyWriter beanPropertyWriter : vbeanProperties) {
			// 修改属性名称
			BeanPropertyWriter v = beanPropertyWriter.rename(propertyNameTransformer);
			beanProperties.add(v);
		}
		return beanProperties;
	}
	

	private static Map<String, String> propertyMapping = new HashMap<String, String>();
	
	static {
		propertyMapping.put("name", "propertyUserName");
	}
	
	/**
	 * 属性名称转换类
	 * @author pengming
	 * @Description 
	 *
	 * @Date  2016年7月28日 下午3:03:40
	 */
	private static class PropertyNameTransformer extends NameTransformer {
		@Override
		public String transform(String name) { // 返回新属性名称
			return MapUtils.getString(propertyMapping, name, name);
		}

		@Override
		public String reverse(String transformed) {
			return transformed;
		}
	}
	
}