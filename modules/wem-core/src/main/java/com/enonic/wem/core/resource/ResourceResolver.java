package com.enonic.wem.core.resource;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceKeys;

interface ResourceResolver
{
    public Resource resolve( ResourceKey key );

    public ResourceKeys getChildren( ResourceKey parentKey );
}
