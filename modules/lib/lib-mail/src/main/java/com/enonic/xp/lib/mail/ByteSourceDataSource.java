package com.enonic.xp.lib.mail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

import com.google.common.io.ByteSource;

class ByteSourceDataSource
    implements DataSource
{
    private final ByteSource source;

    private final String name;

    private final String mimeType;

    ByteSourceDataSource( final ByteSource source, final String name, final String mimeType )
    {
        this.source = source;
        this.name = name;
        this.mimeType = mimeType;
    }

    @Override
    public InputStream getInputStream()
        throws IOException
    {
        return this.source.openStream();
    }

    @Override
    public OutputStream getOutputStream()
        throws IOException
    {
        throw new UnsupportedOperationException( "Not implemented" );
    }

    @Override
    public String getContentType()
    {
        return this.mimeType;
    }

    @Override
    public String getName()
    {
        return this.name;
    }
}
