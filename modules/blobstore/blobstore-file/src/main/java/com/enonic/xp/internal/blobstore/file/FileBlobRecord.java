package com.enonic.xp.internal.blobstore.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final FileBlobRecord that = (FileBlobRecord) o;

        if ( key != null ? !key.equals( that.key ) : that.key != null )
        {
            return false;
        }
        return file != null ? file.equals( that.file ) : that.file == null;
    }

    @Override
    public int hashCode()
    {
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + ( file != null ? file.hashCode() : 0 );
        return result;
    }
}
