package com.enonic.xp.resource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.vfs.VirtualFile;

public interface ResourceService
{
    Resource getResource( ResourceKey resourceKey );

    ResourceKeys findFiles( ApplicationKey key, String pattern );

    <K, V> V processResource( ResourceProcessor<K, V> processor );

    VirtualFile getVirtualFile( ResourceKey resourceKey );
}
