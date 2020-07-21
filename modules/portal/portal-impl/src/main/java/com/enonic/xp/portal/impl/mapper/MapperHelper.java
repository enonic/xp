package com.enonic.xp.portal.impl.mapper;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Multimap;

import com.enonic.xp.script.serializer.MapGenerator;

public final class MapperHelper
{
    public static void serializeMultimap( final String name, final MapGenerator gen, final Multimap<String, String> params )
    {
        serializeMultimap( name, gen, params.asMap() );
    }

    public static void serializeMultimap( final String name, final MapGenerator gen,
                                          final Map<String, ? extends Collection<String>> params )
    {
        gen.map( name );
        params.forEach( ( key, values ) -> {
            if ( values.size() == 1 )
            {
                gen.value( key, values.iterator().next() );
            }
            else
            {
                gen.array( key );
                values.forEach( gen::value );
                gen.end();
            }
        } );
        gen.end();
    }

    public static void serializeMap( final String name, final MapGenerator gen, final Map params )
    {
        serializeMap( name, gen, params, false );
    }

    public static void serializeMap( final String name, final MapGenerator gen, final Map<?, ?> params, final boolean nested )
    {
        gen.map( name );
        for ( final Map.Entry entry : params.entrySet() )
        {
            final Object value = entry.getValue();
            if ( nested && value instanceof Map )
            {
                serializeMap( entry.getKey().toString(), gen, (Map) value, true );
            }
            else
            {
                gen.value( entry.getKey().toString(), value );
            }
        }
        gen.end();
    }
}
