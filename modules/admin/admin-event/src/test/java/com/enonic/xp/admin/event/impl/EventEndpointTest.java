package com.enonic.xp.admin.event.impl;

import java.util.stream.IntStream;

import javax.websocket.RemoteEndpoint;
import javax.websocket.SendHandler;
import javax.websocket.Session;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

class EventEndpointTest
{
    @Test
    void sendMessage()
    {
        final Session session1 = mock( Session.class );
        final RemoteEndpoint.Async remoteEndpoint1 = mock( RemoteEndpoint.Async.class );
        when( session1.isOpen() ).thenReturn( true );
        when( session1.getAsyncRemote() ).thenReturn( remoteEndpoint1 );

        final String testMessage = "test message";

        final EventEndpoint eventWebSocket = new EventEndpoint( mock( WebsocketManager.class, withSettings().stubOnly() ) );
        eventWebSocket.onOpen( session1, null );
        eventWebSocket.sendMessage( testMessage );

        verify( remoteEndpoint1 ).sendText( eq( testMessage ), notNull() );
    }

    @Test
    void sendMessage_more_than_max_inflight_not_sent()
        throws Exception
    {
        final Session session1 = mock( Session.class );
        final RemoteEndpoint.Async remoteEndpoint1 = mock( RemoteEndpoint.Async.class );
        when( session1.isOpen() ).thenReturn( true );
        when( session1.getAsyncRemote() ).thenReturn( remoteEndpoint1 );

        final String testMessage = "test message";

        final EventEndpoint eventWebSocket = new EventEndpoint( mock( WebsocketManager.class, withSettings().stubOnly() ) );
        eventWebSocket.onOpen( session1, null );

        final int maxInflight = 100_000;
        final int exceededCalls = 100;
        IntStream.rangeClosed( 1, maxInflight + exceededCalls ).parallel().forEach( i -> eventWebSocket.sendMessage( testMessage ) );

        verify( remoteEndpoint1, times( maxInflight ) ).sendText( eq( testMessage ), notNull() );
        verify( session1, times( 1 ) ).close( notNull() );
    }

    @Test
    void sendMessage_more_than_max_inflight_some_sent()
    {
        final Session session1 = mock( Session.class );
        final RemoteEndpoint.Async remoteEndpoint1 = mock( RemoteEndpoint.Async.class );
        when( session1.isOpen() ).thenReturn( true );
        when( session1.getAsyncRemote() ).thenReturn( remoteEndpoint1 );

        final ArgumentCaptor<SendHandler> sendHandlerArgumentCaptor = ArgumentCaptor.forClass( SendHandler.class );
        doNothing().when( remoteEndpoint1 ).sendText( any(), sendHandlerArgumentCaptor.capture() );

        final String testMessage = "test message";

        final EventEndpoint eventWebSocket = new EventEndpoint( mock( WebsocketManager.class, withSettings().stubOnly() ) );
        eventWebSocket.onOpen( session1, null );

        final int maxInflight = 100_000;

        IntStream.rangeClosed( 1, maxInflight / 2 ).parallel().forEach( i -> eventWebSocket.sendMessage( testMessage ) );

        sendHandlerArgumentCaptor.getAllValues().forEach( sh -> sh.onResult( null ) );

        final int exceededCalls = 100;
        IntStream.rangeClosed( 1, maxInflight / 2 + exceededCalls ).parallel().forEach( i -> eventWebSocket.sendMessage( testMessage ) );

        verify( remoteEndpoint1, atLeast( maxInflight ) ).sendText( eq( testMessage ), notNull() );
    }
}