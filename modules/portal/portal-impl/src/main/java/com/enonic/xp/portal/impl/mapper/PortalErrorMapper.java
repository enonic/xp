package com.enonic.xp.portal.impl.mapper;


import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.error.PortalError;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class PortalErrorMapper
    implements MapSerializable
{
    private final PortalError error;

    public PortalErrorMapper( final PortalError error )
    {
        this.error = error;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "status", this.error.getStatus().value() );
        gen.value( "message", this.error.getMessage() );
        gen.value( "exception", this.error.getException() );
        serializeRequest( gen, this.error.getRequest() );
    }

    private void serializeRequest( final MapGenerator gen, final PortalRequest request )
    {
        gen.map( "request" );
        new PortalRequestMapper( request ).serialize( gen );
        gen.end();
    }
}
