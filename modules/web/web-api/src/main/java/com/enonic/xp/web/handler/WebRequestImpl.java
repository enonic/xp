package com.enonic.xp.web.handler;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

import com.enonic.xp.web.HttpMethod;

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

    private final ImmutableMap<String, String> cookies;

    private final Object body;

    private final HttpServletRequest rawRequest;

    private final String contentType;

    private final boolean webSocket;

    private WebRequestImpl( final Builder builder )
    {
        method = builder.method;
        scheme = builder.scheme;
        host = builder.host;
        port = builder.port;
        path = builder.path;
        params = builder.params;
        url = builder.url;
        cookies = builder.cookies;
        body = builder.body;
        rawRequest = builder.rawRequest;
        contentType = builder.contentType;
        webSocket = builder.webSocket;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final WebRequestImpl webRequest )
    {
        Builder builder = new Builder();
        builder.method = webRequest.method;
        builder.scheme = webRequest.scheme;
        builder.host = webRequest.host;
        builder.port = webRequest.port;
        builder.path = webRequest.path;
        builder.params = webRequest.params;
        builder.url = webRequest.url;
        builder.cookies = webRequest.cookies;
        builder.body = webRequest.body;
        builder.rawRequest = webRequest.rawRequest;
        builder.contentType = webRequest.contentType;
        builder.webSocket = webRequest.webSocket;
        return builder;
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
        return webSocket;
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

    public static final class Builder
    {
        private HttpMethod method;

        private String scheme;

        private String host;

        private int port;

        private String path;

        private Multimap<String, String> params;

        private String url;

        private ImmutableMap<String, String> cookies;

        private Object body;

        private HttpServletRequest rawRequest;

        private String contentType;

        private boolean webSocket;

        private Builder()
        {
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

        public Builder url( final String url )
        {
            this.url = url;
            return this;
        }

        public Builder cookies( final ImmutableMap<String, String> cookies )
        {
            this.cookies = cookies;
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

        public Builder webSocket( final boolean webSocket )
        {
            this.webSocket = webSocket;
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
