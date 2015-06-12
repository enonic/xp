package com.enonic.xp.web.mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class MockHttpServletResponse
    implements HttpServletResponse
{
    private final static String DEFAULT_CHARACTER_ENCODING = "ISO-8859-1";

    private boolean committed;

    private String characterEncoding = DEFAULT_CHARACTER_ENCODING;

    private final ByteArrayOutputStream content;

    private PrintWriter writer;

    public MockHttpServletResponse()
    {
        this.content = new ByteArrayOutputStream();
    }

    @Override
    public void addCookie( final Cookie cookie )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsHeader( final String name )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encodeURL( final String url )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encodeRedirectURL( final String url )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encodeUrl( final String url )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String encodeRedirectUrl( final String url )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendError( final int sc, final String msg )
        throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendError( final int sc )
        throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void sendRedirect( final String location )
        throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setDateHeader( final String name, final long date )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addDateHeader( final String name, final long date )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHeader( final String name, final String value )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addHeader( final String name, final String value )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setIntHeader( final String name, final int value )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addIntHeader( final String name, final int value )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStatus( final int sc )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStatus( final int sc, final String sm )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getStatus()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getHeader( final String name )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> getHeaders( final String name )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> getHeaderNames()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCharacterEncoding()
    {
        return this.characterEncoding;
    }

    @Override
    public String getContentType()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public ServletOutputStream getOutputStream()
        throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public PrintWriter getWriter()
        throws IOException
    {
        if ( this.writer == null )
        {
            final Writer targetWriter = new OutputStreamWriter( this.content, this.characterEncoding );
            this.writer = new PrintWriter( targetWriter, true );
        }

        return this.writer;
    }

    @Override
    public void setCharacterEncoding( final String charset )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContentLength( final int len )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setContentType( final String type )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBufferSize( final int size )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getBufferSize()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void flushBuffer()
    {
        if ( this.writer != null )
        {
            this.writer.flush();
        }

        try
        {
            this.content.flush();
        }
        catch ( final Exception e )
        {
            // Do nothing
        }

        setCommitted( true );
    }

    @Override
    public void resetBuffer()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCommitted()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void reset()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLocale( final Locale loc )
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Locale getLocale()
    {
        throw new UnsupportedOperationException();
    }

    public String getContentAsString()
        throws UnsupportedEncodingException
    {
        flushBuffer();
        return this.content.toString( this.characterEncoding );
    }

    public void setCommitted( final boolean value )
    {
        this.committed = value;
    }
}
