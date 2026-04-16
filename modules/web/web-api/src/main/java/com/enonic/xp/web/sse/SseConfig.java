package com.enonic.xp.web.sse;

import java.util.Objects;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.util.GenericValue;

/**
 * Configuration for a Server-Sent Events stream.
 *
 * @param attributes Per-connection state exposed to every {@link com.enonic.xp.web.sse.SseEvent}. Must be a {@code GenericValue.Type.OBJECT}.
 * @param retry      Client-side reconnection hint (milliseconds) sent to the browser as the initial {@code retry:} field. A negative value is not sent (client uses its own default).
 * @param timeout    Async context timeout in milliseconds. A value of zero or less indicates no timeout.
 */
@NullMarked
public record SseConfig(GenericValue attributes, long retry, long timeout)
{
    public SseConfig
    {
        Objects.requireNonNull( attributes, "attributes is required" );
        if ( attributes.getType() != GenericValue.Type.OBJECT )
        {
            throw new IllegalArgumentException( "attributes must be a GenericValue OBJECT" );
        }
    }

    /**
     * A default config: empty attributes object, no {@code retry:} hint sent to the client, and infinite async context timeout.
     *
     * @return an {@code SseConfig} with defaults suitable for a plain SSE endpoint.
     */
    public static SseConfig empty()
    {
        return new SseConfig( GenericValue.newObject().build(), -1, 0 );
    }
}
