package com.enonic.xp.web.jetty.impl.websocket;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.CloseReason;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class KeepAliveSessionTest
{
    private Session delegate;

    private HttpSession.Accessor accessor;

    private KeepAliveSession session;

    @BeforeEach
    void setUp()
    {
        this.delegate = mock( Session.class );
        this.accessor = mock( HttpSession.Accessor.class );
        this.session = new KeepAliveSession( this.delegate, this.accessor, Duration.ofSeconds( 60 ) );
    }

    private MessageHandler.Whole<String> registerWholeHandler( final List<String> received )
    {
        this.session.addMessageHandler( new StringHandler( received ) );

        @SuppressWarnings("unchecked") final ArgumentCaptor<MessageHandler.Whole<String>> captor =
            ArgumentCaptor.forClass( MessageHandler.Whole.class );
        verify( this.delegate ).addMessageHandler( eq( String.class ), captor.capture() );
        return captor.getValue();
    }

    @Test
    void inbound_message_refreshes_session_and_is_forwarded()
    {
        final List<String> received = new ArrayList<>();
        final MessageHandler.Whole<String> wrapped = registerWholeHandler( received );

        wrapped.onMessage( "hello" );

        verify( this.accessor ).access( any() );
        assertThat( received ).containsExactly( "hello" );
    }

    @Test
    void session_refresh_is_throttled()
    {
        final List<String> received = new ArrayList<>();
        final MessageHandler.Whole<String> wrapped = registerWholeHandler( received );

        wrapped.onMessage( "one" );
        wrapped.onMessage( "two" );
        wrapped.onMessage( "three" );

        // All messages are forwarded, but the session is refreshed at most once within the throttle window.
        verify( this.accessor, times( 1 ) ).access( any() );
        assertThat( received ).containsExactly( "one", "two", "three" );
    }

    @Test
    void closes_socket_with_1008_when_session_already_gone()
        throws Exception
    {
        doThrow( new IllegalStateException( "session invalidated" ) ).when( this.accessor ).access( any() );

        final List<String> received = new ArrayList<>();
        final MessageHandler.Whole<String> wrapped = registerWholeHandler( received );

        wrapped.onMessage( "hello" );

        final ArgumentCaptor<CloseReason> reason = ArgumentCaptor.forClass( CloseReason.class );
        verify( this.delegate ).close( reason.capture() );
        assertThat( reason.getValue().getCloseCode().getCode() ).isEqualTo( CloseReason.CloseCodes.VIOLATED_POLICY.getCode() );
        // The message is still forwarded to the application.
        assertThat( received ).containsExactly( "hello" );
    }

    @Test
    void lambda_handler_is_forwarded_unwrapped()
    {
        // The message type of a lambda handler cannot be resolved by reflection, so it is forwarded as-is.
        final MessageHandler.Whole<String> lambda = message -> {
        };
        this.session.addMessageHandler( lambda );

        verify( this.delegate ).addMessageHandler( lambda );
        verify( this.delegate, never() ).addMessageHandler( any( Class.class ), any( MessageHandler.Whole.class ) );
    }

    @Test
    void single_arg_partial_handler_refreshes_session_and_is_forwarded()
    {
        final List<String> received = new ArrayList<>();
        this.session.addMessageHandler( new StringPartialHandler( received ) );

        @SuppressWarnings("unchecked") final ArgumentCaptor<MessageHandler.Partial<String>> captor =
            ArgumentCaptor.forClass( MessageHandler.Partial.class );
        verify( this.delegate ).addMessageHandler( eq( String.class ), captor.capture() );

        captor.getValue().onMessage( "part", true );

        verify( this.accessor ).access( any() );
        assertThat( received ).containsExactly( "part" );
    }

    @Test
    void typed_whole_handler_refreshes_session_and_is_forwarded()
    {
        final List<String> received = new ArrayList<>();
        this.session.addMessageHandler( String.class, (MessageHandler.Whole<String>) received::add );

        @SuppressWarnings("unchecked") final ArgumentCaptor<MessageHandler.Whole<String>> captor =
            ArgumentCaptor.forClass( MessageHandler.Whole.class );
        verify( this.delegate ).addMessageHandler( eq( String.class ), captor.capture() );

        captor.getValue().onMessage( "hi" );

        verify( this.accessor ).access( any() );
        assertThat( received ).containsExactly( "hi" );
    }

    @Test
    void typed_partial_handler_refreshes_session_and_is_forwarded()
    {
        final List<String> received = new ArrayList<>();
        this.session.addMessageHandler( String.class, (MessageHandler.Partial<String>) ( message, last ) -> received.add( message ) );

        @SuppressWarnings("unchecked") final ArgumentCaptor<MessageHandler.Partial<String>> captor =
            ArgumentCaptor.forClass( MessageHandler.Partial.class );
        verify( this.delegate ).addMessageHandler( eq( String.class ), captor.capture() );

        captor.getValue().onMessage( "part", false );

        verify( this.accessor ).access( any() );
        assertThat( received ).containsExactly( "part" );
    }

    @Test
    void delegates_all_session_operations()
        throws Exception
    {
        this.session.getContainer();
        this.session.getMessageHandlers();
        final MessageHandler handler = mock( MessageHandler.class );
        this.session.removeMessageHandler( handler );
        this.session.getProtocolVersion();
        this.session.getNegotiatedSubprotocol();
        this.session.getNegotiatedExtensions();
        this.session.isSecure();
        this.session.isOpen();
        this.session.getMaxIdleTimeout();
        this.session.setMaxIdleTimeout( 123L );
        this.session.setMaxBinaryMessageBufferSize( 10 );
        this.session.getMaxBinaryMessageBufferSize();
        this.session.setMaxTextMessageBufferSize( 20 );
        this.session.getMaxTextMessageBufferSize();
        this.session.getAsyncRemote();
        this.session.getBasicRemote();
        this.session.getId();
        this.session.close();
        final CloseReason reason = new CloseReason( CloseReason.CloseCodes.NORMAL_CLOSURE, "x" );
        this.session.close( reason );
        this.session.getRequestURI();
        this.session.getRequestParameterMap();
        this.session.getQueryString();
        this.session.getPathParameters();
        this.session.getUserProperties();
        this.session.getUserPrincipal();
        this.session.getOpenSessions();

        verify( this.delegate ).getContainer();
        verify( this.delegate ).getMessageHandlers();
        verify( this.delegate ).removeMessageHandler( handler );
        verify( this.delegate ).setMaxIdleTimeout( 123L );
        verify( this.delegate ).setMaxBinaryMessageBufferSize( 10 );
        verify( this.delegate ).setMaxTextMessageBufferSize( 20 );
        verify( this.delegate ).getAsyncRemote();
        verify( this.delegate ).getBasicRemote();
        verify( this.delegate ).getId();
        verify( this.delegate ).close();
        verify( this.delegate ).close( reason );
        verify( this.delegate ).getOpenSessions();
    }

    private static final class StringHandler
        implements MessageHandler.Whole<String>
    {
        private final List<String> received;

        StringHandler( final List<String> received )
        {
            this.received = received;
        }

        @Override
        public void onMessage( final String message )
        {
            this.received.add( message );
        }
    }

    private static final class StringPartialHandler
        implements MessageHandler.Partial<String>
    {
        private final List<String> received;

        StringPartialHandler( final List<String> received )
        {
            this.received = received;
        }

        @Override
        public void onMessage( final String partialMessage, final boolean last )
        {
            this.received.add( partialMessage );
        }
    }
}
