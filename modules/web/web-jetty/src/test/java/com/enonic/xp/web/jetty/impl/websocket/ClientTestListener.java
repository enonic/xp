package com.enonic.xp.web.jetty.impl.websocket;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import com.squareup.okhttp.Response;
import com.squareup.okhttp.ws.WebSocket;
import com.squareup.okhttp.ws.WebSocketListener;

import okio.Buffer;
import okio.BufferedSource;

public final class ClientTestListener
    implements WebSocketListener
{
    protected String type;

    protected String message;

    protected WebSocket socket;

    private final CountDownLatch connectLatch = new CountDownLatch( 1 );

    private final CountDownLatch messageLatch = new CountDownLatch( 1 );

    @Override
    public void onOpen( final WebSocket socket, final Response response )
    {
        this.socket = socket;
        this.connectLatch.countDown();
    }

    @Override
    public void onFailure( final IOException e, final Response response )
    {
        // Do nothing
    }

    @Override
    public void onMessage( final BufferedSource payload, final WebSocket.PayloadType type )
        throws IOException
    {
        this.type = type.name();
        this.message = payload.readByteString().utf8();
        payload.close();
        this.messageLatch.countDown();
    }

    @Override
    public void onPong( final Buffer payload )
    {
        // Do nothing
    }

    @Override
    public void onClose( final int code, final String reason )
    {
        this.socket = null;
    }

    void sendMessage( final String message )
        throws Exception
    {
        if ( this.socket != null )
        {
            this.socket.sendMessage( WebSocket.PayloadType.TEXT, new Buffer().writeUtf8( message ) );
        }
    }

    void waitForConnect()
        throws Exception
    {
        if ( !this.connectLatch.await( 120, TimeUnit.SECONDS ) )
        {
            throw new RuntimeException( "Timeout waiting for Connect" );
        }
    }

    void waitForMessage()
        throws Exception
    {
        if ( !this.messageLatch.await( 120, TimeUnit.SECONDS ) )
        {
            throw new RuntimeException( "Timeout waiting for Message" );
        }
    }
}
