package com.hunteron.hessian.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * hessian 注解用于自动扫描注入hessian接口到spring容器中
 * 
 * @author rocca.peng@hunteron.com
 * @Description 
 * @Date  2015年4月2日 下午9:49:16
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Hessian {

	/**
	 * 客户端调用服务端的contextPath
	 * @return
	 */
	String context();
	
	/**
	 * true 表示context是配置的key,告诉扫描器从配置中读取路径, false 表示context是路径
	 * @return
	 */
	boolean isKey() default true;
	
	/**
	 * 服务端中服务bean的名称，也代表访问的pathinfo路径
	 * @return
	 */
	String serverName();
	
	/**
	 * 表示客户端是否启用方法重载
	 * @return
	 */
	boolean overloadEnabled() default false;
	
	/**
	 * 请求头
	 * @return
	 */
	String[] headers() default {};
	
	/**
	 * 备注
	 * @return
	 */
	String description() default "";
	
}
