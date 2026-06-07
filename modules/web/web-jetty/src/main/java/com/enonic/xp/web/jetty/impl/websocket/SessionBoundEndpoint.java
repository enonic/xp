package com.enonic.xp.web.jetty.impl.websocket;

import java.time.Duration;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.CloseReason;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;

/**
 * Wraps an application {@link Endpoint} to bind its WebSocket session to the HTTP session it was opened
 * within. When {@code httpSessionId} is set, the socket is registered with the {@link WebSocketSessionTracker}
 * on open (and unregistered on close) so it gets closed when the HTTP session ends. When
 * {@code sessionAccessor} is set, the delegate endpoint is handed a {@link KeepAliveSession} wrapper so that
 * inbound messages refresh the HTTP session.
 */
final class SessionBoundEndpoint
    extends Endpoint
{
    private final Endpoint delegate;

    private final WebSocketSessionTracker tracker;

    private final String httpSessionId;

    private final HttpSession.Accessor sessionAccessor;

    private final Duration sessionAccessThrottle;

    // A fresh endpoint instance is created per connection, so the effective session opened in onOpen can be
    // remembered here and handed to the delegate again in onClose/onError. Without this the delegate would see
    // the KeepAliveSession wrapper in onOpen but the raw container Session afterwards, breaking endpoints that
    // compare session identity.
    private volatile Session effectiveSession;

    SessionBoundEndpoint( final Endpoint delegate, final WebSocketSessionTracker tracker, final String httpSessionId,
                          final HttpSession.Accessor sessionAccessor, final Duration sessionAccessThrottle )
    {
        this.delegate = delegate;
        this.tracker = tracker;
        this.httpSessionId = httpSessionId;
        this.sessionAccessor = sessionAccessor;
        this.sessionAccessThrottle = sessionAccessThrottle;
    }

    @Override
    public void onOpen( final Session session, final EndpointConfig config )
    {
        if ( this.httpSessionId != null )
        {
            this.tracker.register( this.httpSessionId, session );
        }

        this.effectiveSession =
            this.sessionAccessor != null ? new KeepAliveSession( session, this.sessionAccessor, this.sessionAccessThrottle ) : session;

        this.delegate.onOpen( this.effectiveSession, config );
    }

    @Override
    public void onClose( final Session session, final CloseReason closeReason )
    {
        try
        {
            // Tracking is keyed on the raw container session, the same instance that was registered in onOpen.
            if ( this.httpSessionId != null )
            {
                this.tracker.unregister( this.httpSessionId, session );
            }
        }
        finally
        {
            this.delegate.onClose( effectiveSession( session ), closeReason );
        }
    }

    @Override
    public void onError( final Session session, final Throwable thr )
    {
        this.delegate.onError( effectiveSession( session ), thr );
    }

    private Session effectiveSession( final Session session )
    {
        final Session opened = this.effectiveSession;
        return opened != null ? opened : session;
    }
}
