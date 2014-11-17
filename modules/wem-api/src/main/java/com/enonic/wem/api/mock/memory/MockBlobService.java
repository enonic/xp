package com.enonic.wem.api.mock.memory;

import java.io.InputStream;
import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.wem.api.blob.Blob;
import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.blob.BlobService;

public final class MockBlobService
    implements BlobService
{
    private final Map<BlobKey, Blob> blobs;

    public MockBlobService()
    {
        this.blobs = Maps.newHashMap();
    }

    @Override
    public Blob create( final InputStream in )
    {
        final MemoryBlob blob = MemoryBlob.create( in );
        this.blobs.put( blob.getKey(), blob );
        return blob;
    }

    @Override
    public Blob get( final BlobKey key )
    {
        return this.blobs.get( key );
    }
}
