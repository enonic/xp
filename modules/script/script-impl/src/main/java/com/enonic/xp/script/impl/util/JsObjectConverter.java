package com.enonic.xp.script.impl.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.script.Bindings;

import org.openjdk.nashorn.api.scripting.JSObject;

import com.enonic.xp.script.serializer.MapSerializable;

public final class JsObjectConverter
    implements ObjectConverter
{
    private final JavascriptHelper<?> helper;

    public JsObjectConverter( final JavascriptHelper<?> helper )
    {
        this.helper = helper;
    }

    @Override
    public Object toJs( final Object value )
    {
        if ( value instanceof MapSerializable )
        {
            return toJs( (MapSerializable) value );
        }

        if ( value instanceof List )
        {
            return toJs( (List) value );
        }

        if ( value != null && value.getClass().isArray() && !value.getClass().getComponentType().isPrimitive() )
        {
            return toJs( Arrays.asList( (Object[]) value ) );
        }

        return value;
    }

    @Override
    public Object[] toJsArray( final Object[] values )
    {
        final Object[] result = new Object[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = toJs( values[i] );
        }

        return result;
    }

    private Object toJs( final MapSerializable value )
    {
        final ScriptMapGenerator generator = new ScriptMapGenerator( this.helper );
        value.serialize( generator );
        return generator.getRoot();
    }

    private Object toJs( final List list )
    {
        final Object array = this.helper.newJsArray();
        for ( final Object element : list )
        {
            NashornHelper.addToNativeArray( array, toJs( element ) );
        }

        return array;
    }

    @Override
    public Object fromJs( final Object value )
    {
        return toObject( value );
    }

    private Object toObject( final Object source )
    {
        if ( source instanceof JSObject )
        {
            return toObject( (JSObject) source );
        }

        return source;
    }

    private Object toObject( final JSObject source )
    {
        if ( source.isArray() )
        {
            return toList( source );
        }
        else if ( source.isFunction() )
        {
            return toFunction( source );
        }
        else if ( NashornHelper.isDateType( source ) )
        {
            return NashornHelper.toDate( source );
        }
        else
        {
            return toMap( source );
        }
    }

    private List<Object> toList( final JSObject source )
    {
        final List<Object> result = new ArrayList<>();
        for ( final Object item : source.values() )
        {
            final Object converted = toObject( item );
            if ( converted != null )
            {
                result.add( converted );
            }
        }

        return result;
    }

    @Override
    public Map<String, Object> toMap( final Object source )
    {
        if ( source instanceof Bindings )
        {
            return toMap( (Bindings) source );
        }

        return new HashMap<>();
    }

    private Map<String, Object> toMap( final Bindings source )
    {
        final Map<String, Object> result = new LinkedHashMap<>();
        for ( final Map.Entry<String, Object> entry : source.entrySet() )
        {
            final Object converted = toObject( entry.getValue() );
//            if ( converted != null )
//            {
                result.put( entry.getKey(), converted );
//            }
        }

        return result;
    }

    private Function<Object[], Object> toFunction( final JSObject source )
    {
        return arg -> toObject( source.call( null, arg ) );
    }
}
