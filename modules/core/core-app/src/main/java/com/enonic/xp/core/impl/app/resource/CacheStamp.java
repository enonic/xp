package com.enonic.xp.core.impl.app.resource;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.core.impl.app.resolver.ApplicationUrlResolver;

/**
 * What a value derived from the resources of an application was computed under: the incarnation of its bundle ({@code null}
 * for an application without an active bundle) and the {@link ApplicationUrlResolver#revision() revision} of the resolver serving
 * its resources ({@code null} for no resolver, or one with no revision of its own). Either moving makes the value stale.
 */
record CacheStamp(@Nullable BundleStamp bundle, @Nullable Object revision)
{
}
