package com.enonic.wem.core.web.etag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

final class ShallowEtagResponseWrapper
    extends HttpServletResponseWrapper
{
    private final ByteArrayOutputStream content = new ByteArrayOutputStream();

    private final ServletOutputStream outputStream = new ResponseServletOutputStream();

    private PrintWriter writer;

    public ShallowEtagResponseWrapper( HttpServletResponse response )
    {
        super( response );
    }

    @Override
    public ServletOutputStream getOutputStream()
        throws IOException
    {
        return outputStream;
    }

    @Override
    public PrintWriter getWriter()
        throws IOException
    {
        if ( writer == null )
        {
            String characterEncoding = getCharacterEncoding();
            Writer targetWriter = ( characterEncoding != null
                ? new OutputStreamWriter( outputStream, characterEncoding )
                : new OutputStreamWriter( outputStream ) );
            writer = new PrintWriter( targetWriter );
        }
        return writer;
    }

    @Override
    public void resetBuffer()
    {
        content.reset();
    }

    @Override
    public void reset()
    {
        super.reset();
        resetBuffer();
    }

    public byte[] toByteArray()
    {
        return content.toByteArray();
    }

    private class ResponseServletOutputStream
        extends ServletOutputStream
    {

        @Override
        public void write( int b )
            throws IOException
        {
            content.write( b );
        }

    }
}
