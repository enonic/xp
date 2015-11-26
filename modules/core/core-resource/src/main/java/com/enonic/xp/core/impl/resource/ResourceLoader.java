package com.enonic.xp.core.impl.resource;

import com.enonic.xp.app.Application;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;

public interface ResourceLoader
{
    Resource getResource( Application app, ResourceKey key );

    ResourceKeys findFolders( Application app, String path );
}
