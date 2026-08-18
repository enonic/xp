package com.enonic.xp.impl.scheduler;

import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.scheduler.ScheduledJob;

/**
 * A scheduled job without run metadata, together with the node version it was read from.
 * The version id acts as an etag: run metadata cached for it stays valid until the job node changes,
 * because updating run metadata does not create a new node version.
 */
record ScheduledJobEntry(ScheduledJob job, NodeVersionId versionId)
{
}
