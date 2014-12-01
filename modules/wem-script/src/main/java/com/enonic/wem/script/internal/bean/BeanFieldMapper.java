package com.enonic.wem.script.internal.bean;

import java.lang.reflect.Field;
import java.util.Map;

import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.script.convert.Converters;

public final class BeanFieldMapper
{
    private final Object bean;

    private final Map<String, Object> map;

    private BeanFieldMapper( final Object bean, final Map<String, Object> map )
    {
        this.bean = bean;
        this.map = map;
    }

    private void mapToBean()
    {
        for ( final Map.Entry<String, Object> entry : this.map.entrySet() )
        {
            applyProperty( entry.getKey(), entry.getValue() );
        }
    }

    private void applyProperty( final String key, final Object value )
    {
        try
        {
            final Field field = this.bean.getClass().getDeclaredField( key );
            applyProperty( field, value );
        }
        catch ( final NoSuchFieldException e )
        {
            // Do nothing
        }
    }

    private void applyProperty( final Field field, final Object value )
    {
        field.setAccessible( true );

        final Class<?> type = field.getType();
        final Object converted = Converters.convert( value, type );

        try
        {
            field.set( this.bean, converted );
        }
        catch ( final IllegalAccessException e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    public static void mapToBean( final Object bean, final Map<String, Object> map )
    {
        new BeanFieldMapper( bean, map ).mapToBean();
    }
}
