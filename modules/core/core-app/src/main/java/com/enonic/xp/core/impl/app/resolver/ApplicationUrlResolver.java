package com.enonic.xp.core.impl.app.resolver;

import java.util.Set;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.resource.Resource;

public interface ApplicationUrlResolver
{
    Set<String> findFiles();

    Resource findResource( String path );

    /**
     * Whether the resources served stay the same for as long as the {@link #revision() revision} of the resolver and the bundle of
     * the application do. A stable resolver lets values derived from its resources be cached without re-reading them; an unstable
     * one, serving files that change under it without any signal (the classloader of an application in development), has its
     * resources checked on every use.
     */
    boolean isStable();

    /**
     * The revision of the source the resources are served from, read anew on every call: the version of the application node for
     * resources persisted in the repository. {@code null} when the source has no revision of its own, being immutable (a bundle) or
     * checked resource by resource (a classloader). A revision is compared, never interpreted: nothing about it but its equality
     * means anything to a reader.
     */
    @Nullable
    Object revision();
}
