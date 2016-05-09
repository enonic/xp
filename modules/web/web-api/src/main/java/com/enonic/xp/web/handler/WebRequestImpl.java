package com.enonic.xp.web.handler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.websocket.WebSocketContext;

@Beta
public class WebRequestImpl
    implements WebRequest
{
    private final HttpMethod method;

    private final String scheme;

    private final String host;

    private final int port;

    private final String path;

    private final Multimap<String, String> params;

    private final String url;

    private final String endpointPath;

    private final ImmutableMap<String, String> headers;

    private final ImmutableMap<String, String> cookies;

    private final Object body;

    private final HttpServletRequest rawRequest;

    private final String contentType;

    private final WebSocketContext webSocketContext;

    protected WebRequestImpl( final Builder builder )
    {
        method = builder.method;
        scheme = builder.scheme;
        host = builder.host;
        port = builder.port;
        path = builder.path;
        params = builder.params;
        url = builder.url;
        endpointPath = builder.endpointPath;
        headers = ImmutableMap.copyOf( builder.headers );
        cookies = ImmutableMap.copyOf( builder.cookies );
        body = builder.body;
        rawRequest = builder.rawRequest;
        contentType = builder.contentType;
        webSocketContext = builder.webSocketContext;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final WebRequest webRequest )
    {
        return new Builder( webRequest );
    }

    @Override
    public HttpMethod getMethod()
    {
        return method;
    }

    @Override
    public String getScheme()
    {
        return scheme;
    }

    @Override
    public String getHost()
    {
        return host;
    }

    @Override
    public int getPort()
    {
        return port;
    }

    @Override
    public String getPath()
    {
        return path;
    }

    @Override
    public Multimap<String, String> getParams()
    {
        return params;
    }

    @Override
    public String getUrl()
    {
        return url;
    }

    @Override
    public String getEndpointPath()
    {
        return endpointPath;
    }

    @Override
    public Map<String, String> getHeaders()
    {
        return headers;
    }

    @Override
    public Map<String, String> getCookies()
    {
        return cookies;
    }

    @Override
    public Object getBody()
    {
        return body;
    }

    @Override
    public String getBodyAsString()
    {
        return body != null ? body.toString() : null;
    }

    @Override
    public HttpServletRequest getRawRequest()
    {
        return rawRequest;
    }

    @Override
    public String getContentType()
    {
        return contentType;
    }

    @Override
    public boolean isWebSocket()
    {
        return webSocketContext != null;
    }

    @Override
    public WebSocketContext getWebSocketContext()
    {
        return webSocketContext;
    }

    @Override
    public Object getAttribute( final String name )
    {
        return rawRequest.getAttribute( name );
    }

    @Override
    public void setAttribute( final String name, final Object value )
    {
        rawRequest.setAttribute( name, value );
    }

    public static class Builder
    {
        private HttpMethod method;

        private String scheme;

        private String host;

        private int port;

        private String path;

        private Multimap<String, String> params = HashMultimap.create();

        private String url;

        private String endpointPath;

        private Map<String, String> cookies = Maps.newHashMap();

        private Map<String, String> headers = Maps.newHashMap();

        private Object body;

        private HttpServletRequest rawRequest;

        private String contentType;

        private WebSocketContext webSocketContext;

        protected Builder()
        {
        }

        public Builder( final WebRequest webRequest )
        {
            method = webRequest.getMethod();
            scheme = webRequest.getScheme();
            host = webRequest.getHost();
            port = webRequest.getPort();
            path = webRequest.getPath();
            params = webRequest.getParams();
            url = webRequest.getUrl();
            endpointPath = webRequest.getEndpointPath();
            cookies = webRequest.getCookies();
            body = webRequest.getBody();
            rawRequest = webRequest.getRawRequest();
            contentType = webRequest.getContentType();
            webSocketContext = webRequest.getWebSocketContext();
        }

        public Builder method( final HttpMethod method )
        {
            this.method = method;
            return this;
        }

        public Builder scheme( final String scheme )
        {
            this.scheme = scheme;
            return this;
        }

        public Builder host( final String host )
        {
            this.host = host;
            return this;
        }

        public Builder port( final int port )
        {
            this.port = port;
            return this;
        }

        public Builder path( final String path )
        {
            this.path = path;
            return this;
        }


        public Builder params( final Multimap<String, String> params )
        {
            this.params = params;
            return this;
        }

        public Builder param( final String key, String value )
        {
            this.params.put( key, value );
            return this;
        }

        public Builder url( final String url )
        {
            this.url = url;
            return this;
        }

        public Builder endpointPath( final String endpointPath )
        {
            this.endpointPath = Strings.emptyToNull( endpointPath );
            return this;
        }

        public Builder header( final String key, String value )
        {
            headers.put( key, value );
            return this;
        }

        public Builder cookies( final ImmutableMap<String, String> cookies )
        {
            this.cookies = cookies;
            return this;
        }

        public Builder cookie( final String key, String value )
        {
            cookies.put( key, value );
            return this;
        }

        public Builder body( final Object body )
        {
            this.body = body;
            return this;
        }

        public Builder rawRequest( final HttpServletRequest rawRequest )
        {
            this.rawRequest = rawRequest;
            return this;
        }

        public Builder contentType( final String contentType )
        {
            this.contentType = contentType;
            return this;
        }

        public Builder webSocketContext( final WebSocketContext webSocketContext )
        {
            this.webSocketContext = webSocketContext;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( rawRequest, "rawRequest cannot be null" );
        }

        public WebRequestImpl build()
        {
            validate();
            return new WebRequestImpl( this );
        }
    }
}
