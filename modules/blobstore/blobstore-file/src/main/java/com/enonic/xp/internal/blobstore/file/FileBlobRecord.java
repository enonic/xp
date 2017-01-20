package com.enonic.xp.internal.blobstore.file;

import java.io.File;

import com.google.common.io.ByteSource;
import com.google.common.io.Files;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;


final class FileBlobRecord
    implements BlobRecord
{
    private final BlobKey key;

    private final File file;

    public FileBlobRecord( final BlobKey key, final File file )
    {
        this.key = key;
        this.file = file;
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
}
