package com.enonic.wem.api.resource;

import java.io.File;
import java.net.URL;

public interface ResourceUrlRegistry
{
    public URL getUrl( ResourceKey resourceKey );

    public ResourceUrlRegistry modulesDir( File modulesDir );

    public ResourceUrlRegistry modulesClassLoader( ClassLoader modulesClassLoader );
}
