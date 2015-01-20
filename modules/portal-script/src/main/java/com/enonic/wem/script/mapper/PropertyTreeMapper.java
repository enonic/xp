package com.enonic.wem.script.mapper;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Strings;

import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.script.serializer.MapGenerator;
import com.enonic.wem.script.serializer.MapSerializable;

public final class PropertyTreeMapper
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
        serialize( gen, this.value );
    }

    private static void serialize( final MapGenerator gen, final PropertyTree value )
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
        else if ( value instanceof Map<?, ?> )
        {
            gen.map( key );
            serializeMap( gen, (Map<?, ?>) value );
            gen.end();
        }
        else
        {
            gen.value( key, value );
        }
    }

    private static void serializeList( final MapGenerator gen, final String key, final List<?> values )
    {
        final List<?> simplified = simplifyList( values );
        if ( simplified.isEmpty() )
        {
            serializeKeyValue( gen, key, null );
            return;
        }

        if ( simplified.size() == 1 )
        {
            serializeKeyValue( gen, key, simplified.get( 0 ) );
            return;
        }

        gen.array( key );
        for ( final Object value : simplified )
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

    @SuppressWarnings("unchecked")
    private static List<Object> simplifyList( final List<?> list )
    {
        return ( (List<Object>) list ).stream().filter( item -> !isNullOrEmpty( item ) ).collect( Collectors.toList() );
    }

    private static boolean isNullOrEmpty( final Object value )
    {
        if ( value instanceof String )
        {
            return Strings.isNullOrEmpty( (String) value );
        }

        return value == null;
    }
}
