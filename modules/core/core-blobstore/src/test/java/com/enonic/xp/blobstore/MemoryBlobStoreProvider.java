package com.enonic.xp.blobstore;

import java.util.Map;

import com.google.common.collect.Maps;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.BlobStoreProvider;
import com.enonic.xp.blob.ProviderConfig;
import com.enonic.xp.blob.Segment;

public class MemoryBlobStoreProvider
    implements BlobStoreProvider
{
    final MemoryBlobStore blobStore;

    public MemoryBlobStoreProvider( final MemoryBlobStore blobStore )
    {
        this.blobStore = blobStore;
    }

    @Override
    public BlobStore get()
    {
        return this.blobStore;
    }

    @Override
    public String name()
    {
        return "memory";
    }

    @Override
    public ProviderConfig config()
    {
        return new ProviderConfig()
        {
            @Override
            public Map<Segment, String> segments()
            {
                return Maps.newHashMap();
            }

            @Override
            public String readThroughProvider()
            {
                return null;
            }

            @Override
            public boolean readThroughEnabled()
            {
                return false;
            }

            @Override
            public long readThroughSizeThreshold()
            {
                return 0;
            }

            @Override
            public boolean isValid()
            {
                return true;
            }
        };
    }
}
