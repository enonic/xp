package com.enonic.xp.script.impl.util;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import com.enonic.xp.script.serializer.MapSerializable;

public final class JsObjectConverter
{
    private final JavascriptHelper helper;

    public JsObjectConverter( final JavascriptHelper helper )
    {
        this.helper = helper;
    }

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

        return value;
    }

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

    public Object fromJs( final Object value )
    {
        return toObject( value );
    }

    private Object toObject( final Object source )
    {
        if ( source instanceof ScriptObjectMirror )
        {
            return toObject( (ScriptObjectMirror) source );
        }

        return source;
    }

    private Object toObject( final ScriptObjectMirror source )
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

    private List<Object> toList( final ScriptObjectMirror source )
    {
        final List<Object> result = Lists.newArrayList();
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

    public Map<String, Object> toMap( final Object source )
    {
        if ( source instanceof ScriptObjectMirror )
        {
            return toMap( (ScriptObjectMirror) source );
        }

        return Maps.newHashMap();
    }

    private Map<String, Object> toMap( final ScriptObjectMirror source )
    {
        final Map<String, Object> result = Maps.newLinkedHashMap();
        for ( final Map.Entry<String, Object> entry : source.entrySet() )
        {
            final Object converted = toObject( entry.getValue() );
            if ( converted != null )
            {
                result.put( entry.getKey(), converted );
            }
        }

        return result;
    }

    private Function<Object[], Object> toFunction( final ScriptObjectMirror source )
    {
        return arg -> toObject( source.call( null, arg ) );
    }
}
