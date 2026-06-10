package com.enonic.xp.web.jetty.impl.websocket;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;

/**
 * Tracks the WebSocket sessions opened within each HTTP session and closes them with code 1008
 * ({@link CloseReason.CloseCodes#VIOLATED_POLICY}) when that HTTP session ends — either through explicit
 * invalidation (logout) or idle-timeout expiry.
 * <p>
 * The Jakarta WebSocket spec (§7.2) suggests this behavior but Jetty deliberately leaves it to the
 * application, so XP implements it here. Tracking is node-local, which matches the supported sticky
 * load-balancing topology.
 */
@Component(service = WebSocketSessionTracker.class)
public final class WebSocketSessionTracker
    implements HttpSessionListener
{
    private static final Logger LOG = LoggerFactory.getLogger( WebSocketSessionTracker.class );

    private final ConcurrentMap<String, Set<Session>> sessionsByHttpSessionId = new ConcurrentHashMap<>();

    public void register( final String httpSessionId, final Session wsSession )
    {
        this.sessionsByHttpSessionId.computeIfAbsent( httpSessionId, key -> ConcurrentHashMap.newKeySet() ).add( wsSession );
    }

    public void unregister( final String httpSessionId, final Session wsSession )
    {
        this.sessionsByHttpSessionId.computeIfPresent( httpSessionId, ( key, tracked ) -> {
            tracked.remove( wsSession );
            return tracked.isEmpty() ? null : tracked;
        } );
    }

    @Override
    public void sessionDestroyed( final HttpSessionEvent se )
    {
        final Set<Session> tracked = this.sessionsByHttpSessionId.remove( se.getSession().getId() );
        if ( tracked == null )
        {
            return;
        }

        LOG.debug( "Closing {} WebSocket session(s) after HTTP session [{}] ended", tracked.size(), se.getSession().getId() );

        final CloseReason reason = new CloseReason( CloseReason.CloseCodes.VIOLATED_POLICY, "HTTP session ended" );
        for ( final Session wsSession : tracked )
        {
            try
            {
                wsSession.close( reason );
            }
            catch ( Exception e )
            {
                // Swallow per-socket failures so one failing socket cannot block closing the rest.
                LOG.warn( "Failed to close WebSocket session [{}] after HTTP session ended", wsSession.getId(), e );
            }
        }
    }
}
