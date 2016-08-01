package com.jsonfilter;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.ser.BeanSerializerBuilder;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.PropertyBuilder;
import com.fasterxml.jackson.databind.ser.SerializerFactory;

/**
 * 自定义bean序列化工厂
 * @author pengming
 * @Description 
 *
 * @Date  2016年7月27日 下午4:09:20
 */
public class JacBeanSerializerFactory extends BeanSerializerFactory {

	private static final long serialVersionUID = 1L;
	
	private IRename rename;

	public JacBeanSerializerFactory(SerializerFactoryConfig config) {
		super(config);
		rename = new IRename() { // 默认实现返回老名称
			@Override
			public String getNewName(SerializerProvider prov,
					BeanPropertyDefinition propDef, BeanDescription beanDesc,
					String oldName) {
				return oldName;
			}
		};
	}

	public JacBeanSerializerFactory(SerializerFactoryConfig config, IRename rename) {
		super(config);
		this.rename = rename;
	}
	
	@Override
    protected PropertyBuilder constructPropertyBuilder(SerializationConfig config,
    		BeanDescription beanDesc) {
    	return new JacPropertyBuilder(config, beanDesc, rename);
    }
    
    @Override
    protected BeanSerializerBuilder constructBeanSerializerBuilder(
    		BeanDescription beanDesc) {
    	return new JacBeanSerializerBuilder(beanDesc);
    }
    
    @Override
    public SerializerFactory withConfig(SerializerFactoryConfig config)
    {
        if (_factoryConfig == config) {
            return this;
        }
        if (getClass() != JacBeanSerializerFactory.class) {
            throw new IllegalStateException("Subtype of BeanSerializerFactory ("+getClass().getName()
                    +") has not properly overridden method 'withAdditionalSerializers': can not instantiate subtype with "
                    +"additional serializer definitions");
        }
        return new JacBeanSerializerFactory(config);
    }

	public IRename getRename() {
		return rename;
	}

	public void setRename(IRename rename) {
		this.rename = rename;
	}
    
}