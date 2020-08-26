package com.enonic.xp.repo.impl.dump.blobstore;

import java.io.IOException;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repository.RepositorySegmentUtils;

public abstract class AbstractDumpBlobStore
    implements DumpBlobStore
{
    private final PathRef pathRef;

    public AbstractDumpBlobStore( final PathRef pathRef )
    {
        this.pathRef = pathRef;
    }

    @Override
    public DumpBlobRecord getRecord( final Segment segment, final BlobKey key )
    {
        return new DumpBlobRecord( segment, key, this );
    }

    @Override
    public BlobKey addRecord( final Segment segment, final ByteSource in )
        throws BlobStoreException
    {
        try
        {
            final BlobKey blobKey = BlobKey.from( in );
            writeRecord( segment, blobKey, in );
            return blobKey;
        }
        catch ( final IOException e )
        {
            throw new BlobStoreException( "Failed to add blob", e );
        }
    }

    protected abstract ByteSource getBytes( final Segment segment, final BlobKey key );

    protected abstract ByteSink getByteSink( final Segment segment, final BlobKey key );

    protected abstract void writeRecord( final Segment segment, final BlobKey key, final ByteSource in )
        throws IOException;

    protected PathRef getBlobRef( final Segment segment, final BlobKey key )
    {
        final String id = key.toString();
        return pathRef.resolve( segment.getLevel( RepositorySegmentUtils.BLOB_TYPE_LEVEL ).getValue() ).
            resolve( id.substring( 0, 2 ) ).
            resolve( id.substring( 2, 4 ) ).
            resolve( id.substring( 4, 6 ) ).
            resolve( id );
    }
}
