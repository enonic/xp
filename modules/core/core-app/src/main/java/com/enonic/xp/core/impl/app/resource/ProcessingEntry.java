package com.enonic.xp.core.impl.app.resource;

import java.util.List;

/**
 * A value computed from one resource: the resource it was computed from (the key translation is not repeated on a hit) and the
 * resources read through the service while computing it, as they were then, checked on every use for an unstable resolver only
 * (empty for a stable one), and the stamp the value was computed under.
 */
record ProcessingEntry(Object value, Dependency resource, List<Dependency> dependencies, CacheStamp stamp, boolean stable)
{
}
