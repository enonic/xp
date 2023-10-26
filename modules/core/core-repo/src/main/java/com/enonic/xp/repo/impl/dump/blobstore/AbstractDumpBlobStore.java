package com.enonic.xp.repo.impl.dump.blobstore;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
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
    public void addRecord( final Segment segment, final BlobRecord blobRecord )
    {
        throw new UnsupportedOperationException();
    }

    protected ByteSource getBytes( Segment segment, BlobKey key )
    {
        throw new UnsupportedOperationException();
    }

    protected ByteSink getByteSink( Segment segment, BlobKey key )
    {
        throw new UnsupportedOperationException();
    }

    protected PathRef getBlobRef( final Segment segment, final BlobKey key )
    {
        final String id = key.toString();
        return pathRef.resolve( segment.getLevel( RepositorySegmentUtils.BLOB_TYPE_LEVEL ).getValue() )
            .resolve( id.substring( 0, 2 ) )
            .resolve( id.substring( 2, 4 ) )
            .resolve( id.substring( 4, 6 ) )
            .resolve( id );
    }
}
