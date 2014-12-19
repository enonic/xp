package com.enonic.wem.portal.internal.mapper;

import java.util.Collection;
import java.util.Objects;

import com.google.common.collect.Multimap;

import com.enonic.wem.script.serializer.MapGenerator;
import com.enonic.wem.script.serializer.MapSerializable;
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
        gen.value( "method", this.request.getMethod() );
        gen.value( "mode", Objects.toString( this.request.getMode(), null ) );
        if ( this.request.getWorkspace() != null )
        {
            gen.value( "workspace", this.request.getWorkspace().getName() );
        }
        gen.value( "baseUri", this.request.getBaseUri() );

        serializeParams( gen, this.request.getParams() );
    }

    private void serializeParams( final MapGenerator gen, final Multimap<String, String> params )
    {
        gen.map( "params" );
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
                for ( String value : values )
                {
                    gen.value( value );
                }
                gen.end();
            }
        }
        gen.end();
    }
}
