package com.enonic.xp.web.handler;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;

import com.google.common.annotations.Beta;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.net.MediaType;

import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.websocket.WebSocketConfig;

@Beta
public class WebResponseImpl
    implements WebResponse
{
    private HttpStatus status;

    private MediaType contentType;

    private Map<String, String> headers = Maps.newHashMap();

    private Set<Cookie> cookies = Sets.newHashSet();

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
    public Map<String, String> getHeaders()
    {
        return headers;
    }

    @Override
    public Set<Cookie> getCookies()
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
    public void setHeader( final String key, final String value )
    {
        this.headers.put( key, value );
    }

    @Override
    public void setCookie( Cookie cookie )
    {
        this.cookies.add( cookie );
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
