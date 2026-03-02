package com.enonic.xp.core.internal;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

/**
 * Utility class for handling {@link Instant} objects with millisecond precision.
 * Provides methods to retrieve the current time or process {@link Instant} objects
 * to ensure millisecond precision.
 */
public final class Millis
{
    static Clock CLOCK_MILLIS = Clock.tickMillis( ZoneOffset.UTC );

    /**
     * Returns the current time instant with milliseconds precision.
     *
     * @return the current time instant with milliseconds precision
     */
    public static Instant now()
    {
        return Instant.now( CLOCK_MILLIS );
    }

    /**
     * Returns an {@link Instant} with millisecond precision based on the provided input.
     * If the input {@code instant} is non-null, it will be truncated to milliseconds precision.
     * Otherwise, the current time with millisecond precision is returned.
     *
     * @param instant an {@link Instant} object that can be null.
     *                If not null, it will be truncated to milliseconds precision.
     * @return a non-null {@link Instant} which is either the input truncated to milliseconds
     * or the current time in milliseconds precision.
     */
    public static @NonNull Instant fromOrElseNow( final @Nullable Instant instant )
    {
        return instant != null ? instant.truncatedTo( ChronoUnit.MILLIS ) : now();
    }

    /**
     * Returns an {@link Instant} with millisecond precision based on the provided input.
     * If the input {@code instant} is null, the method will return null.
     * Otherwise, the input {@code instant} is truncated to milliseconds precision.
     *
     * @param instant an {@link Instant} object that can be null.
     *                If not null, it will be truncated to milliseconds precision.
     * @return a truncated {@link Instant} with millisecond precision if the input is non-null,
     * or null if the input is null.
     */
    public static @Nullable Instant from( final @Nullable Instant instant )
    {
        return instant != null ? instant.truncatedTo( ChronoUnit.MILLIS ) : null;
    }

    private Millis()
    {
    }
}
