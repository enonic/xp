package com.enonic.xp.core.impl.app.resource;

import java.util.Objects;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.core.impl.app.resolver.RevisionedResource;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;

/**
 * A resource a cached value was computed from, as it was then: whether it existed and the {@link RevisionedResource revision} of
 * its content, as the resolver serving it reported it. The revision is compared, never interpreted. A resource without one (an entry
 * of an immutable bundle, or a missing resource) is current for as long as it exists the same: a bundle replaced is a new
 * {@link BundleStamp}, which is checked before any dependency is.
 */
record Dependency(ResourceKey resourceKey, boolean exists, @Nullable Object revision)
{
    static Dependency of( final Resource resource )
    {
        return new Dependency( resource.getKey(), resource.exists(), revisionOf( resource ) );
    }

    boolean isCurrent( final Resource resource )
    {
        return resource.exists() == exists && Objects.equals( revision, revisionOf( resource ) );
    }

    private static @Nullable Object revisionOf( final Resource resource )
    {
        return resource instanceof RevisionedResource revisioned ? revisioned.revision() : null;
    }
}
