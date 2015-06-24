package com.enonic.xp.testing.resource;

import java.io.File;
import java.net.URL;

import com.enonic.xp.resource.ResourceKey;

public interface ResourceUrlRegistry
{
    URL getUrl( ResourceKey resourceKey );

    ResourceUrlRegistry modulesDir( File modulesDir );

    ResourceUrlRegistry modulesClassLoader( ClassLoader modulesClassLoader );
}
