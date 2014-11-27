package com.enonic.wem.script.internal.util;

import java.util.Map;

import com.enonic.wem.script.internal.serializer.ScriptMapGenerator;
import com.enonic.wem.script.serializer.MapSerializable;

public final class JsObjectConverter
{
    public static Object toJs( final Object value )
    {
        if ( value instanceof MapSerializable )
        {
            return toJs( (MapSerializable) value );
        }

        return value;
    }

    private static Object toJs( final MapSerializable value )
    {
        final ScriptMapGenerator generator = new ScriptMapGenerator();
        value.serialize( generator );
        return generator.getRootObject();
    }

    public static Object fromJs( final Object value )
    {
        return ScriptObjectConverter.toObject( value );
    }

    public static Map<String, Object> fromJsAsMap( final Object value )
    {
        return ScriptObjectConverter.toMap( value );
    }
}
