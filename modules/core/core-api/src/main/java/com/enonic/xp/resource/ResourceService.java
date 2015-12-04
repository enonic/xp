package com.enonic.xp.resource;

import com.enonic.xp.app.ApplicationKey;

public interface ResourceService
{
    Resource getResource( ResourceKey resourceKey );

    ResourceKeys findFiles( ApplicationKey key, String path, String ext, boolean recursive );

    ResourceKeys findFolders( ApplicationKey key, String path );

    <K, V> V processResource( ResourceProcessor<K, V> processor );
}
