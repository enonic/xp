package com.enonic.xp.core.impl.app.resource;

import org.jspecify.annotations.Nullable;

/**
 * What a value derived from the resources of an application was computed under: the incarnation of its bundle ({@code null}
 * for an application without an active bundle) and its generation on this node. Either moving makes the value stale.
 */
record CacheStamp(@Nullable BundleStamp bundle, long generation)
{
}
