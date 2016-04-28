package com.enonic.xp.web.handler;

import javax.servlet.http.Cookie;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;

import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.websocket.WebSocketConfig;

@Beta
public class WebResponseImpl
    implements WebResponse
{
    private HttpStatus status;

    private MediaType contentType;

    private ImmutableMap<String, String> headers;

    private ImmutableList<Cookie> cookies;

    private WebSocketConfig webSocketConfig;

    private Object body;

    @Override
    public HttpStatus getStatus()
    {
        return status;
    }

    @Override
    public MediaType getContentType()
    {
        return contentType;
    }

    @Override
    public ImmutableMap<String, String> getHeaders()
    {
        return headers;
    }

    @Override
    public ImmutableList<Cookie> getCookies()
    {
        return cookies;
    }

    @Override
    public WebSocketConfig getWebSocket()
    {
        return webSocketConfig;
    }

    @Override
    public Object getBody()
    {
        return body;
    }

    @Override
    public void setStatus( final HttpStatus status )
    {
        this.status = status;
    }

    @Override
    public void setContentType( final MediaType contentType )
    {
        this.contentType = contentType;
    }

    @Override
    public void setHeaders( final ImmutableMap<String, String> headers )
    {
        this.headers = headers;
    }

    @Override
    public void setCookies( final ImmutableList<Cookie> cookies )
    {
        this.cookies = cookies;
    }

    @Override
    public void setWebSocketConfig( final WebSocketConfig webSocketConfig )
    {
        this.webSocketConfig = webSocketConfig;
    }

    @Override
    public void setBody( final Object body )
    {
        this.body = body;
    }


}
