package com.enonic.xp.web.jetty.impl.websocket;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.CloseReason;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.Session;

import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class SessionBoundEndpointTest
{
    private static final Duration THROTTLE = Duration.ofSeconds( 60 );

    private final RecordingEndpoint delegate = new RecordingEndpoint();

    private final WebSocketSessionTracker tracker = mock( WebSocketSessionTracker.class );

    private final EndpointConfig config = mock( EndpointConfig.class );

    @Test
    void registers_and_unregisters_raw_session_and_forwards_raw_session_when_not_accessing()
    {
        final Session session = mock( Session.class );
        final SessionBoundEndpoint endpoint = new SessionBoundEndpoint( this.delegate, this.tracker, () -> true, "s1", null, THROTTLE );

        endpoint.onOpen( session, this.config );
        endpoint.onClose( session, new CloseReason( CloseReason.CloseCodes.NORMAL_CLOSURE, "bye" ) );

        verify( this.tracker ).register( "s1", session );
        verify( this.tracker ).unregister( "s1", session );

        // Without an accessor the delegate sees the raw session in every callback.
        assertThat( this.delegate.opened ).containsExactly( session );
        assertThat( this.delegate.closed ).containsExactly( session );
    }

    @Test
    void delegate_sees_the_same_keepalive_session_in_open_and_close_when_accessing()
    {
        final Session session = mock( Session.class );
        final HttpSession.Accessor accessor = mock( HttpSession.Accessor.class );
        final SessionBoundEndpoint endpoint =
            new SessionBoundEndpoint( this.delegate, this.tracker, () -> true, "s1", accessor, THROTTLE );

        endpoint.onOpen( session, this.config );
        endpoint.onError( session, new RuntimeException( "boom" ) );
        endpoint.onClose( session, new CloseReason( CloseReason.CloseCodes.NORMAL_CLOSURE, "bye" ) );

        final Session effective = this.delegate.opened.get( 0 );
        assertThat( effective ).isInstanceOf( KeepAliveSession.class ).isNotSameAs( session );
        assertThat( this.delegate.errored ).containsExactly( effective );
        assertThat( this.delegate.closed ).containsExactly( effective );

        // Tracking still uses the raw container session.
        verify( this.tracker ).register( "s1", session );
        verify( this.tracker ).unregister( "s1", session );
    }

    @Test
    void does_not_touch_tracker_when_only_accessing()
    {
        final Session session = mock( Session.class );
        final HttpSession.Accessor accessor = mock( HttpSession.Accessor.class );
        final SessionBoundEndpoint endpoint = new SessionBoundEndpoint( this.delegate, this.tracker, null, null, accessor, THROTTLE );

        endpoint.onOpen( session, this.config );
        endpoint.onClose( session, new CloseReason( CloseReason.CloseCodes.NORMAL_CLOSURE, "bye" ) );

        verifyNoInteractions( this.tracker );
        assertThat( this.delegate.opened.get( 0 ) ).isInstanceOf( KeepAliveSession.class );
    }

    @Test
    void closes_socket_with_1008_when_http_session_ended_before_registration()
        throws Exception
    {
        final Session session = mock( Session.class );
        final SessionBoundEndpoint endpoint = new SessionBoundEndpoint( this.delegate, this.tracker, () -> false, "s1", null, THROTTLE );

        endpoint.onOpen( session, this.config );

        // Registration must happen BEFORE the probe: if the session ends after register(), the tracker closes
        // the socket; if it ended before, the probe catches it here. Either order of the race is covered.
        final InOrder inOrder = inOrder( this.tracker, session );
        inOrder.verify( this.tracker ).register( "s1", session );

        final ArgumentCaptor<CloseReason> reason = ArgumentCaptor.forClass( CloseReason.class );
        inOrder.verify( session ).close( reason.capture() );
        assertThat( reason.getValue().getCloseCode() ).isEqualTo( CloseReason.CloseCodes.VIOLATED_POLICY );

        // The delegate saw a normal open; the container will deliver the matching onClose.
        assertThat( this.delegate.opened ).containsExactly( session );
    }

    @Test
    void does_not_close_socket_when_http_session_is_still_valid_at_open()
        throws Exception
    {
        final Session session = mock( Session.class );
        final SessionBoundEndpoint endpoint = new SessionBoundEndpoint( this.delegate, this.tracker, () -> true, "s1", null, THROTTLE );

        endpoint.onOpen( session, this.config );

        verify( session, never() ).close( any( CloseReason.class ) );
    }

    private static final class RecordingEndpoint
        extends Endpoint
    {
        final List<Session> opened = new ArrayList<>();

        final List<Session> closed = new ArrayList<>();

        final List<Session> errored = new ArrayList<>();

        @Override
        public void onOpen( final Session session, final EndpointConfig config )
        {
            this.opened.add( session );
        }

        @Override
        public void onClose( final Session session, final CloseReason closeReason )
        {
            this.closed.add( session );
        }

        @Override
        public void onError( final Session session, final Throwable thr )
        {
            this.errored.add( session );
        }
    }
}
