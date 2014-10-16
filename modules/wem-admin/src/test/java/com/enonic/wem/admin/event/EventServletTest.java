package com.enonic.wem.admin.event;

import javax.servlet.http.HttpServletRequest;

// import org.eclipse.jetty.websocket.WebSocket;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EventServletTest
{
    private EventServlet eventServlet;

    @Before
    public final void setUp()
        throws Exception
    {
        eventServlet = new EventServlet();
    }

    @Test
    public void sendDataToOpenWebSocket()
        throws Exception
    {
        /*final String protocol = "text";
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final WebSocket.Connection connection = mock( WebSocket.Connection.class );
        when( connection.isOpen() ).thenReturn( true );

        final EventWebSocket webSocket = (EventWebSocket) eventServlet.doWebSocketConnect( req, protocol );

        webSocket.onOpen( connection );

        eventServlet.sendToAll( "Hello" );*/
    }

    @Test
    public void sendDataToClosedWebSocket()
        throws Exception
    {
        /*final HttpServletRequest req = mock( HttpServletRequest.class );
        final WebSocket.Connection connection = mock( WebSocket.Connection.class );
        when( connection.isOpen() ).thenReturn( false );

        final String protocol = "text";
        final EventWebSocket webSocket = (EventWebSocket) eventServlet.doWebSocketConnect( req, protocol );

        webSocket.onOpen( connection );
        webSocket.onClose( -1, "" );

        eventServlet.sendToAll( "Hello" );*/
    }

    @Test
    public void sendDataToMultipleWebSockets()
        throws Exception
    {
        /*final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletRequest req2 = mock( HttpServletRequest.class );
        final WebSocket.Connection connection = mock( WebSocket.Connection.class );
        final WebSocket.Connection connection2 = mock( WebSocket.Connection.class );
        when( connection.isOpen() ).thenReturn( false );
        when( connection2.isOpen() ).thenReturn( false );

        final String protocol = "text";
        final EventWebSocket webSocket = (EventWebSocket) eventServlet.doWebSocketConnect( req, protocol );
        final EventWebSocket webSocket2 = (EventWebSocket) eventServlet.doWebSocketConnect( req2, protocol );

        webSocket.onOpen( connection );
        webSocket2.onOpen( connection );

        eventServlet.sendToAll( "Hello" );*/
    }
}