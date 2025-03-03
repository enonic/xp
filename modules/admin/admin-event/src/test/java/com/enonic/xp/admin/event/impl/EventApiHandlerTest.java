package com.enonic.xp.admin.event.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.Session;

import com.enonic.xp.event.Event;
import com.enonic.xp.portal.websocket.WebSocketManager;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.websocket.WebSocketEvent;
import com.enonic.xp.web.websocket.WebSocketEventType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class EventApiHandlerTest
{
    private EventApiHandler instance;

    private WebSocketManager webSocketManager;

    @BeforeEach
    void setup()
    {
        this.webSocketManager = mock( WebSocketManager.class );

        this.instance = new EventApiHandler( this.webSocketManager );
    }

    @Test
    void testHandle()
    {
        WebRequest request = mock( WebRequest.class );
        when( request.getRawRequest() ).thenReturn( mock( HttpServletRequest.class ) );
        when( request.isWebSocket() ).thenReturn( true );

        WebResponse response = instance.handle( request );

        assertEquals( HttpStatus.OK, response.getStatus() );
        assertNotNull( response.getWebSocket() );
        assertEquals( 1, response.getWebSocket().getSubProtocols().size() );
        assertEquals( "text", response.getWebSocket().getSubProtocols().getFirst() );
    }

    @Test
    void testHandleBadRequest()
    {
        WebRequest request = mock( WebRequest.class );
        when( request.getRawRequest() ).thenReturn( mock( HttpServletRequest.class ) );
        when( request.isWebSocket() ).thenReturn( false );

        WebResponse response = instance.handle( request );

        assertEquals( HttpStatus.BAD_REQUEST, response.getStatus() );
    }

    @Test
    void testOnSocketEvent()
    {
        Session session = mock( Session.class );
        when( session.getId() ).thenReturn( "session-id" );

        WebSocketEvent event = mock( WebSocketEvent.class );
        when( event.getType() ).thenReturn( WebSocketEventType.OPEN );
        when( event.getSession() ).thenReturn( session );

        instance.onSocketEvent( event );

        verify( webSocketManager, times( 1 ) ).addToGroup( "com.enonic.xp.admin.event", event.getSession().getId() );
        verifyNoMoreInteractions( webSocketManager );
    }

    @Test
    void testOnSocketEventNotOpen()
    {
        Session session = mock( Session.class );
        when( session.getId() ).thenReturn( "session-id" );

        WebSocketEvent event = mock( WebSocketEvent.class );
        when( event.getType() ).thenReturn( WebSocketEventType.MESSAGE );
        when( event.getSession() ).thenReturn( session );

        instance.onSocketEvent( event );

        verifyNoMoreInteractions( webSocketManager );
    }

    @Test
    void testOnEvent()
    {
        final Event event = mock( Event.class );
        instance.onEvent( event );

        verify( webSocketManager, times( 1 ) ).sendToGroup( eq( "com.enonic.xp.admin.event" ), anyString() );
        verifyNoMoreInteractions( webSocketManager );
    }
}
