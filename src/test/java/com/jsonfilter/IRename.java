package com.jsonfilter;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;

/**
 * 重命名属性名称接口 {link MyPropertyBuilder}
 * 该接口用于Jackson在分析构建属性序列化回写对象时就改变序列化输出的名称
 * 
 * @author pengming
 * @Description 
 *
 * @Date  2016年7月27日 下午3:08:28
 */
public interface IRename {

	/**
	 * 根据老名称获取要变更的新名称
	 * @param prov 
	 * @param propDef 当前属性
	 * @param beanDesc 当前bean
	 * @param oldName 老属性名称即propDef.getName()
	 * @return 新属性名用于序列化
	 */
	public String getNewName(SerializerProvider prov,
            BeanPropertyDefinition propDef,
            BeanDescription beanDesc, String oldName);
	
}
