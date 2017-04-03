package com.enonic.xp.lib.common;

import java.util.List;
import java.util.Map;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class PropertyTreeMapper
    implements MapSerializable
{
    private final PropertyTree value;

    private final boolean useRawValue;

    public PropertyTreeMapper( final PropertyTree value )
    {
        this.value = value;
        this.useRawValue = false;
    }

    public PropertyTreeMapper( final boolean useRawValue, final PropertyTree value )
    {
        this.useRawValue = useRawValue;
        this.value = value;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        serialize( gen, this.value );
    }

    private void serialize( final MapGenerator gen, final PropertyTree value )
    {
        final Map<String, Object> map = value.toMap();
        serializeMap( gen, map );
    }

    private void serializeMap( final MapGenerator gen, final Map<?, ?> map )
    {
        for ( final Map.Entry<?, ?> entry : map.entrySet() )
        {
            serializeKeyValue( gen, entry.getKey().toString(), entry.getValue() );
        }
    }

    private void serializeKeyValue( final MapGenerator gen, final String key, final Object value )
    {
        if ( value instanceof List )
        {
            serializeList( gen, key, (List<?>) value );
        }
        else if ( value instanceof Map )
        {
            serializeMap( gen, key, (Map<?, ?>) value );
        }
        else
        {
            if ( this.useRawValue )
            {
                gen.rawValue( key, value );
            }
            else
            {
                gen.value( key, value );
            }
        }
    }

    private void serializeList( final MapGenerator gen, final String key, final List<?> values )
    {
        if ( values.isEmpty() )
        {
            serializeKeyValue( gen, key, null );
            return;
        }

        if ( values.size() == 1 )
        {
            serializeKeyValue( gen, key, values.get( 0 ) );
            return;
        }

        gen.array( key );

        for ( final Object value : values )
        {
            serializeValue( gen, value );
        }
        gen.end();
    }

    private void serializeMap( final MapGenerator gen, final String key, final Map<?, ?> map )
    {
        gen.map( key );
        serializeMap( gen, map );
        gen.end();
    }

    private void serializeValue( final MapGenerator gen, final Object value )
    {
        if ( value instanceof Map )
        {
            gen.map();
            serializeMap( gen, (Map<?, ?>) value );
            gen.end();
        }
        else
        {
            if ( this.useRawValue )
            {
                gen.rawValue( value );
            }
            else
            {
                gen.value( value );
            }
        }
    }
}
