package com.enonic.xp.lib.http;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import com.google.common.base.Ascii;
import com.google.common.io.ByteProcessor;
import com.google.common.io.ByteSource;

public final class StringByteSource
    extends ByteSource
{
    private final String text;

    private final Charset charset;

    private byte[] bytes;

    StringByteSource( final String text, final Charset charset )
    {
        this.text = text == null ? "" : text;
        this.charset = charset;
        this.bytes = null;
    }

    private byte[] getBytes()
    {
        if ( bytes == null )
        {
            bytes = text.getBytes( charset );
        }
        return bytes;
    }

    @Override
    public InputStream openStream()
    {
        return new ByteArrayInputStream( getBytes() );
    }

    @Override
    public InputStream openBufferedStream()
        throws IOException
    {
        return openStream();
    }

    @Override
    public boolean isEmpty()
    {
        return text.isEmpty();
    }

    @Override
    public long size()
    {
        return text.isEmpty() ? 0 : getBytes().length;
    }

    @Override
    public byte[] read()
    {
        return getBytes().clone();
    }

    @Override
    public <T> T read( ByteProcessor<T> processor )
        throws IOException
    {
        processor.processBytes( getBytes(), 0, getBytes().length );
        return processor.getResult();
    }

    @Override
    public String toString()
    {
        return "StringByteSource(" + Ascii.truncate( text, 30, "..." ) + ")";
    }
}
