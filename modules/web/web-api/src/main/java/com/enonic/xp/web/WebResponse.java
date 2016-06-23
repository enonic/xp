package com.enonic.xp.web;

import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.net.MediaType;

import com.enonic.xp.web.websocket.WebSocketConfig;

@Beta
public class WebResponse
{
    private final HttpStatus status;

    private final MediaType contentType;

    private final Object body;

    private final ImmutableMap<String, String> headers;

    private final ImmutableList<Cookie> cookies;

    private final WebSocketConfig webSocket;

    protected WebResponse( final Builder builder )
    {
        this.status = builder.status;
        this.contentType = builder.contentType;
        this.body = builder.body;
        this.headers = builder.headers.build();
        this.cookies = builder.cookies.build();
        this.webSocket = builder.webSocket;
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
            return new ObjectMapper().writeValueAsString( value );
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

    public static class Builder
    {
        private Object body;

        private ImmutableMap.Builder<String, String> headers;

        private MediaType contentType = MediaType.PLAIN_TEXT_UTF_8;

        private HttpStatus status = HttpStatus.OK;

        private ImmutableList.Builder<Cookie> cookies;

        private WebSocketConfig webSocket;

        protected Builder()
        {
            clearHeaders();
            clearCookies();
        }

        protected Builder( final WebResponse source )
        {
            this.body = source.body;
            headers( source.headers );
            this.contentType = source.contentType;
            this.status = source.status;
            cookies( source.cookies );
            this.webSocket = source.webSocket;
        }

        public Builder body( final Object body )
        {
            this.body = body;
            return this;
        }

        public Builder headers( final Map<String, String> headers )
        {
            if ( this.headers == null )
            {
                clearHeaders();
            }
            this.headers.putAll( headers );
            return this;
        }

        public Builder header( final String key, final String value )
        {
            if ( this.headers == null )
            {
                clearHeaders();
            }
            this.headers.put( key, value );
            return this;
        }

        public Builder clearHeaders()
        {
            headers = ImmutableSortedMap.orderedBy( String.CASE_INSENSITIVE_ORDER );
            return this;
        }

        public Builder cookies( final List<Cookie> cookies )
        {
            if ( this.cookies == null )
            {
                clearCookies();
            }
            this.cookies.addAll( cookies );
            return this;
        }

        public Builder cookie( final Cookie cookie )
        {
            if ( this.cookies == null )
            {
                clearCookies();
            }
            this.cookies.add( cookie );
            return this;
        }

        public Builder clearCookies()
        {
            this.cookies = ImmutableList.builder();
            return this;
        }

        public Builder contentType( MediaType contentType )
        {
            this.contentType = contentType;
            return this;
        }

        public Builder status( final HttpStatus status )
        {
            this.status = status;
            return this;
        }

        public Builder webSocket( final WebSocketConfig webSocket )
        {
            this.webSocket = webSocket;
            return this;
        }

        public WebResponse build()
        {
            return new WebResponse( this );
        }
    }

}
