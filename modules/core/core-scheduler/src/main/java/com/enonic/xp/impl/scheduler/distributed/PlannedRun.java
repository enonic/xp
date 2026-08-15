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
 * Instances are shared through Hazelcast, which deserializes them with its own bundle's
 * class loader - hence this package, which the bundle exports.
 */
public record PlannedRun(Instant nextRun, @Nullable String lastTaskId)
    implements Serializable
{
    public PlannedRun
    {
        requireNonNull( nextRun, "nextRun is required" );
    }
}
