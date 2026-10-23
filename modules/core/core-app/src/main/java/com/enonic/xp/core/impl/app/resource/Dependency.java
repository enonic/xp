package com.enonic.xp.core.impl.app.resource;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;

/**
 * A resource a cached value was computed from besides its own, read through the service while it was computed (a fragment a content
 * type inlines), as it was then: an unstable resolver gives no signal of a change but the resource itself.
 */
record Dependency(ResourceKey resourceKey, boolean exists, long timestamp)
{
    static Dependency of( final Resource resource )
    {
        return new Dependency( resource.getKey(), resource.exists(), resource.getTimestamp() );
    }

    boolean isCurrent( final Resource resource )
    {
        return resource.exists() == exists && resource.getTimestamp() == timestamp;
    }
}
