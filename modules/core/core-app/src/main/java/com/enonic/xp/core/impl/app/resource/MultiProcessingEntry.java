package com.enonic.xp.core.impl.app.resource;

import java.util.List;

/**
 * A value computed from several resources, possibly of several applications: the resources and the resources read through the
 * service while computing it, as they were then, checked on every use for an unstable resolver only (empty for a stable one), and
 * the stamp of every application involved, in the order the applications first appear among the resources.
 */
record MultiProcessingEntry(Object value, List<Dependency> resources, List<Dependency> dependencies, List<CacheStamp> stamps,
                            boolean stable)
{
}
