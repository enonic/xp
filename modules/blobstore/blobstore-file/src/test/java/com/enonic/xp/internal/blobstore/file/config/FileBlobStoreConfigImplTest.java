package com.enonic.xp.internal.blobstore.file.config;

import java.nio.file.Path;
import java.util.HashMap;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileBlobStoreConfigImplTest
{

    @Test
    void default_values()
    {
        final FileBlobStoreConfigImpl config = new FileBlobStoreConfigImpl();
        config.activate( new HashMap<>() );

        assertFalse( config.readThroughEnabled() );
        assertEquals( Path.of( "${xp.home}/repo/blob" ), config.baseDir() );
        assertEquals( 104857600, config.readThroughSizeThreshold() );
    }


    @Test
    void trim_space()
    {
        final HashMap<String, String> values = new HashMap<>();
        values.put( "readThrough.enabled", "true " );

        final FileBlobStoreConfigImpl config = new FileBlobStoreConfigImpl();

        config.activate( values );

        assertTrue( config.readThroughEnabled() );
    }
}
