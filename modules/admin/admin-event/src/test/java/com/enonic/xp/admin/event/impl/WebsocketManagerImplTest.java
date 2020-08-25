package com.enonic.xp.admin.event.impl;

import javax.websocket.RemoteEndpoint;
import javax.websocket.Session;

import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WebsocketManagerImplTest
{
    @Test
    void sendToAll()
    {
        final WebsocketManagerImpl webSocketManager = new WebsocketManagerImpl( Runnable::run );

        final EventEndpoint eventWebSocket1 = new EventEndpoint( webSocketManager );
        final Session session1 = mock( Session.class );
        final RemoteEndpoint.Async remoteEndpoint1 = mock( RemoteEndpoint.Async.class );
        when( session1.isOpen() ).thenReturn( true );
        when( session1.getAsyncRemote() ).thenReturn( remoteEndpoint1 );

        final Session session2 = mock( Session.class );
        final EventEndpoint eventWebSocket2 = new EventEndpoint( webSocketManager );
        final RemoteEndpoint.Async remoteEndpoint2 = mock( RemoteEndpoint.Async.class );
        when( session2.isOpen() ).thenReturn( true );
        when( session2.getAsyncRemote() ).thenReturn( remoteEndpoint2 );

        eventWebSocket1.onOpen( session1, null );

        eventWebSocket2.onOpen( session2, null );

        final String testMessage = "test message";

        webSocketManager.sendToAll( testMessage );

        verify( remoteEndpoint1 ).sendText( eq( testMessage ), notNull() );
        verify( remoteEndpoint2 ).sendText( eq( testMessage ), notNull() );
    }

    @Test
    void unregister()
    {
        final WebsocketManagerImpl webSocketManager = new WebsocketManagerImpl( Runnable::run );

        final EventEndpoint eventWebSocket1 = new EventEndpoint( webSocketManager );
        final Session session1 = mock( Session.class );
        final RemoteEndpoint.Async remoteEndpoint1 = mock( RemoteEndpoint.Async.class );
        when( session1.isOpen() ).thenReturn( true );
        when( session1.getAsyncRemote() ).thenReturn( remoteEndpoint1 );

        eventWebSocket1.onOpen( session1, null );

        final String testMessage = "test message";

        webSocketManager.sendToAll( testMessage );

        eventWebSocket1.onClose( session1, null );

        webSocketManager.sendToAll( testMessage );

        verify( remoteEndpoint1, times( 1 ) ).sendText( eq( testMessage ), notNull() );
    }

}
