package com.enonic.xp.portal.impl.auth;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.google.common.io.ByteStreams;

public class MultiBodyReaderRequestMapper
    extends HttpServletRequestWrapper
{
    final HttpServletRequest request;

    private byte[] body;

    public MultiBodyReaderRequestMapper( final HttpServletRequest request )
    {
        super( request );
        this.request = request;
    }

    @Override
    public BufferedReader getReader()
        throws IOException
    {
        return new BufferedReader( new InputStreamReader( getInputStream() ) );
    }

    @Override
    public ServletInputStream getInputStream()
        throws IOException
    {
        if ( this.body == null )
        {
            this.body = ByteStreams.toByteArray( super.getInputStream() );
        }
        return new ByteArrayServletInputStream( this.body );
    }

    public class ByteArrayServletInputStream
        extends ServletInputStream
    {
        private ByteArrayInputStream byteArrayInputStream;

        public ByteArrayServletInputStream( final byte[] byteArray )
        {
            byteArrayInputStream = new ByteArrayInputStream( byteArray );
        }

        @Override
        public int read()
            throws IOException
        {
            return byteArrayInputStream.read();
        }

        @Override
        public boolean isFinished()
        {
            return byteArrayInputStream.available() > 0;
        }

        @Override
        public boolean isReady()
        {
            return true;
        }

        @Override
        public void setReadListener( final ReadListener readListener )
        {
        }
    }


}
