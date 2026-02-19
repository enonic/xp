package com.enonic.xp.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import jakarta.servlet.http.HttpServletRequest;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.security.IdProvider;
import com.enonic.xp.web.websocket.WebSocketContext;

@PublicApi
public class WebRequest
{
    private HttpMethod method;

    private final ListMultimap<String, String> params;

    private final Map<String, String> headers;

    private final Map<String, String> cookies;

    private final List<Locale> locales;

    private String scheme;

    private String host;

    private String remoteAddress;

    private int port;

    private String path;

    private String rawPath;

    private String url;

    private String endpointPath;

    private String basePath;

    private String contentType;

    private Object body;

    private HttpServletRequest rawRequest;

    private WebSocketContext webSocketContext;

    private IdProvider idProvider;

    public WebRequest()
    {
        this.params = MultimapBuilder.linkedHashKeys().arrayListValues().build();
        this.headers = new TreeMap<>( String.CASE_INSENSITIVE_ORDER );
        this.cookies = new HashMap<>();
        this.locales = new ArrayList<>();
        this.setRawPath( "/" );
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
        this.url = webRequest.url;
        this.contentType = webRequest.contentType;
        this.body = webRequest.body;
        this.rawRequest = webRequest.rawRequest;
        this.webSocketContext = webRequest.webSocketContext;
        this.idProvider = webRequest.idProvider;
        this.locales = webRequest.locales;
        this.setRawPath( webRequest.rawPath );
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
        this.rawPath = Objects.requireNonNull( rawPath );
        final int endpointPathIndex = rawPath.indexOf( "/_/" );
        if ( endpointPathIndex > -1 )
        {
            this.endpointPath = rawPath.substring( endpointPathIndex + 2 );
            this.basePath = rawPath.substring( 0, endpointPathIndex );
        }
        else
        {
            this.endpointPath = null;
            this.basePath = rawPath;
        }
    }

    public void setUrl( final String url )
    {
        this.url = url;
    }

    public Map<String, String> getHeaders()
    {
        return this.headers;
    }

    public IdProvider getIdProvider()
    {
        return idProvider;
    }

    public void setIdProvider( final IdProvider idProvider )
    {
        this.idProvider = idProvider;
    }

    public List<Locale> getLocales()
    {
        return locales;
    }

    public Map<String, String> getCookies()
    {
        return this.cookies;
    }

    /**
     * Returns the raw path of the request, URL decoded.
     * Designed for resource lookup.
     * NOT designed for url reconstruction, as it easy to introdue path traversal issues - use {@link #getUrl()} instead.
     *
     * @return path info of the request
     */
    @NonNull
    public String getRawPath()
    {
        return rawPath;
    }

    /**
     * The endpoint path from the {@link #getRawPath()} - everything starting from "/_/" prefix. May be null if "/_/" is not present in rawPath.
     *
     * @return the endpoint path in rawPath, starting from "/_/" prefix.
     */
    @Nullable
    public String getEndpointPath()
    {
        return this.endpointPath;
    }

    /**
     * The base path from the {@link #getRawPath()} - everything before "/_/" prefix. May be empty if "/_/" is at the very start of rawPath.
     *
     * @return base of the path in rawPath, before "/_/" prefix.
     */
    @NonNull
    public String getBasePath()
    {
        return this.basePath;
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
