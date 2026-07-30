package com.enonic.xp.resource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.vfs.VirtualFile;

public interface ResourceService
{
    Resource getResource( ResourceKey resourceKey );

    ResourceKeys findFiles( ApplicationKey key, String pattern );

    <K, V> V processResource( ResourceProcessor<K, V> processor );

    /**
     * Processes several resources of an application ("file bundle") into a single cached value.
     * The value is recomputed when the application bundle changes or any of the resources
     * appears, disappears or is modified.
     *
     * @param processor multi-resource processor
     * @return processed value, or null if the processor returned null
     */
    <K, V> V processResources( MultiResourceProcessor<K, V> processor );

    VirtualFile getVirtualFile( ResourceKey resourceKey );
}
