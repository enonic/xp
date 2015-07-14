package com.enonic.xp.resource;

import com.enonic.xp.module.ModuleKey;

public interface ResourceService
{
    Resource getResource( ResourceKey resourceKey );

    Resources findResources( ModuleKey moduleKey, String path, String filePattern );
}
