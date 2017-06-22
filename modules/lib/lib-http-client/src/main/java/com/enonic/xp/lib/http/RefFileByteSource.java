package com.enonic.xp.lib.http;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

/**
 * Wrapper for FileByteSource that deletes the file when there are no references to it (i.e. it's garbage collected and the finalize method is called).
 */
public final class RefFileByteSource
    extends ByteSource
{
    private final File file;

    private final ByteSource byteSource;

    public RefFileByteSource( final File file )
    {
        this.file = file;
        this.byteSource = Files.asByteSource( file );
    }

    File getFile()
    {
        return this.file;
    }

    @Override
    public byte[] read()
        throws IOException
    {
        return byteSource.read();
    }

    @Override
    public long size()
        throws IOException
    {
        return byteSource.size();
    }

    @Override
    public InputStream openStream()
        throws IOException
    {
        return byteSource.openStream();
    }

    @Override
    protected void finalize()
        throws Throwable
    {
        this.file.delete();
    }
}
