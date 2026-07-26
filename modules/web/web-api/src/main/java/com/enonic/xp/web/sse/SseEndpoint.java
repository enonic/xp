package com.enonic.xp.web.sse;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.app.ApplicationKey;

/**
 * Server-side endpoint for an SSE connection.
 *
 * <p>Implementations are registered with the SSE infrastructure via the SSE manager and receive lifecycle
 * callbacks (open, close, timeout, error) through {@link #onEvent(SseEvent)}. The {@link #getConfig()}
 * method returns the configuration that was associated with the connection at setup time.</p>
 */
@NullMarked
public interface SseEndpoint
{
    /**
     * Invoked by the SSE infrastructure whenever a lifecycle event occurs for this connection.
     *
     * @param event the lifecycle event (open, close, timeout, or error).
     */
    void onEvent( SseEvent event );

    /**
     * Returns the configuration associated with this endpoint at connection setup time.
     *
     * @return the SSE configuration.
     */
    SseConfig getConfig();

    /**
     * The application this connection serves, when known. Connections of a stopped or redeployed
     * application are closed by the server.
     *
     * @return the application, or {@code null} when the connection is not tied to one.
     */
    default @Nullable ApplicationKey getApplication()
    {
        return null;
    }
}
