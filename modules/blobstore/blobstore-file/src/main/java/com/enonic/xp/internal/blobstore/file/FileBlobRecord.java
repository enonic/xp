package com.enonic.xp.internal.blobstore.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import com.google.common.io.ByteSource;
import com.google.common.io.MoreFiles;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;

final class FileBlobRecord
    implements BlobRecord
{
    private final BlobKey key;

    private final Path file;

    FileBlobRecord( final BlobKey key, final Path file )
    {
        this.key = Objects.requireNonNull( key );
        this.file = Objects.requireNonNull( file );
    }

    @Override
    public BlobKey getKey()
    {
        return this.key;
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
            return 0;
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

    @Override
    public boolean equals( final Object o )
    {
        if ( !( o instanceof FileBlobRecord ) )
        {
            return false;
        }

        final FileBlobRecord that = (FileBlobRecord) o;

        return key.equals( that.key ) && file.equals( that.file );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( key, file );
    }
}
