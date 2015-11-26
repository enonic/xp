package com.enonic.xp.resource;

import com.enonic.xp.app.ApplicationKey;

public interface ResourceService
{
    Resource getResource( ResourceKey resourceKey );

    ResourceKeys findFolders( ApplicationKey key, String path );
}
