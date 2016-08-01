package com.jsonfilter;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerBuilder;

public class JacBeanSerializerBuilder extends BeanSerializerBuilder {

	protected final static BeanPropertyWriter[] NO_PROPERTIES = new BeanPropertyWriter[0];

	public JacBeanSerializerBuilder(BeanDescription beanDesc) {
		super(beanDesc);
	}

	public JacBeanSerializerBuilder(BeanSerializerBuilder src) {
		super(src);
	}
	
	public JsonSerializer<?> build()
    {
        BeanPropertyWriter[] properties;
        // No properties, any getter or object id writer?
        // No real serializer; caller gets to handle
        if (_properties == null || _properties.isEmpty()) {
            if (_anyGetter == null && _objectIdWriter == null) {
                return null;
            }
            properties = NO_PROPERTIES;
        } else {
            properties = _properties.toArray(new BeanPropertyWriter[_properties.size()]);
        }
        return new JacBeanSerializer(_beanDesc.getType(), this,
                properties, _filteredProperties);
    }
    
}