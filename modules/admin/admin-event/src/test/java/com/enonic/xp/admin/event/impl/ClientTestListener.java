package com.enonic.xp.admin.event.impl;

import java.io.IOException;

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

    @Override
    public void onOpen( final WebSocket webSocket, final Response response )
    {
        // Do nothing
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
    }

    @Override
    public void onPong( final Buffer payload )
    {
        // Do nothing
    }

    @Override
    public void onClose( final int code, final String reason )
    {
        // Do nothing
    }
}
