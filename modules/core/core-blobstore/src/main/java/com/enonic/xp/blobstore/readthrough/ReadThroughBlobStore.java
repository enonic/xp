package com.enonic.xp.blobstore.readthrough;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreException;
import com.enonic.xp.blob.Segment;

public class ReadThroughBlobStore
    implements BlobStore
{
    private BlobStore store;

    private BlobStore readThroughStore;

    private ReadThroughBlobStore( final Builder builder )
    {
        store = builder.store;
        readThroughStore = builder.readThroughStore;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public BlobRecord getRecord( final Segment segment, final BlobKey key )
        throws BlobStoreException
    {
        BlobRecord record = this.readThroughStore.getRecord( segment, key );

        if ( record != null )
        {
            return record;
        }

        record = store.getRecord( segment, key );

        if ( record != null )
        {
            this.readThroughStore.addRecord( segment, record );
        }

        return record;
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final ByteSource in )
        throws BlobStoreException
    {
        final BlobRecord blobRecord = this.store.addRecord( segment, in );

        this.readThroughStore.addRecord( segment, blobRecord );

        return blobRecord;
    }

    @Override
    public BlobRecord addRecord( final Segment segment, final BlobRecord record )
        throws BlobStoreException
    {
        this.store.addRecord( segment, record );
        this.readThroughStore.addRecord( segment, record );
        return record;
    }

    public static final class Builder
    {
        private BlobStore store;

        private BlobStore readThroughStore;

        private Builder()
        {
        }

        public Builder store( final BlobStore val )
        {
            store = val;
            return this;
        }

        public Builder readThroughStore( final BlobStore val )
        {
            readThroughStore = val;
            return this;
        }

        public ReadThroughBlobStore build()
        {
            return new ReadThroughBlobStore( this );
        }
    }
}
