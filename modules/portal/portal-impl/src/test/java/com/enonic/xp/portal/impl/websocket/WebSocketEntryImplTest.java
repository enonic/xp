package com.enonic.xp.portal.impl.websocket;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.websocket.RemoteEndpoint;
import jakarta.websocket.Session;

import com.enonic.xp.trace.TestTrace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketEndpoint;
import com.enonic.xp.web.websocket.WebSocketEventType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebSocketEntryImplTest
{
    private WebSocketEndpoint endpoint;

    private WebSocketRegistry registry;

    private Session session;

    private RemoteEndpoint.Async asyncRemote;

    @BeforeEach
    void setUp()
    {
        final WebSocketConfig config = new WebSocketConfig();
        config.setSubProtocols( List.of( "chat", "v2" ) );

        this.endpoint = mock( WebSocketEndpoint.class );
        when( this.endpoint.getConfig() ).thenReturn( config );

        this.registry = mock( WebSocketRegistry.class );

        this.asyncRemote = mock( RemoteEndpoint.Async.class );
        this.session = mock( Session.class );
        when( this.session.getId() ).thenReturn( "session-1" );
        when( this.session.getAsyncRemote() ).thenReturn( this.asyncRemote );
    }

    @Test
    void ctorRecordsWebsocketAttribute()
    {
        final TestTrace ctorTrace = TestTrace.of( "portalRequest" );
        ctorTrace.attribute( "app", "com.enonic.app.myapp" );

        Tracer.trace( ctorTrace, () -> new WebSocketEntryImpl( this.endpoint, this.registry ) );

        assertEquals( List.of( "chat", "v2" ), ctorTrace.get( "websocket" ) );
    }

    @Test
    void sendMessageRecordsTraceAttributes()
    {
        final TestTrace ctorTrace = TestTrace.of( "portalRequest" );
        ctorTrace.attribute( "app", "com.enonic.app.myapp" );

        final WebSocketEntryImpl entry = Tracer.trace( ctorTrace, () -> new WebSocketEntryImpl( this.endpoint, this.registry ) );
        entry.onOpen( this.session, null );

        // sendMessageTraced is @Traced (inert outside OSGi), so its withCurrent enrichment needs an ambient trace
        final TestTrace sendTrace = TestTrace.of( "ws" );
        Tracer.trace( sendTrace, () -> entry.sendMessage( "hi" ) );

        assertEquals( "hi", sendTrace.get( "message" ) );
        assertEquals( "message_sent", sendTrace.get( "type" ) );
        assertEquals( "session-1", sendTrace.get( "sessionid" ) );
        assertEquals( ctorTrace.getId(), sendTrace.get( "parentId" ) );
        assertEquals( "com.enonic.app.myapp", sendTrace.get( "app" ) );

        verify( this.asyncRemote ).sendText( "hi" );
    }

    @Test
    void onMessageRecordsTraceAttributes()
    {
        final TestTrace ctorTrace = TestTrace.of( "portalRequest" );
        ctorTrace.attribute( "app", "com.enonic.app.myapp" );

        final WebSocketEntryImpl entry = Tracer.trace( ctorTrace, () -> new WebSocketEntryImpl( this.endpoint, this.registry ) );
        entry.onOpen( this.session, null );

        final TestTrace eventTrace = TestTrace.of( "ws" );
        Tracer.trace( eventTrace, () -> entry.onMessage( "ping" ) );

        assertEquals( "ping", eventTrace.get( "message" ) );
        assertEquals( "message_received", eventTrace.get( "type" ) );
        assertEquals( "session-1", eventTrace.get( "sessionid" ) );
        assertEquals( ctorTrace.getId(), eventTrace.get( "parentId" ) );
        assertEquals( "com.enonic.app.myapp", eventTrace.get( "app" ) );

        verify( this.endpoint ).onEvent( argThat( event -> event.getType() == WebSocketEventType.MESSAGE ) );
    }

    @Test
    void sendMessageWithoutTraceJustDelegates()
    {
        // constructed without a bound trace: the no-trace branch must simply delegate
        final WebSocketEntryImpl entry = new WebSocketEntryImpl( this.endpoint, this.registry );
        entry.onOpen( this.session, null );

        final TestTrace sendTrace = TestTrace.of( "ws" );
        Tracer.trace( sendTrace, () -> entry.sendMessage( "hello" ) );

        assertFalse( sendTrace.containsKey( "message" ) );
        verify( this.asyncRemote ).sendText( "hello" );
    }

    @Test
    void onOpenRegistersAndDispatchesOpenEvent()
    {
        final WebSocketEntryImpl entry = new WebSocketEntryImpl( this.endpoint, this.registry );
        entry.onOpen( this.session, null );

        verify( this.session ).addMessageHandler( entry );
        verify( this.registry ).add( entry );
        verify( this.endpoint ).onEvent( argThat( event -> event.getType() == WebSocketEventType.OPEN ) );
        assertEquals( "session-1", entry.getId() );
    }
}
