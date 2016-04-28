package com.enonic.xp.web.handler;

import javax.servlet.http.Cookie;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.MediaType;

import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.websocket.WebSocketConfig;

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


}
