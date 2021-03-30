package com.enonic.xp.script.graaljs.impl.util;

import java.util.List;

import com.enonic.xp.script.serializer.MapSerializable;

public final class JsObjectConverter
{
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

    private Object toJs( final MapSerializable value )
    {
        final ScriptMapGenerator generator = new ScriptMapGenerator();
        value.serialize( generator );
        return generator.getRoot();
    }

    private Object toJs( final List list )
    {
        throw new UnsupportedOperationException();
    }

}
