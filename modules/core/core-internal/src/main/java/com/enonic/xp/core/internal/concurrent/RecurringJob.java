package com.enonic.xp.core.internal.concurrent;

/**
 * Recurring job to have an ability to cancel further executions.
 */
public interface RecurringJob
{
    /**
     * Cancels recurring job and attempts to interrupt currently running one.
     */
    void cancel();
}
