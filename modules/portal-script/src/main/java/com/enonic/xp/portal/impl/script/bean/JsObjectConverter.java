package com.enonic.xp.portal.impl.script.bean;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.api.scripting.ScriptUtils;
import jdk.nashorn.internal.objects.Global;
import jdk.nashorn.internal.objects.NativeArray;

import com.enonic.xp.portal.impl.script.serializer.ScriptMapGenerator;
import com.enonic.xp.portal.script.serializer.MapSerializable;

public final class JsObjectConverter
{
    public static Object toJs( final Object value )
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

    public static Object[] toJsArray( final Object[] values )
    {
        final Object[] result = new Object[values.length];
        for ( int i = 0; i < values.length; i++ )
        {
            result[i] = toJs( values[i] );
        }

        return result;
    }

    private static Object toJs( final MapSerializable value )
    {
        final ScriptMapGenerator generator = new ScriptMapGenerator();
        value.serialize( generator );
        return generator.getRoot();
    }

    private static Object toJs( final List list )
    {
        final Object array = Global.allocate( new Object[0] );
        for ( final Object element : list )
        {
            NativeArray.push( array, toJs( element ) );
        }

        return array;
    }

    public static Object fromJs( final Object value )
    {
        return toObject( value );
    }

    public static Map<String, Object> fromJsAsMap( final Object value )
    {
        return toMap( value );
    }

    private static Object toObject( final Object source )
    {
        final Object object = ScriptUtils.wrap( source );
        if ( object instanceof ScriptObjectMirror )
        {
            return toObject( (ScriptObjectMirror) object );
        }

        return source;
    }

    private static Object toObject( final ScriptObjectMirror source )
    {
        if ( source.isArray() )
        {
            return toList( source );
        }
        else if ( source.isFunction() )
        {
            return toFunction( source );
        }
        else
        {
            return toMap( source );
        }
    }

    private static List<Object> toList( final ScriptObjectMirror source )
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

    public static Map<String, Object> toMap( final Object source )
    {
        final Object object = ScriptUtils.wrap( source );
        if ( object instanceof ScriptObjectMirror )
        {
            return toMap( (ScriptObjectMirror) object );
        }

        return Maps.newHashMap();
    }

    private static Map<String, Object> toMap( final ScriptObjectMirror source )
    {
        final Map<String, Object> result = Maps.newHashMap();
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

    private static Function<Object[], Object> toFunction( final ScriptObjectMirror source )
    {
        return arg -> toObject( source.call( source, arg ) );
    }
}
