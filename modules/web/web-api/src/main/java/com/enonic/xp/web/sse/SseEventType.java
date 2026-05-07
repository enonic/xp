package com.enonic.xp.web.sse;

import java.util.Locale;

/**
 * Lifecycle event type delivered to an {@link SseEndpoint}.
 */
public enum SseEventType
{
    /**
     * The SSE connection has been established and the response headers have been sent.
     */
    OPEN,

    /**
     * The SSE connection has been closed (client disconnected or server called complete).
     */
    CLOSE,

    /**
     * The async context reached its configured timeout before the connection closed normally.
     */
    TIMEOUT,

    /**
     * An I/O or processing error occurred on the SSE connection. The cause is available via
     * {@link SseEvent#getError()}.
     */
    ERROR;

    private final String value;

    SseEventType()
    {
        this.value = name().toLowerCase( Locale.ROOT );
    }

    /**
     * Returns the lowercase string representation of this event type (e.g. {@code "open"}).
     */
    public String value()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return value;
    }
}
