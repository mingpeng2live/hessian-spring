package com.jsonfilter;

import java.io.IOException;
import java.io.Serializable;

import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
  
/**
 * 重写spring mvc 的json化过程
 * 
 * @author rocca.peng@hunteron.com
 * @Description 
 * @Date  2015年8月6日 上午9:41:30
 */
public class Jackson2HttpMessageConverter extends MappingJackson2HttpMessageConverter {  
  
    @Override  
    protected void writeInternal(Object object, HttpOutputMessage outputMessage)  
            throws IOException, HttpMessageNotWritableException {  
//    	HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
//    	String pathInfo = request.getPathInfo();
//    	// TODO: 根据当前用户判断是否有需要过滤的字段
//    	System.out.println(pathInfo);

    	
        // 判断是否需要重写objectMapper  
        ObjectMapper objectMapper = this.objectMapper;// 本地化ObjectMapper，防止方法级别的ObjectMapper改变全局ObjectMapper  
//        if (ThreadJacksonMixInHolder.isContainsMixIn()) { // 判断当前请求是否需要过滤, 
        	
        	ClassUtil cu1 = ClassUtil.getAnnotationInstance();
//    		ClassUtil cu2 = ClassUtil.getAnnotationInstance();
        	
        	objectMapper = Jacksons.me()
        			.addMixInAnnotations(Serializable.class, cu1.getClassObj())
//					.addMixInAnnotations(Map.class, cu2.getClassObj())
        			.filter(cu1.getFilterName(), "contractNo")
//					.filter(cu2.getFilterName(), "d")
        			.getObjectMapper();
//        }  
  
        JsonEncoding encoding = getJsonEncoding(outputMessage.getHeaders().getContentType());  
        JsonGenerator generator = objectMapper.getFactory().createGenerator(outputMessage.getBody(), encoding);  
  
        writePrefix(generator, object);
		Class<?> serializationView = null;
		Object value = object;
		if (value instanceof MappingJacksonValue) {
			MappingJacksonValue container = (MappingJacksonValue) object;
			value = container.getValue();
			serializationView = container.getSerializationView();
		}
		if (serializationView != null) {
			objectMapper.writerWithView(serializationView).writeValue(generator, value);
		}
		else {
			objectMapper.writeValue(generator, value);
		}
		writeSuffix(generator, object);
		generator.flush();
        
    }

    public void setPrefixJson(boolean prefixJson) {  
        super.setPrefixJson(prefixJson);
    }  
  
}  