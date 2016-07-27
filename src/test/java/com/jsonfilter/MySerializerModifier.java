package com.jsonfilter;

import java.util.List;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;

public class MySerializerModifier extends BeanSerializerModifier {
	
	@Override
	public JsonSerializer<?> modifySerializer(SerializationConfig config,
			BeanDescription beanDesc, JsonSerializer<?> serializer) {
//		if (serializer instanceof BeanSerializerBase) {
//			serializer = new MyBeanSerializer((BeanSerializerBase)serializer);
//		}
		return serializer;
	}
	
	@Override
	public List<BeanPropertyWriter> changeProperties(
			SerializationConfig config, BeanDescription beanDesc,
			List<BeanPropertyWriter> beanProperties) {
//		List<BeanPropertyWriter> vbeanProperties = new ArrayList<BeanPropertyWriter>(beanProperties);
//		beanProperties.clear();
//		for (BeanPropertyWriter beanPropertyWriter : vbeanProperties) {
//			beanPropertyWriter.rename(NameTransformer.NOP);
//			beanProperties.add(new MyBeanPropertyWriter(beanPropertyWriter));
//		}
		return beanProperties;
	}
}