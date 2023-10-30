package com.enonic.xp.repo.impl.dump.blobstore;

import java.io.IOException;
import java.io.OutputStream;

import com.google.common.io.ByteSink;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repository.RepositorySegmentUtils;

public abstract class AbstractDumpBlobStore
    implements DumpBlobStore
{
    private final PathRef pathRef;

    private final BlobStore sourceBlobStore;

    public AbstractDumpBlobStore( final PathRef pathRef, final BlobStore sourceBlobStore )
    {
        this.pathRef = pathRef;
        this.sourceBlobStore = sourceBlobStore;
    }

    @Override
    public DumpBlobRecord getRecord( final Segment segment, final BlobKey key )
    {
        return new DumpBlobRecord( segment, key, this );
    }

    @Override
    public void addRecord( final BlobContainer blobContainer )
    {
        throw new UnsupportedOperationException();
    }

    protected ByteSource getBytes( final BlobReference reference )
    {
        throw new UnsupportedOperationException();
    }

    protected ByteSink getByteSink( final BlobReference reference )
    {
        throw new UnsupportedOperationException();
    }

    protected PathRef getBlobPathRef( final BlobReference reference )
    {
        final String id = reference.getKey().toString();
        return pathRef.resolve( reference.getSegment().getLevel( RepositorySegmentUtils.BLOB_TYPE_LEVEL ).getValue() )
            .resolve( id.substring( 0, 2 ) )
            .resolve( id.substring( 2, 4 ) )
            .resolve( id.substring( 4, 6 ) )
            .resolve( id );
    }

    void copyBlob( final BlobContainer blobContainer, final OutputStream outputStream )
        throws IOException
    {
        if ( blobContainer instanceof BlobHolder )
        {
            ( (BlobHolder) blobContainer ).copyTo( outputStream );
        }
        else
        {
            final BlobReference blobRef = (BlobReference) blobContainer;
            sourceBlobStore.getRecord( blobRef.getSegment(), blobRef.getKey() ).getBytes().copyTo( outputStream );
        }
    }
}
