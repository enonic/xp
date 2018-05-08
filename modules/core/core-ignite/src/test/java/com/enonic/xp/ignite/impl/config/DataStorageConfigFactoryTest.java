package com.enonic.xp.ignite.impl.config;

import org.apache.ignite.configuration.DataStorageConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class DataStorageConfigFactoryTest
{

    private IgniteSettings igniteSettings;

    @Before
    public void setUp()
        throws Exception
    {
        this.igniteSettings = Mockito.mock( IgniteSettings.class );
    }

    @Test
    public void max_size_settings()
        throws Exception
    {
        Mockito.when( this.igniteSettings.off_heap_max_size() ).
            thenReturn( "11MB" );

        final DataStorageConfiguration config = DataStorageConfigFactory.create( this.igniteSettings );

        final long maxSize = config.getDefaultDataRegionConfiguration().getMaxSize();

        assertEquals( 11L * 1024 * 1024, maxSize );
    }
}