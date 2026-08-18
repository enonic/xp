package com.enonic.xp.scheduler;

import java.time.Duration;

import org.jspecify.annotations.NullMarked;

/**
 * A schedule that runs repeatedly at a fixed rate: each execution is planned one duration after the
 * previous one started, so the duration is the interval between starts rather than a gap between
 * runs. An execution still running when the next one falls due delays it, and the one that was held
 * then starts as soon as its predecessor finishes.
 * <p>
 * Holding an execution back is best effort rather than a guarantee. It rests on the members
 * agreeing which of them schedules, and on the running execution still being known to whichever
 * one does, so a network partition, a task whose record has been lost, and a submission whose
 * outcome was never learned can each still leave two running at once.
 * <p>
 * Periods missed that way are dropped rather than replayed, and the rate is measured from the last
 * execution that actually started, so an execution that overruns shifts every later one along with
 * it. Nothing is persisted between executions either, so a leader change or a restart shifts the
 * next execution in either direction.
 */
@NullMarked
public interface FixedRateCalendar
    extends ScheduleCalendar
{
    /**
     * The interval between the starts of two consecutive executions.
     */
    Duration getDuration();
}
