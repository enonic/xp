package com.enonic.xp.ignite.impl.config;

import org.apache.ignite.configuration.DataStorageConfiguration;

import com.enonic.xp.util.ByteSizeParser;

public class DataStorageConfigFactory
{
    static DataStorageConfiguration create( final IgniteSettings igniteSettings )
    {
        final DataStorageConfiguration config = new DataStorageConfiguration();

        final long bytes = ByteSizeParser.parse( igniteSettings.off_heap_max_size() );

        config.getDefaultDataRegionConfiguration().setMaxSize( bytes );

        return config;
    }

}
