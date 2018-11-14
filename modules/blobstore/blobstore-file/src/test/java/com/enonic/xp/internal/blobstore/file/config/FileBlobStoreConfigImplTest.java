package com.enonic.xp.internal.blobstore.file.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.google.common.collect.Maps;

import com.enonic.xp.blob.Segment;

import static org.junit.Assert.*;

public class FileBlobStoreConfigImplTest
{

    @Test
    public void default_values()
        throws Exception
    {
        final FileBlobStoreConfigImpl config = new FileBlobStoreConfigImpl();
        config.activate( Maps.newHashMap() );

        assertFalse( config.readThroughEnabled() );
        assertEquals( new File( "${xp.home}/repo/blob" ), config.baseDir() );
        assertEquals( 104857600, config.readThroughSizeThreshold() );
    }


    @Test
    public void trim_space()
        throws Exception
    {
        final HashMap<String, String> values = Maps.newHashMap();
        values.put( "readThrough.enabled", "true " );

        final FileBlobStoreConfigImpl config = new FileBlobStoreConfigImpl();

        config.activate( values );

        assertTrue( config.readThroughEnabled() );
    }
}