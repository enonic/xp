package com.enonic.wem.script.v2;

import java.util.function.Function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import jdk.nashorn.internal.objects.NativeJava;

import com.enonic.wem.script.internal.serializer.ScriptMapGenerator;
import com.enonic.wem.script.serializer.MapSerializable;

public final class ScriptHelper
{
    public static Object convert( final Object value )
    {
        if ( value instanceof MapSerializable )
        {
            return convert( (MapSerializable) value );
        }

        return value;
    }

    private static Object convert( final MapSerializable value )
    {
        final ScriptMapGenerator generator = new ScriptMapGenerator();
        value.serialize( generator );
        return generator.getRoot();
    }

    public static Object toArray()
    {
        return NativeJava.from( null, Lists.newArrayList( 1, 2 ) );
    }

    public static Object toMap()
    {
        return NativeJava.from( null, Maps.newHashMap() );
    }

    public static Function<Object, Object> command( final String name )
    {
        return o -> "execute " + name + " with " + o;
    }
}
