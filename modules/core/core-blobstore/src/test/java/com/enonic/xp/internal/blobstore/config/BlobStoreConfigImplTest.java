package com.enonic.xp.internal.blobstore.config;

import java.util.HashMap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BlobStoreConfigImplTest
{

    @Test
    public void default_values()
        throws Exception
    {
        final BlobStoreConfigImpl blobStoreConfig = new BlobStoreConfigImpl();
        blobStoreConfig.activate( new HashMap<>() );

        assertEquals( "file", blobStoreConfig.providerName() );
        assertTrue( blobStoreConfig.cache() );
        assertNotNull( blobStoreConfig.cacheSizeThreshold() );
        assertNotNull( blobStoreConfig.memoryCapacity() );
    }


    @Test
    public void trim_whitespaces()
        throws Exception
    {
        final HashMap<String, String> valueMap = new HashMap<>();
        valueMap.put( "cache.enabled", "    false     " );

        final BlobStoreConfigImpl blobStoreConfig = new BlobStoreConfigImpl();
        blobStoreConfig.activate( valueMap );

        assertFalse( blobStoreConfig.cache() );
    }
}
