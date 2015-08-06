package com.jsonfilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.StringMemberValue;

import com.fasterxml.jackson.annotation.JsonFilter;

/**
 * 产生注解接口的实体
 * 
 * @author rocca.peng@hunteron.com
 * @Description
 * @Date 2015年8月5日 下午3:54:09
 */
public class ClassUtil {

	private String filterName;
	private Class<?> classObj;
	
	private ClassUtil(){
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public Class<?> getClassObj() {
		return classObj;
	}

	public void setClassObj(Class<?> classObj) {
		this.classObj = classObj;
	}
	
	private static final Logger logger = LoggerFactory.getLogger(ClassUtil.class);

	public static ClassUtil getAnnotationInstance() {
        ClassPool pool = ClassPool.getDefault();  
        // 创建代理接口  
        long currentTimeMillis = System.currentTimeMillis();
		String filterName = "filter" + currentTimeMillis;
		CtClass cc = pool.makeInterface("ProxyMixInAnnotation" + currentTimeMillis);  
        ClassFile ccFile = cc.getClassFile();  
        ConstPool constpool = ccFile.getConstPool();  
        // create the annotation  
        AnnotationsAttribute attr = new AnnotationsAttribute(constpool, AnnotationsAttribute.visibleTag);  
        // 创建JsonFilter注解  
        Annotation jsonFilter = new Annotation(JsonFilter.class.getName(), constpool);  
  
        StringMemberValue memberValue = new StringMemberValue(constpool);// 将name值设入注解内  
        memberValue.setValue(filterName);  
        jsonFilter.addMemberValue("value", memberValue);  
        attr.addAnnotation(jsonFilter);  
        ccFile.addAttribute(attr);  
        try {
        	ClassUtil cu = new ClassUtil();
        	cu.setClassObj(cc.toClass());
        	cu.setFilterName(filterName);
			return cu;
		} catch (CannotCompileException e) {
			logger.error("创建过滤器类失败", e);
		} 
        return null;
	}
	
}
