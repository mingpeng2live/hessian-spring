/**
 * 
 */
package com.jsonfilter;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.ReflectionUtils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter.SerializeExceptFilter;

/**
 * @author admaster
 *
 */
public class FiledSerializeExceptFilter extends SerializeExceptFilter {

	private static final long serialVersionUID = 1L;
	
	/** 属性键值 */
	private final Map<String, Object> map = new HashMap<String, Object>();

	public FiledSerializeExceptFilter(Set<String> properties) {
		super(properties);
		map.put("name", "2");
	}
	
    @Override
    public void serializeAsField(Object pojo, JsonGenerator jgen,
            SerializerProvider provider, PropertyWriter writer) throws Exception {
    	// 加入判断处理 主要是根据字段满足对应的条件来过滤相应的字段
    	boolean flag = include(writer);
    	
    	// 当前字段如果是需要过滤字段
    	if (!flag) {
    		if (Map.class.isAssignableFrom(pojo.getClass())) {
    			
    		} else if (List.class.isAssignableFrom(pojo.getClass())) {
     			
     		} else {
     			Field findField = ReflectionUtils.findField(pojo.getClass(), "name");
     			ReflectionUtils.makeAccessible(findField);
     			Object field = ReflectionUtils.getField(findField, pojo);
     			if (field != null) {
     				flag = field.equals(map.get("name"));
     			}
     		}
    	}
    	
        if (flag) {
        	writer.serializeAsField(pojo, jgen, provider);
        } else if (!jgen.canOmitFields()) { // since 2.3
            writer.serializeAsOmittedField(pojo, jgen, provider);
        }
    }

    public static SimpleBeanPropertyFilter serializeAllExcept(String... propertyArray) {
        HashSet<String> properties = new HashSet<String>(propertyArray.length);
        Collections.addAll(properties, propertyArray);
        return new FiledSerializeExceptFilter(properties);
    }
	
	
}
