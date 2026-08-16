package com.enonic.xp.impl.scheduler.distributed;

import java.io.Serializable;
import java.time.Instant;

import org.jspecify.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Shared state of a job's planned execution: when the next run is due and which task
 * the previous run submitted. For a one-time job {@code nextRun} marks the only
 * execution as submitted. {@code lastTaskId} is null when no submitted task is known,
 * e.g. after giving up on a failing job.
 * <p>
 * {@code versionId} is the version of the job this plan was made for. A modified job is a
 * different schedule and plans itself afresh, so a plan is ignored once the job has moved on -
 * which is what re-arms a modified job, whether or not its old plan could be discarded.
 * <p>
 * Instances are shared through Hazelcast, which deserializes them with its own bundle's
 * class loader - hence this package, which the bundle exports.
 */
public record PlannedRun(Instant nextRun, @Nullable String lastTaskId, @Nullable String versionId)
    implements Serializable
{
    public PlannedRun
    {
        requireNonNull( nextRun, "nextRun is required" );
    }
}
