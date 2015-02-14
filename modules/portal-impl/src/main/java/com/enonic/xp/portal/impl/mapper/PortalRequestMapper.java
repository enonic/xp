package com.enonic.xp.portal.impl.mapper;

import java.util.Collection;
import java.util.Objects;

import com.google.common.collect.Multimap;

import com.enonic.xp.portal.script.serializer.MapGenerator;
import com.enonic.xp.portal.script.serializer.MapSerializable;
import com.enonic.xp.portal.PortalRequest;

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
    }

    private void serializeMultimap( final String name, final MapGenerator gen, final Multimap<String, String> params )
    {
        gen.map( name );
        for ( String key : params.keySet() )
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
}
