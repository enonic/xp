package com.enonic.xp.scheduler;

import java.time.Duration;

import org.jspecify.annotations.NullMarked;

/**
 * A schedule that runs repeatedly, one duration apart. The next execution is planned one duration
 * after the previous one was submitted; if that previous execution is still running when the next
 * falls due, the next one waits for it to finish, so executions never overlap.
 * <p>
 * Nothing is persisted between executions, so a leader change or a restart shifts the next
 * execution in either direction.
 */
@NullMarked
public interface FixedDelayCalendar
    extends ScheduleCalendar
{
    /**
     * The duration between executions.
     */
    Duration getDuration();
}
