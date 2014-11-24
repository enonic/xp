package com.enonic.wem.script.internal.util;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import jdk.nashorn.api.scripting.ScriptObjectMirror;
import jdk.nashorn.api.scripting.ScriptUtils;

public final class ScriptObjectConverter
{
    public static Object toObject( final Object source )
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

    private static Function<Object, Object> toFunction( final ScriptObjectMirror source )
    {
        return arg -> toObject( source.call( source, arg ) );
    }
}
