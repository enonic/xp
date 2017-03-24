package com.enonic.xp.lib.content.mapper;

import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.ByteProcessor;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.icon.Icon;

public final class IconByteSource
    extends ByteSource
{
    private final Icon icon;

    IconByteSource( final Icon icon )
    {
        this.icon = icon;
    }

    @Override
    public InputStream openStream()
    {
        return icon.asInputStream();
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
        return icon == null || icon.getSize() == 0;
    }

    @Override
    public long size()
    {
        return isEmpty() ? 0 : icon.getSize();
    }

    @Override
    public byte[] read()
        throws IOException
    {
        return ByteStreams.toByteArray( icon.asInputStream() );
    }

    @Override
    public <T> T read( ByteProcessor<T> processor )
        throws IOException
    {
        processor.processBytes( read(), 0, icon.getSize() );
        return processor.getResult();
    }

    @Override
    public String toString()
    {
        return "IconStream(" + size() + ")";
    }
}
