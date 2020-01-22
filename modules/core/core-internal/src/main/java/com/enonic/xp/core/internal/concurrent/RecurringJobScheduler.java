package com.enonic.xp.core.internal.concurrent;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * Similar to {@link java.util.concurrent.Executor} but allows to schedule recurring {@link Runnable}
 */
public interface RecurringJobScheduler
{
    /**
     * Schedules recurring job. Returned {@link RecurringJob} should be used to cancel subsequent executions.
     * {@link Error} and {@link Throwable} forcibly prevent further executions,
     * but {@link Exception} does not prevent further executions.
     *
     * @param command          recurring {@link Runnable}
     * @param initialDelay     time to delay first execution
     * @param delay            delay between subsequent executions
     * @param exceptionHandler should be used primarily for logging of uncaught Exceptions
     * @param errorHandler     should be used primarily for logging of uncaught Exceptions before forced termination
     * @return RecurringJob which is possible to cancel
     */
    RecurringJob scheduleWithFixedDelay( Runnable command, Duration initialDelay, Duration delay, Consumer<Exception> exceptionHandler,
                                         Consumer<Throwable> errorHandler );
}
