package com.enonic.xp.core.impl.resource;

import com.enonic.xp.app.Application;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;

public interface ResourceLoader
{
    Resource getResource( Application app, ResourceKey key );

    ResourceKeys findFiles( Application app, String path, String ext, boolean recursive );

    ResourceKeys findFolders( Application app, String path );
}
