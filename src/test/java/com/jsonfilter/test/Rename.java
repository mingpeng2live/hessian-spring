package com.jsonfilter.test;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.jsonfilter.IRename;

/**
 * @author pengming
 * @Description 
 *
 * @Date  2016年7月27日 下午4:20:38
 */
public class Rename implements IRename {

	private static Map<String, String> propertyMapping = new HashMap<String, String>();
	
	static {
		propertyMapping.put("name", "propertyUserName");
	}
	
	@Override
	public String getNewName(SerializerProvider prov,
			BeanPropertyDefinition propDef, BeanDescription beanDesc,
			String oldName) {
		return MapUtils.getString(propertyMapping, oldName, oldName);
	}

}
