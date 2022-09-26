package com.enonic.xp.portal.impl.mapper;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class PortalRequestMapper
    implements MapSerializable
{
    private static volatile boolean lowercaseHeaders = true;

    private final PortalRequest request;

    public PortalRequestMapper( final PortalRequest request )
    {
        this.request = request;
    }

    static void setLowercaseHeaders( final boolean lowercaseHeaders )
    {
        PortalRequestMapper.lowercaseHeaders = lowercaseHeaders;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "method", this.request.getMethod() );
        gen.value( "scheme", this.request.getScheme() );
        gen.value( "host", this.request.getHost() );
        gen.value( "port", this.request.getPort() );
        gen.value( "path", this.request.getPath() );
        gen.value( "rawPath", this.request.getRawPath() );
        gen.value( "url", this.request.getUrl() );
        gen.value( "remoteAddress", this.request.getRemoteAddress() );
        gen.value( "mode", Objects.toString( this.request.getMode(), null ) );
        gen.value( "webSocket", this.request.isWebSocket() );

        if ( this.request.isValidTicket() != null )
        {
            gen.value( "validTicket", this.request.isValidTicket() );
        }

        if ( this.request.getRepositoryId() != null )
        {
            gen.value( "repositoryId", this.request.getRepositoryId().toString() );
        }

        if ( this.request.getBranch() != null )
        {
            gen.value( "branch", this.request.getBranch().getValue() );
        }

        if ( this.request.getContextPath() != null )
        {
            gen.value( "contextPath", this.request.getContextPath() );
        }

        serializeBody( gen );
        MapperHelper.serializeMultimap( "params", gen, this.request.getParams().asMap() );
        serializeHeaders( gen );
        gen.value( "cookies", this.request.getCookies() );
    }

    private void serializeHeaders( final MapGenerator gen )
    {
        final Map<String, String> headers = this.request.getHeaders();
        if ( lowercaseHeaders )
        {
            gen.map( "headers" );
            headers.forEach( ( key, value ) -> gen.value( key.toLowerCase( Locale.ROOT ), value ) );
            gen.end();
        }
        else
        {
            gen.value( "headers", headers );
        }
    }

    private void serializeBody( final MapGenerator gen )
    {
        if ( this.request.getContentType() == null )
        {
            return;
        }

        gen.value( "contentType", this.request.getContentType() );
        gen.value( "body", this.request.getBodyAsString() );
    }
}
