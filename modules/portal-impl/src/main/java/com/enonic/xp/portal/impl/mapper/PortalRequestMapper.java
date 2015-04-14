package com.enonic.xp.portal.impl.mapper;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.script.serializer.MapGenerator;
import com.enonic.xp.portal.script.serializer.MapSerializable;

public final class PortalRequestMapper
    implements MapSerializable
{
    private final PortalRequest request;

    public PortalRequestMapper( final PortalRequest request )
    {
        this.request = request;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "uri", this.request.getUri() );
        gen.value( "method", this.request.getMethod() );
        gen.value( "mode", Objects.toString( this.request.getMode(), null ) );
        if ( this.request.getBranch() != null )
        {
            gen.value( "branch", this.request.getBranch().getName() );
        }

        serializeMultimap( "params", gen, this.request.getParams() );
        serializeMultimap( "formParams", gen, this.request.getFormParams() );
        serializeMultimap( "headers", gen, this.request.getHeaders() );
        serializeMap( "cookies", gen, this.request.getCookies() );
    }

    private void serializeMultimap( final String name, final MapGenerator gen, final Multimap<String, String> params )
    {
        gen.map( name );
        for ( final String key : params.keySet() )
        {
            final Collection<String> values = params.get( key );
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
        }
        gen.end();
    }

    private void serializeMap( final String name, final MapGenerator gen, final Map<String, String> params )
    {
        gen.map( name );
        for ( final Map.Entry<String, String> entry : params.entrySet() )
        {
            gen.value( entry.getKey(), entry.getValue() );
        }
        gen.end();
    }
}
