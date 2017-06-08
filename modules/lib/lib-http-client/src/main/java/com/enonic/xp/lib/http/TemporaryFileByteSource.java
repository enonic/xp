package com.enonic.xp.lib.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.common.io.ByteSource;

final class TemporaryFileByteSource
    extends ByteSource
{
    private final File file;

    TemporaryFileByteSource( final File file )
    {
        this.file = file;
    }

    @Override
    public long size()
        throws IOException
    {
        if ( !file.isFile() )
        {
            throw new FileNotFoundException( file.toString() );
        }
        return file.length();
    }

    @Override
    public boolean isEmpty()
        throws IOException
    {
        return size() == 0;
    }

    @Override
    public FileInputStream openStream()
        throws IOException
    {
        return new TemporaryFileInputStream( file );
    }

    @Override
    public String toString()
    {
        return "TemporaryFileByteSource(" + file + ")";
    }

}