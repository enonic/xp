package com.enonic.xp.web.sse;

import java.util.UUID;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.util.GenericValue;

import static java.util.Objects.requireNonNull;

/**
 * A lifecycle event delivered by the SSE infrastructure to an {@link SseEndpoint}.
 *
 * <p>Dispatched when the client connects ({@link SseEventType#OPEN}), disconnects
 * ({@link SseEventType#CLOSE}), the async context times out ({@link SseEventType#TIMEOUT}),
 * or an I/O error occurs ({@link SseEventType#ERROR}).</p>
 */
@NullMarked
public final class SseEvent
{
    private final SseEventType type;

    private final UUID clientId;

    private final @Nullable String lastEventId;

    private final GenericValue attributes;

    private final @Nullable Throwable error;

    private SseEvent( final Builder builder )
    {
        this.type = requireNonNull( builder.type, "type is required" );
        this.clientId = requireNonNull( builder.clientId, "clientId is required" );
        this.lastEventId = builder.lastEventId;
        this.attributes = requireNonNull( builder.attributes, "attributes is required" );
        this.error = builder.error;
    }

    /**
     * Returns the lifecycle event type.
     */
    public SseEventType getType()
    {
        return this.type;
    }

    /**
     * Returns the server-generated id of the SSE connection this event belongs to.
     */
    public UUID getClientId()
    {
        return this.clientId;
    }

    /**
     * Returns the value of the {@code Last-Event-ID} HTTP request header sent by the client on reconnection,
     * or {@code null} if absent. Typically only meaningful for {@link SseEventType#OPEN} events.
     */
    public @Nullable String getLastEventId()
    {
        return this.lastEventId;
    }

    /**
     * Returns the per-connection attributes declared in {@link SseConfig}. The same instance is exposed to
     * every event fired for this connection, so endpoints can share setup-time state between OPEN and the
     * later CLOSE / TIMEOUT / ERROR events.
     */
    public GenericValue getAttributes()
    {
        return this.attributes;
    }

    /**
     * Returns the underlying {@link Throwable} for {@link SseEventType#ERROR} events, or {@code null} for
     * other event types.
     */
    public @Nullable Throwable getError()
    {
        return this.error;
    }

    /**
     * Starts a new builder.
     */
    public static Builder create()
    {
        return new Builder();
    }

    /**
     * Builder for {@link SseEvent}. {@code clientId} and {@code attributes} are required; all other fields
     * are optional.
     */
    public static final class Builder
    {
        private SseEventType type = SseEventType.OPEN;

        private @Nullable UUID clientId;

        private @Nullable String lastEventId;

        private @Nullable GenericValue attributes;

        private @Nullable Throwable error;

        private Builder()
        {
        }

        public Builder type( final SseEventType type )
        {
            this.type = type;
            return this;
        }

        public Builder clientId( final UUID clientId )
        {
            this.clientId = clientId;
            return this;
        }

        public Builder lastEventId( final @Nullable String lastEventId )
        {
            this.lastEventId = lastEventId;
            return this;
        }

        public Builder attributes( final GenericValue attributes )
        {
            this.attributes = attributes;
            return this;
        }

        public Builder error( final @Nullable Throwable error )
        {
            this.error = error;
            return this;
        }

        public SseEvent build()
        {
            return new SseEvent( this );
        }
    }
}
