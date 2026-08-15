package com.enonic.xp.scheduler;

/**
 * How a scheduled job decides when to run.
 */
public enum ScheduleCalendarType
{
    /**
     * Runs on every occurrence of a cron expression. An occurrence arriving while the previous
     * execution is still running is skipped - occurrences are positions in a calendar, and a missed
     * one is missed.
     */
    CRON,

    /**
     * Runs once, at a fixed point in time.
     */
    ONE_TIME,

    /**
     * Runs repeatedly, one duration apart, waiting for the previous execution to finish rather than
     * skipping.
     */
    FIXED_DELAY
}
