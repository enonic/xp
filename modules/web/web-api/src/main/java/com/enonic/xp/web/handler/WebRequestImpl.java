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

    public static class Builder<BuilderType extends Builder>
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

        public BuilderType method( final HttpMethod method )
        {
            this.method = method;
            return (BuilderType) this;
        }

        public BuilderType scheme( final String scheme )
        {
            this.scheme = scheme;
            return (BuilderType) this;
        }

        public BuilderType host( final String host )
        {
            this.host = host;
            return (BuilderType) this;
        }

        public BuilderType port( final int port )
        {
            this.port = port;
            return (BuilderType) this;
        }

        public BuilderType path( final String path )
        {
            this.path = path;
            return (BuilderType) this;
        }


        public BuilderType params( final Multimap<String, String> params )
        {
            this.params = params;
            return (BuilderType) this;
        }

        public Builder param( final String key, String value )
        {
            this.params.put( key, value );
            return this;
        }

        public BuilderType url( final String url )
        {
            this.url = url;
            return (BuilderType) this;
        }

        public BuilderType endpointPath( final String endpointPath )
        {
            this.endpointPath = Strings.emptyToNull( endpointPath );
            return (BuilderType) this;
        }

        public BuilderType header( final String key, String value )
        {
            headers.put( key, value );
            return (BuilderType) this;
        }

        public BuilderType cookies( final ImmutableMap<String, String> cookies )
        {
            this.cookies = cookies;
            return (BuilderType) this;
        }

        public BuilderType cookie( final String key, String value )
        {
            cookies.put( key, value );
            return (BuilderType) this;
        }

        public BuilderType body( final Object body )
        {
            this.body = body;
            return (BuilderType) this;
        }

        public BuilderType rawRequest( final HttpServletRequest rawRequest )
        {
            this.rawRequest = rawRequest;
            return (BuilderType) this;
        }

        public BuilderType contentType( final String contentType )
        {
            this.contentType = contentType;
            return (BuilderType) this;
        }

        public BuilderType webSocketContext( final WebSocketContext webSocketContext )
        {
            this.webSocketContext = webSocketContext;
            return (BuilderType) this;
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
