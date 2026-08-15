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
     * Runs repeatedly at a fixed interval between starts. An execution that is still running when
     * the next falls due delays it rather than being joined by it, and the periods missed while it
     * runs are dropped rather than replayed.
     */
    FIXED_RATE
}
