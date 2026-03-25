package com.enonic.xp.internal.blobstore.file;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;

record FileBlobRecord(BlobKey key, Path file)
    implements BlobRecord
{
    FileBlobRecord( final BlobKey key, final Path file )
    {
        this.key = Objects.requireNonNull( key );
        this.file = Objects.requireNonNull( file );
    }

    public BlobKey getKey()
    {
        return key;
    }

    @Override
    public long getLength()
    {
        try
        {
            return Files.size( this.file );
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    @Override
    public ByteSource getBytes()
    {
        return MoreFiles.asByteSource( this.file );
    }

    @Override
    public long lastModified()
    {
        try
        {
            return Files.getLastModifiedTime( this.file ).toMillis();
        }
        catch ( IOException e )
        {
            return 0;
        }
    }
}
