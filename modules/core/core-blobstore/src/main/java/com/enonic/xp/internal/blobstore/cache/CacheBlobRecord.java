package com.enonic.xp.internal.blobstore.cache;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;

public class CacheBlobRecord
    implements BlobRecord
{
    private final BlobKey blobKey;

    private byte[] content;

    private final static Logger LOG = LoggerFactory.getLogger( CacheBlobRecord.class );

    public CacheBlobRecord( final BlobKey blobKey, final ByteSource source )
    {
        this.blobKey = blobKey;

        try (final InputStream stream = source.openStream())
        {
            this.content = ByteStreams.toByteArray( stream );
        }
        catch ( IOException e )
        {
            LOG.error( "Could not create cache blob-record", e );
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

        final CacheBlobRecord that = (CacheBlobRecord) o;

        return blobKey != null ? blobKey.equals( that.blobKey ) : that.blobKey == null;

    }

    @Override
    public int hashCode()
    {
        return blobKey != null ? blobKey.hashCode() : 0;
    }

    @Override
    public BlobKey getKey()
    {
        return this.blobKey;
    }

    @Override
    public long getLength()
    {
        return content.length;
    }

    @Override
    public ByteSource getBytes()
    {
        return ByteSource.wrap( content );
    }
}
