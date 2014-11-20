package com.enonic.wem.script.internal.bean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.wem.api.util.Exceptions;

// http://fisheye.igniterealtime.org/browse/openfiregit/src/java/org/jivesoftware/util/BeanUtils.java?r=2a6595537e2eb571dcfa35a6c737ab68d13b9673
public final class BeanHelper
{
    public static Map<String, Object> getAsMap( final Object bean )
    {
        final Map<String, Object> map = Maps.newHashMap();
        final BeanInfo info = introspect( bean );

        for ( final PropertyDescriptor descriptor : info.getPropertyDescriptors() )
        {
            final String name = descriptor.getName();
            final Object value = readValue( bean, descriptor );

            if ( value != null )
            {
                map.put( name, value );
            }
        }

        return map;
    }

    private static BeanInfo introspect( final Object bean )
    {
        try
        {
            return Introspector.getBeanInfo( bean.getClass() );
        }
        catch ( final IntrospectionException e )
        {
            throw new RuntimeException( e );
        }
    }

    private static Object readValue( final Object bean, final PropertyDescriptor descriptor )
    {
        final Method method = descriptor.getReadMethod();
        if ( method == null )
        {
            return null;
        }

        try
        {
            return method.invoke( bean );
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
}
