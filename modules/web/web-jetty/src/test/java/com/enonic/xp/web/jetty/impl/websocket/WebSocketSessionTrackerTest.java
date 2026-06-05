package com.enonic.xp.web.jetty.impl.websocket;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.websocket.CloseReason;
import jakarta.websocket.Session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebSocketSessionTrackerTest
{
    private final WebSocketSessionTracker tracker = new WebSocketSessionTracker();

    private static HttpSessionEvent sessionEvent( final String id )
    {
        final HttpSession httpSession = mock( HttpSession.class );
        when( httpSession.getId() ).thenReturn( id );
        return new HttpSessionEvent( httpSession );
    }

    @Test
    void sessionDestroyed_closes_all_tracked_sockets_with_1008()
        throws Exception
    {
        final Session ws1 = mock( Session.class );
        final Session ws2 = mock( Session.class );
        tracker.register( "s1", ws1 );
        tracker.register( "s1", ws2 );

        tracker.sessionDestroyed( sessionEvent( "s1" ) );

        final ArgumentCaptor<CloseReason> reason = ArgumentCaptor.forClass( CloseReason.class );
        verify( ws1 ).close( reason.capture() );
        verify( ws2 ).close( reason.capture() );
        assertThat( reason.getAllValues() ).allSatisfy(
            r -> assertThat( r.getCloseCode().getCode() ).isEqualTo( CloseReason.CloseCodes.VIOLATED_POLICY.getCode() ) );
    }

    @Test
    void sessionDestroyed_for_unknown_id_is_noop()
        throws Exception
    {
        final Session ws1 = mock( Session.class );
        tracker.register( "s1", ws1 );

        tracker.sessionDestroyed( sessionEvent( "other" ) );

        verify( ws1, never() ).close( any() );
    }

    @Test
    void unregistered_socket_is_not_closed()
        throws Exception
    {
        final Session ws1 = mock( Session.class );
        tracker.register( "s1", ws1 );
        tracker.unregister( "s1", ws1 );

        tracker.sessionDestroyed( sessionEvent( "s1" ) );

        verify( ws1, never() ).close( any() );
    }

    @Test
    void failure_to_close_one_socket_does_not_block_the_rest()
        throws Exception
    {
        final Session failing = mock( Session.class );
        final Session healthy = mock( Session.class );
        doThrow( new IOException( "boom" ) ).when( failing ).close( any() );
        tracker.register( "s1", failing );
        tracker.register( "s1", healthy );

        tracker.sessionDestroyed( sessionEvent( "s1" ) );

        verify( failing ).close( any() );
        verify( healthy ).close( any() );
    }

    @Test
    void destroying_a_session_clears_tracking_so_a_second_destroy_is_noop()
        throws Exception
    {
        final Session ws1 = mock( Session.class );
        tracker.register( "s1", ws1 );

        tracker.sessionDestroyed( sessionEvent( "s1" ) );
        tracker.sessionDestroyed( sessionEvent( "s1" ) );

        verify( ws1, times( 1 ) ).close( any() );
    }
}
