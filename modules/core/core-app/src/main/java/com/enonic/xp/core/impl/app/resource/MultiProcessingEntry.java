package com.enonic.xp.core.impl.app.resource;

import java.util.List;

import com.enonic.xp.resource.ResourceKey;

/**
 * A value computed from several resources, possibly of several applications: the resources, their timestamps, checked on every
 * use for an unstable resolver only, and the stamp of every application involved, in the order of {@code applicationKeys}.
 */
record MultiProcessingEntry(Object value, List<ResourceKey> resourceKeys, long[] timestamps, List<CacheStamp> stamps, boolean stable)
{
}
