package com.enonic.xp.portal.impl.serializer;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Charsets;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.resource.Resource;

public final class ResponseSerializer
{
    private final PortalRequest portalRequest;

    private final PortalResponse portalResponse;

    public ResponseSerializer( final PortalRequest portalRequest, final PortalResponse portalResponse )
    {
        this.portalRequest = portalRequest;
        this.portalResponse = portalResponse;
    }

    public void serialize( final HttpServletResponse response )
        throws Exception
    {
        response.setStatus( this.portalResponse.getStatus() );
        response.setContentType( this.portalResponse.getContentType() );

        serializeHeaders( response, this.portalResponse.getHeaders() );
        serializeBody( response, this.portalResponse.getBody() );
    }

    private void serializeBody( final HttpServletResponse response, final Object body )
        throws Exception
    {
        if ( body instanceof Resource )
        {
            serializeBody( response, (Resource) body );
            return;
        }

        if ( body != null )
        {
            serializeBody( response, body.toString() );
        }
    }

    private void serializeBody( final HttpServletResponse response, final String body )
        throws Exception
    {
        writeToStream( response, body.getBytes( Charsets.UTF_8 ) );
    }

    private void writeToStream( final HttpServletResponse response, final byte[] data )
        throws Exception
    {
        response.setContentLength( data.length );

        if ( !isHeadRequest() )
        {
            response.getOutputStream().write( data );
        }
    }

    private void serializeBody( final HttpServletResponse response, final Resource body )
        throws Exception
    {
        writeToStream( response, body.readBytes() );
    }

    private void serializeHeaders( final HttpServletResponse response, final Map<String, String> headers )
    {
        for ( final Map.Entry<String, String> entry : headers.entrySet() )
        {
            response.setHeader( entry.getKey(), entry.getValue() );
        }
    }

    private boolean isHeadRequest()
    {
        return this.portalRequest.getMethod().equals( "HEAD" );
    }
}
