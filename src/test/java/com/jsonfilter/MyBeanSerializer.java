package com.jsonfilter;

import java.io.IOException;
import java.util.Set;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerBuilder;
import com.fasterxml.jackson.databind.ser.impl.BeanAsArraySerializer;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.ser.impl.UnwrappingBeanSerializer;
import com.fasterxml.jackson.databind.ser.std.BeanSerializerBase;
import com.fasterxml.jackson.databind.util.NameTransformer;

/**
 * bean序列化处理
 * @author pengming
 * @Description 
 *
 * @Date  2016年7月27日 下午3:58:34
 */
public class MyBeanSerializer extends BeanSerializerBase {

	private static final long serialVersionUID = 1L;

	public MyBeanSerializer(JavaType type, BeanSerializerBuilder builder,
            BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties)
    {
        super(type, builder, properties, filteredProperties);
    }
    
    /**
     * Alternate copy constructor that can be used to construct
     * standard {@link MyBeanSerializer} passing an instance of
     * "compatible enough" source serializer.
     */
	public MyBeanSerializer(BeanSerializerBase src) {
        super(src);
    }

	public MyBeanSerializer(BeanSerializerBase src,
            ObjectIdWriter objectIdWriter) {
        super(src, objectIdWriter);
    }

	public MyBeanSerializer(BeanSerializerBase src,
            ObjectIdWriter objectIdWriter, Object filterId) {
        super(src, objectIdWriter, filterId);
    }
    
	public MyBeanSerializer(BeanSerializerBase src, Set<String> toIgnore) {
        super(src, toIgnore);
    }

    /*
    /**********************************************************
    /* Life-cycle: factory methods, fluent factories
    /**********************************************************
     */

    /**
     * Method for constructing dummy bean serializer; one that
     * never outputs any properties
     */
    public static MyBeanSerializer createDummy(JavaType forType)
    {
        return new MyBeanSerializer(forType, null, NO_PROPS, null);
    }

    @Override
    public JsonSerializer<Object> unwrappingSerializer(NameTransformer unwrapper) {
        return new UnwrappingBeanSerializer(this, unwrapper);
    }

    @Override
    public BeanSerializerBase withObjectIdWriter(ObjectIdWriter objectIdWriter) {
        return new MyBeanSerializer(this, objectIdWriter, _propertyFilterId);
    }

    @Override
    public BeanSerializerBase withFilterId(Object filterId) {
        return new MyBeanSerializer(this, _objectIdWriter, filterId);
    }

    @Override
    protected BeanSerializerBase withIgnorals(Set<String> toIgnore) {
        return new MyBeanSerializer(this, toIgnore);
    }

    /**
     * Implementation has to check whether as-array serialization
     * is possible reliably; if (and only if) so, will construct
     * a {@link BeanAsArraySerializer}, otherwise will return this
     * serializer as is.
     */
    @Override
    protected BeanSerializerBase asArraySerializer()
    {
        /* Can not:
         * 
         * - have Object Id (may be allowed in future)
         * - have "any getter"
         * - have per-property filters
         */
        if ((_objectIdWriter == null)
                && (_anyGetterWriter == null)
                && (_propertyFilterId == null)
                ) {
            return new BeanAsArraySerializer(this);
        }
        // already is one, so:
        return this;
    }
    
    /*
    /**********************************************************
    /* JsonSerializer implementation that differs between impls
    /**********************************************************
     */

    /**
     * Main serialization method that will delegate actual output to
     * configured
     * {@link BeanPropertyWriter} instances.
     */
    @Override
    public final void serialize(Object bean, JsonGenerator gen, SerializerProvider provider)
        throws IOException
    {
        if (_objectIdWriter != null) {
            gen.setCurrentValue(bean); // [databind#631]
            _serializeWithObjectId(bean, gen, provider, true);
            return;
        }
        gen.writeStartObject(bean);
        if (_propertyFilterId != null) {
            serializeFieldsFiltered(bean, gen, provider);
        } else {
            serializeFields(bean, gen, provider);
        }
        gen.writeEndObject();
    }
    
    /*
    /**********************************************************
    /* Standard methods
    /**********************************************************
     */

    @Override 
    public String toString() {
        return "MyBeanSerializer for "+handledType().getName();
    }
	
}