package com.enonic.xp.lib.content.mapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import com.google.common.base.Optional;
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
        this.icon = Objects.requireNonNull( icon );
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
        return icon.getSize() == 0;
    }

    @Override
    public long size()
    {
        return icon.getSize();
    }

    @Override
    public Optional<Long> sizeIfKnown()
    {
        return Optional.of( (long) icon.getSize() );
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
        return "IconStream(" + icon.getSize() + ")";
    }
}
