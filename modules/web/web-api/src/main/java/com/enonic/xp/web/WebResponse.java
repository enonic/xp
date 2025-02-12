package com.enonic.xp.web;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import javax.servlet.http.Cookie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.net.MediaType;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.web.websocket.WebSocketConfig;

@PublicApi
public class WebResponse
{
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final HttpStatus status;

    private final MediaType contentType;

    private final Object body;

    private final ImmutableMap<String, String> headers;

    private final ImmutableList<Cookie> cookies;

    private final WebSocketConfig webSocket;

    protected WebResponse( final Builder<?> builder )
    {
        this.status = builder.status;
        this.contentType = builder.contentType;
        this.body = builder.body;
        this.headers = ImmutableSortedMap.copyOf( builder.headers, String.CASE_INSENSITIVE_ORDER );
        this.cookies = builder.cookies.build();
        this.webSocket = builder.webSocket;
    }

    public static Builder<?> create()
    {
        return new Builder<>();
    }

    public static Builder<?> create( final WebResponse source )
    {
        return new Builder<>( source );
    }

    public HttpStatus getStatus()
    {
        return this.status;
    }

    public MediaType getContentType()
    {
        return this.contentType;
    }

    public Object getBody()
    {
        return this.body;
    }

    public ImmutableMap<String, String> getHeaders()
    {
        return this.headers;
    }

    @Deprecated
    public String getAsString()
    {
        if ( this.body instanceof Map )
        {
            return convertToJson( this.body );
        }
        return ( this.body != null ) ? this.body.toString() : null;
    }

    private String convertToJson( final Object value )
    {
        try
        {
            return MAPPER.writeValueAsString( value );
        }
        catch ( final Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    public ImmutableList<Cookie> getCookies()
    {
        return cookies;
    }

    public WebSocketConfig getWebSocket()
    {
        return this.webSocket;
    }

    public static class Builder<T extends Builder<T>>
    {
        private Object body;

        private final Map<String, String> headers = new TreeMap<>( String.CASE_INSENSITIVE_ORDER );

        private MediaType contentType = MediaType.PLAIN_TEXT_UTF_8;

        private HttpStatus status = HttpStatus.OK;

        private ImmutableList.Builder<Cookie> cookies = ImmutableList.builder();

        private WebSocketConfig webSocket;

        protected Builder()
        {
        }

        protected Builder( final WebResponse source )
        {
            this.body = source.body;
            putAllHeaders( source.headers );
            this.contentType = source.contentType;
            this.status = source.status;
            addAllCookies( source.cookies );
            this.webSocket = source.webSocket;
        }

        public T body( final Object body )
        {
            this.body = body;
            return (T) this;
        }

        public T headers( final Map<String, String> headers )
        {
            putAllHeaders( headers );
            return (T) this;
        }

        public T header( final String key, final String value )
        {
            putHeader( key, value );
            return (T) this;
        }

        public T removeHeader( final String key )
        {
            this.headers.remove( key );
            return (T) this;
        }

        public T clearHeaders()
        {
            headers.clear();
            return (T) this;
        }

        public T cookies( final List<Cookie> cookies )
        {
            addAllCookies( cookies );
            return (T) this;
        }

        public T cookie( final Cookie cookie )
        {
            this.cookies.add( cookie );
            return (T) this;
        }

        public T clearCookies()
        {
            this.cookies = ImmutableList.builder();
            return (T) this;
        }

        public T contentType( MediaType contentType )
        {
            this.contentType = contentType;
            return (T) this;
        }

        public T status( final HttpStatus status )
        {
            this.status = status;
            return (T) this;
        }

        public T webSocket( final WebSocketConfig webSocket )
        {
            this.webSocket = webSocket;
            return (T) this;
        }

        public WebResponse build()
        {
            return new WebResponse( this );
        }

        private void putAllHeaders( final Map<String, String> headers )
        {
            headers.forEach( this::putHeader );
        }

        private void putHeader( final String key, final String value )
        {
            this.headers.put( key.toLowerCase( Locale.ROOT ), Objects.requireNonNull( value ) );
        }

        public void addAllCookies( final List<Cookie> cookies )
        {
            this.cookies.addAll( cookies );
        }
    }
}
