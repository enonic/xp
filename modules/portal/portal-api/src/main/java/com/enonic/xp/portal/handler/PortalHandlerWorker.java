package com.enonic.xp.portal.handler;

import com.google.common.net.HttpHeaders;

import com.enonic.xp.portal.PortalException;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketEndpoint;

public abstract class PortalHandlerWorker
{
    protected PortalRequest request;

    protected PortalResponse.Builder response;

    public abstract void execute()
        throws Exception;

    public WebSocketEndpoint newWebSocketEndpoint( final WebSocketConfig config )
        throws Exception
    {
        return null;
    }

    protected final PortalException notFound( final String message, final Object... args )
    {
        return PortalException.notFound( String.format( message, args ) );
    }

    protected final PortalException forbidden( final String message, final Object... args )
    {
        return new PortalException( HttpStatus.FORBIDDEN, String.format( message, args ) );
    }

    protected void setResponseCacheable( final boolean isPublic )
    {
        final String cacheControlValue = ( isPublic ? "public" : "private" ) + ", max-age=31536000";
        this.response.header( HttpHeaders.CACHE_CONTROL, cacheControlValue );
    }
}
