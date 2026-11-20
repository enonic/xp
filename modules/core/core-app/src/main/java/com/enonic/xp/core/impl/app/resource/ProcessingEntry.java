package com.enonic.xp.core.impl.app.resource;

import com.enonic.xp.app.ApplicationKey;

/**
 * A value computed from one resource of a stable resolver: the application the resource belongs to and the stamp the value was
 * computed under. While the stamp holds, neither the key translation nor the resource is looked at again.
 */
record ProcessingEntry(Object value, ApplicationKey applicationKey, CacheStamp stamp)
{
}
