package com.enonic.wem.jsapi.internal.mapper;

import java.util.List;
import java.util.Map;

import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.script.serializer.MapGenerator;

public final class PropertyTreeMapper
{
    public static void serialize( final MapGenerator gen, final PropertyTree value )
    {
        final Map<String, Object> map = value.toMap();
        serializeMap( gen, map );
    }

    private static void serializeMap( final MapGenerator gen, final Map<?, ?> map )
    {
        for ( final Map.Entry<?, ?> entry : map.entrySet() )
        {
            serializeKeyValue( gen, entry.getKey().toString(), entry.getValue() );
        }
    }

    private static void serializeKeyValue( final MapGenerator gen, final String key, final Object value )
    {
        if ( value instanceof List )
        {
            serializeList( gen, key, (List<?>) value );
        }
    }

    private static void serializeList( final MapGenerator gen, final String key, final List<?> values )
    {
        gen.array( key );
        for ( final Object value : values )
        {
            serializeValue( gen, value );
        }
        gen.end();
    }

    private static void serializeValue( final MapGenerator gen, final Object value )
    {
        if ( value instanceof Map )
        {
            gen.map();
            serializeMap( gen, (Map<?, ?>) value );
            gen.end();
        }
        else
        {
            gen.value( value );
        }
    }
}
