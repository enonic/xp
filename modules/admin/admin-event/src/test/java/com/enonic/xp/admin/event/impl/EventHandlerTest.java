package com.enonic.xp.admin.event.impl;

import org.junit.Assert;
import org.junit.Test;

import com.squareup.okhttp.Request;
import com.squareup.okhttp.ws.WebSocketCall;
import com.squareup.okhttp.ws.WebSocketListener;

import com.enonic.xp.web.jetty.impl.JettyTestSupport;

public class EventHandlerTest
    extends JettyTestSupport
{
    private EventHandler servlet;

    @Override
    protected void configure()
        throws Exception
    {
        this.servlet = new EventHandler();
        this.servlet.securityEnabled = false;
        addServlet( this.servlet, "/admin/event" );
    }

    private void newWebSocketRequest( final WebSocketListener listener )
    {
        final Request request = new Request.Builder().
            url( "ws://localhost:" + this.server.getPort() + "/admin/event" ).
            build();

        WebSocketCall.create( this.client, request ).enqueue( listener );
    }

    @Test
    public void sendData()
        throws Exception
    {
        final ClientTestListener listener1 = new ClientTestListener();
        final ClientTestListener listener2 = new ClientTestListener();

        newWebSocketRequest( listener1 );
        newWebSocketRequest( listener2 );

        Thread.sleep( 400L );
        this.servlet.sendToAll( "Hello World" );
        Thread.sleep( 400L );

        Assert.assertEquals( "TEXT", listener1.type );
        Assert.assertEquals( "Hello World", listener1.message );

        Assert.assertEquals( "TEXT", listener2.type );
        Assert.assertEquals( "Hello World", listener2.message );
    }
}
