package com.enonic.xp.portal.handler;

import com.google.common.net.HttpHeaders;

import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.handler.WebException;
import com.enonic.xp.web.handler.WebRequest;
import com.enonic.xp.web.handler.WebResponse;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketEndpoint;

public abstract class PortalHandlerWorker<WebRequestType extends WebRequest, WebResponseType extends WebResponse>
{
    protected WebRequestType webRequest;

    protected WebResponseType webResponse;

    public PortalHandlerWorker( final Builder<? extends Builder, ? extends WebRequestType, ? extends WebResponseType> builder )
    {
        webRequest = builder.webRequest;
        webResponse = builder.webResponse;
    }

    public abstract WebResponseType execute()
        throws Exception;

    public WebSocketEndpoint newWebSocketEndpoint( final WebSocketConfig config )
        throws Exception
    {
        return null;
    }

    protected final WebException notFound( final String message, final Object... args )
    {
        return new WebException( HttpStatus.NOT_FOUND, String.format( message, args ) );
    }

    protected final WebException forbidden( final String message, final Object... args )
    {
        return new WebException( HttpStatus.FORBIDDEN, String.format( message, args ) );
    }

    protected void setResponseCacheable( final boolean isPublic )
    {
        final String cacheControlValue = ( isPublic ? "public" : "private" ) + ", max-age=31536000";
        webResponse.setHeader( HttpHeaders.CACHE_CONTROL, cacheControlValue );
    }

    public static class Builder<BuilderType extends Builder, WebRequestType extends WebRequest, WebResponseType extends WebResponse>
    {
        private WebRequestType webRequest;

        private WebResponseType webResponse;

        protected Builder()
        {
        }

        public BuilderType webRequest( final WebRequestType webRequest )
        {
            this.webRequest = webRequest;
            return (BuilderType) this;
        }

        public BuilderType webResponse( final WebResponseType webResponse )
        {
            this.webResponse = webResponse;
            return (BuilderType) this;
        }
    }
}
