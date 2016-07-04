package com.enonic.xp.web;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.common.annotations.Beta;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import com.enonic.xp.security.UserStore;
import com.enonic.xp.web.websocket.WebSocketContext;

@Beta
public class WebRequest
{
    private HttpMethod method;

    private final Multimap<String, String> params;

    private final Map<String, String> headers;

    private final Map<String, String> cookies;

    private String scheme;

    private String host;

    private String remoteAddress;

    private int port;

    private String path;

    private String rawPath;

    private String url;

    private String endpointPath;

    private String contentType;

    private Object body;

    private HttpServletRequest rawRequest;

    private WebSocketContext webSocketContext;

    private UserStore userStore;

    public WebRequest()
    {
        this.params = HashMultimap.create();
        this.headers = Maps.newHashMap();
        this.cookies = Maps.newHashMap();
    }

    public WebRequest( final WebRequest webRequest )
    {
        this.method = webRequest.method;
        this.params = webRequest.params;
        this.headers = webRequest.headers;
        this.cookies = webRequest.cookies;
        this.scheme = webRequest.scheme;
        this.host = webRequest.host;
        this.remoteAddress = webRequest.remoteAddress;
        this.port = webRequest.port;
        this.path = webRequest.path;
        this.rawPath = webRequest.rawPath;
        this.url = webRequest.url;
        this.endpointPath = webRequest.endpointPath;
        this.contentType = webRequest.contentType;
        this.body = webRequest.body;
        this.rawRequest = webRequest.rawRequest;
        this.webSocketContext = webRequest.webSocketContext;
        this.userStore = webRequest.userStore;
    }

    public HttpMethod getMethod()
    {
        return this.method;
    }

    public Multimap<String, String> getParams()
    {
        return this.params;
    }

    public String getScheme()
    {
        return scheme;
    }

    public String getHost()
    {
        return host;
    }

    public int getPort()
    {
        return port;
    }

    public String getRemoteAddress()
    {
        return remoteAddress;
    }

    public String getPath()
    {
        return path;
    }

    public String getRawPath()
    {
        return rawPath;
    }

    public String getUrl()
    {
        return url;
    }

    public void setMethod( final HttpMethod method )
    {
        this.method = method;
    }

    public void setScheme( final String scheme )
    {
        this.scheme = scheme;
    }

    public void setHost( final String host )
    {
        this.host = host;
    }

    public void setPort( final int port )
    {
        this.port = port;
    }

    public void setRemoteAddress( final String remoteAddress )
    {
        this.remoteAddress = remoteAddress;
    }

    public void setPath( final String path )
    {
        this.path = path;
    }

    public void setRawPath( final String rawPath )
    {
        this.rawPath = rawPath;
    }

    public void setUrl( final String url )
    {
        this.url = url;
    }

    public Map<String, String> getHeaders()
    {
        return this.headers;
    }

    public UserStore getUserStore()
    {
        return userStore;
    }

    public void setUserStore( final UserStore userStore )
    {
        this.userStore = userStore;
    }

    public Map<String, String> getCookies()
    {
        return this.cookies;
    }

    public String getEndpointPath()
    {
        return this.endpointPath;
    }

    public void setEndpointPath( final String endpointPath )
    {
        this.endpointPath = Strings.emptyToNull( endpointPath );
    }

    public String getContentType()
    {
        return this.contentType;
    }

    public void setContentType( final String contentType )
    {
        this.contentType = contentType;
    }

    public Object getBody()
    {
        return this.body;
    }

    public void setBody( final Object body )
    {
        this.body = body;
    }

    public String getBodyAsString()
    {
        return this.body != null ? this.body.toString() : null;
    }

    public HttpServletRequest getRawRequest()
    {
        return rawRequest;
    }

    public void setRawRequest( final HttpServletRequest rawRequest )
    {
        this.rawRequest = rawRequest;
    }

    public boolean isWebSocket()
    {
        return this.webSocketContext != null;
    }

    public WebSocketContext getWebSocketContext()
    {
        return webSocketContext;
    }

    public void setWebSocketContext( final WebSocketContext webSocketContext )
    {
        this.webSocketContext = webSocketContext;
    }
}
