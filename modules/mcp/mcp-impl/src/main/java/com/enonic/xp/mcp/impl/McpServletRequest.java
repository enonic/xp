package com.enonic.xp.mcp.impl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.jspecify.annotations.NullMarked;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

@NullMarked
final class McpServletRequest
    extends HttpServletRequestWrapper
{
    private final byte[] body;

    McpServletRequest( final HttpServletRequest request, final String body )
    {
        super( request );
        this.body = body.getBytes( StandardCharsets.UTF_8 );
    }

    @Override
    public int getContentLength()
    {
        return body.length;
    }

    @Override
    public long getContentLengthLong()
    {
        return body.length;
    }

    @Override
    public String getCharacterEncoding()
    {
        return StandardCharsets.UTF_8.name();
    }

    @Override
    public BufferedReader getReader()
    {
        return new BufferedReader( new InputStreamReader( getInputStream(), StandardCharsets.UTF_8 ) );
    }

    @Override
    public ServletInputStream getInputStream()
    {
        final var input = new ByteArrayInputStream( body );
        return new ServletInputStream()
        {
            @Override
            public int read()
            {
                return input.read();
            }

            @Override
            public boolean isFinished()
            {
                return input.available() == 0;
            }

            @Override
            public boolean isReady()
            {
                return true;
            }

            @Override
            public void setReadListener( final ReadListener listener )
            {
                throw new UnsupportedOperationException( "MCP request bodies use blocking reads" );
            }
        };
    }
}
