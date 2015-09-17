package com.enonic.xp.resource;

import com.enonic.xp.app.ApplicationKey;

public interface ResourceService
{
    Resource getResource( ResourceKey resourceKey );

    ResourceKeys findResourceKeys( ApplicationKey applicationKey, String path, String filePattern, boolean recurse );

    ResourceKeys findFolders(ApplicationKey key, String path);
}
