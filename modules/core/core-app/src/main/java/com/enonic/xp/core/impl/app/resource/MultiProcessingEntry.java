package com.enonic.xp.core.impl.app.resource;

import java.util.List;

import com.enonic.xp.app.ApplicationKey;

/**
 * A value computed from several resources of stable resolvers, possibly of several applications: the applications involved, in
 * the order they first appear among the resources, and the stamp of each the value was computed under.
 */
record MultiProcessingEntry(Object value, List<ApplicationKey> applicationKeys, List<CacheStamp> stamps)
{
}
