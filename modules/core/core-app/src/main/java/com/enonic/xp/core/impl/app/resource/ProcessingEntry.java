package com.enonic.xp.core.impl.app.resource;

import com.enonic.xp.resource.ResourceKey;

/**
 * A value computed from one resource: the resource it was computed from (the key translation is not repeated on a hit), the
 * timestamp of that resource, checked on every use for an unstable resolver only, and the stamp the value was computed under.
 */
record ProcessingEntry(Object value, ResourceKey resourceKey, long timestamp, CacheStamp stamp, boolean stable)
{
}
