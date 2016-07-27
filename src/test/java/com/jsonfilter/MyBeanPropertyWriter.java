package com.jsonfilter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.PropertySerializerMap;
import com.fasterxml.jackson.databind.util.Annotations;
import com.fasterxml.jackson.databind.util.NameTransformer;

/**
 * 重写bean属性对象，修改name可以重新设置值
 * @author pengming
 * @Description 
 *
 * @Date  2016年7月26日 下午3:37:17
 */
@JacksonStdImpl
public class MyBeanPropertyWriter extends BeanPropertyWriter {

	private static final long serialVersionUID = 1L;

	protected SerializedString _name;
	
	public MyBeanPropertyWriter() {
		super();
	}
	
	public MyBeanPropertyWriter(BeanPropertyWriter base) {
		super(base);
	}

	public MyBeanPropertyWriter(BeanPropertyWriter base, PropertyName name) {
		super(base, name);
	}

	public MyBeanPropertyWriter(BeanPropertyWriter base, SerializedString name) {
		super(base, name);
	}
	
	public MyBeanPropertyWriter(BeanPropertyDefinition propDef,
			AnnotatedMember member, Annotations contextAnnotations,
			JavaType declaredType, JsonSerializer<?> ser,
			TypeSerializer typeSer, JavaType serType, boolean suppressNulls,
			Object suppressableValue, String name) {
		super(propDef, member, contextAnnotations, declaredType, ser, typeSer, serType,
				suppressNulls, suppressableValue);
		_name = new SerializedString(name);
	}
	
    public BeanPropertyWriter rename(NameTransformer transformer) {
        String newName = transformer.transform(_name.getValue());
        if (newName.equals(_name.toString())) {
            return this;
        }
        return _new(PropertyName.construct(newName));
    }
	
    public BeanPropertyWriter _new(PropertyName newName) {
        return new MyBeanPropertyWriter(this, newName);
    }
    
    public void rename(String name) {
        if (!name.equals(_name.toString())) {
        	_name = new SerializedString(name);
        }
    }
	
    @Override 
    public String getName() { 
    	return _name.getValue();
    }

    @Override 
    public PropertyName getFullName() {
        return new PropertyName(_name.getValue());
    }
    
    @Override
    public SerializableString getSerializedName() { 
    	return _name; 
    }
    
    public boolean wouldConflictWithName(PropertyName name) {
        if (_wrapperName != null) {
            return _wrapperName.equals(name);
        }
        // Bit convoluted since our support for namespaces is spotty but:
        return name.hasSimpleName(_name.getValue()) && !name.hasNamespace();
    }
    
    @Override
    public void serializeAsField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception
    {
        // inlined 'get()'
        final Object value = (_accessorMethod == null) ? _field.get(bean) : _accessorMethod.invoke(bean);

        // Null handling is bit different, check that first
        if (value == null) {
            if (_nullSerializer != null) {
                gen.writeFieldName(_name);
                _nullSerializer.serialize(null, gen, prov);
            }
            return;
        }
        // then find serializer to use
        JsonSerializer<Object> ser = _serializer;
        if (ser == null) {
            Class<?> cls = value.getClass();
            PropertySerializerMap m = _dynamicSerializers;
            ser = m.serializerFor(cls);
            if (ser == null) {
                ser = _findAndAddDynamic(m, cls, prov);
            }
        }
        // and then see if we must suppress certain values (default, empty)
        if (_suppressableValue != null) {
            if (MARKER_FOR_EMPTY == _suppressableValue) {
                if (ser.isEmpty(prov, value)) {
                    return;
                }
            } else if (_suppressableValue.equals(value)) {
                return;
            }
        }
        // For non-nulls: simple check for direct cycles
        if (value == bean) {
            // three choices: exception; handled by call; or pass-through
            if (_handleSelfReference(bean, gen, prov, ser)) {
                return;
            }
        }
        gen.writeFieldName(_name);
        if (_typeSerializer == null) {
            ser.serialize(value, gen, prov);
        } else {
            ser.serializeWithType(value, gen, prov, _typeSerializer);
        }
    }
    
    @Override
    public void serializeAsOmittedField(Object bean, JsonGenerator gen, SerializerProvider prov) throws Exception
    {
        if (!gen.canOmitFields()) {
            gen.writeOmittedField(_name.getValue());
        }
    }
    
}