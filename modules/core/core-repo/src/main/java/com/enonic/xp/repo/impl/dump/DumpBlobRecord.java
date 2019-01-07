package com.enonic.xp.repo.impl.dump;

import java.io.File;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;

public class DumpBlobRecord
    implements BlobRecord
{
    private final BlobKey key;

    private final File file;

    private final long lastModified;

    DumpBlobRecord( final BlobKey key, final File file )
    {
        this.key = key;
        this.file = file;
        this.lastModified = System.currentTimeMillis();
    }

    @Override
    public final BlobKey getKey()
    {
        return this.key;
    }

    @Override
    public long getLength()
    {
        return this.file.length();
    }

    @Override
    public ByteSource getBytes()
    {
        return Files.asByteSource( this.file );
    }

    public ByteSink getByteSink()
    {
        return Files.asByteSink( this.file );
    }

    @Override
    public long lastModified()
    {
        return this.lastModified;
    }
}
