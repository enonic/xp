package com.enonic.xp.web.handler;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;

import com.google.common.annotations.Beta;
import com.google.common.net.MediaType;

import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.websocket.WebSocketConfig;

@Beta
public interface WebResponse
{
    HttpStatus getStatus();

    MediaType getContentType();

    Map<String, String> getHeaders();

    Set<Cookie> getCookies();

    WebSocketConfig getWebSocket();

    Object getBody();

    void setStatus( final HttpStatus status );

    void setContentType( final MediaType contentType );

    void setHeader( final String key, final String value );

    void setCookie( Cookie cookie );

    void setWebSocketConfig( final WebSocketConfig webSocketConfig );

    void setBody( final Object body );

//    boolean isPostProcess();

//    ImmutableList<String> getContributions( final HtmlTag tag );
//
//    boolean hasContributions();

//    String getAsString();

//    boolean applyFilters();
}
