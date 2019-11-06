package com.enonic.xp.web.jetty.impl.websocket;

import java.net.http.WebSocket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public final class ClientTestListener
    implements WebSocket.Listener
{
    private final CompletableFuture<String> messageFuture = new CompletableFuture<>();

    @Override
    public CompletionStage<?> onText( WebSocket webSocket, CharSequence data, boolean last )
    {
        messageFuture.complete( data.toString() );
        return WebSocket.Listener.super.onText( webSocket, data, last );
    }

    String waitForMessage()
        throws Exception
    {
        return messageFuture.get( 120, TimeUnit.SECONDS );
    }
}
