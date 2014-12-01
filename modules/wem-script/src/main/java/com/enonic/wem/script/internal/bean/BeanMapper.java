package com.enonic.wem.script.internal.bean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.script.convert.Converters;

public final class BeanMapper
{
    private final Object bean;

    private final Map<?, ?> map;

    private final BeanInfo info;

    private BeanMapper( final Object bean, final Map<?, ?> map )
    {
        this.bean = bean;
        this.map = map;
        this.info = introspect( this.bean );
    }

    private void mapToBean()
    {
        for ( final PropertyDescriptor descriptor : info.getPropertyDescriptors() )
        {
            applyProperty( descriptor );
        }
    }

    private void applyProperty( final PropertyDescriptor descriptor )
    {
        final Method method = descriptor.getWriteMethod();
        if ( method == null )
        {
            return;
        }

        final String name = descriptor.getName();
        final Object value = this.map.get( name );
        if ( value == null )
        {
            return;
        }

        final Class<?> type = descriptor.getPropertyType();
        final Object converted = Converters.convert( value, type );

        try
        {
            method.invoke( this.bean, converted );
        }
        catch ( final InvocationTargetException e )
        {
            throw Exceptions.unchecked( e.getTargetException() );
        }
        catch ( final IllegalAccessException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private BeanInfo introspect( final Object bean )
    {
        try
        {
            return Introspector.getBeanInfo( bean.getClass() );
        }
        catch ( final IntrospectionException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    public static void mapToBean( final Object bean, final Map<?, ?> map )
    {
        new BeanMapper( bean, map ).mapToBean();
    }
}
