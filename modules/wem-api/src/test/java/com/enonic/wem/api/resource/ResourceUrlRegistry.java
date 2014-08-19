package com.enonic.wem.api.resource;

import java.io.File;
import java.net.URL;

import com.enonic.wem.api.module.ModuleKey;

public interface ResourceUrlRegistry
{
    public URL getUrl( ResourceKey resourceKey );

    public void register( ModuleKey moduleKey, URL baseUrl );

    public void register( ModuleKey moduleKey, File baseDir );
}
