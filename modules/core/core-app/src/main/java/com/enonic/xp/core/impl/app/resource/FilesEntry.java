package com.enonic.xp.core.impl.app.resource;

import java.util.Set;

/**
 * The files of an application as listed under a stamp.
 */
record FilesEntry(Set<String> files, CacheStamp stamp)
{
}
