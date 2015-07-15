package com.enonic.xp.resource;

import com.enonic.xp.app.ApplicationKey;

public interface ResourceService
{
    Resource getResource( ResourceKey resourceKey );

    Resources findResources( ApplicationKey applicationKey, String path, String filePattern );
}
