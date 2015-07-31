package com.enonic.xp.resource;

import java.io.File;
import java.net.URL;

public interface ResourceUrlRegistry
{
    public URL getUrl( ResourceKey resourceKey );

    public ResourceUrlRegistry applicationsDir( File modulesDir );

    public ResourceUrlRegistry modulesClassLoader( ClassLoader modulesClassLoader );
}
