package com.enonic.wem.jsapi.internal.mapper;

import java.util.List;
import java.util.Map;

import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.script.serializer.MapGenerator;
import com.enonic.wem.script.serializer.MapSerializable;

final class PropertyTreeMapper
    implements MapSerializable
{
    private final PropertyTree value;

    public PropertyTreeMapper( final PropertyTree value )
    {
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        final Map<String, Object> map = this.value.toMap();
        serialize( gen, map );
    }

    private void serialize( final MapGenerator gen, final Map<?, ?> map )
    {
        for ( final Map.Entry<?, ?> entry : map.entrySet() )
        {
            serialize( gen, entry.getKey().toString(), entry.getValue() );
        }
    }

    private void serialize( final MapGenerator gen, final String key, final Object value )
    {
        if ( value instanceof List )
        {
            serialize( gen, key, (List<?>) value );
        }
    }

    private void serialize( final MapGenerator gen, final String key, final List<?> values )
    {
        gen.array( key );
        for ( final Object value : values )
        {
            serialize( gen, value );
        }
        gen.end();
    }

    private void serialize( final MapGenerator gen, final Object value )
    {
        if ( value instanceof Map )
        {
            gen.map();
            serialize( gen, (Map<?, ?>) value );
            gen.end();
        }
        else
        {
            gen.value( value );
        }
    }
}
