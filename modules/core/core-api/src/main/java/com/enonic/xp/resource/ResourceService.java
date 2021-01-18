package com.enonic.xp.resource;

import java.util.Optional;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.util.HashCode;

public interface ResourceService
{
    Resource getResource( ResourceKey resourceKey );

    ResourceKeys findFiles( ApplicationKey key, String pattern );

    Optional<HashCode> resourceHash( ResourceKey key );

    <K, V> V processResource( ResourceProcessor<K, V> processor );
}
