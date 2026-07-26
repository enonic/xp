package com.enonic.xp.portal.impl.websocket;

import java.util.concurrent.atomic.AtomicBoolean;

import org.jspecify.annotations.Nullable;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.web.websocket.WebSocketConfig;
import com.enonic.xp.web.websocket.WebSocketEndpoint;
import com.enonic.xp.web.websocket.WebSocketEvent;
import com.enonic.xp.web.websocket.WebSocketEventType;

public final class WebSocketEndpointImpl
    implements WebSocketEndpoint
{
    /**
     * Pinned to the exact script context that executed the handshake request, so every event of
     * this connection sees the module state the handshake initialized.
     */
    private final ControllerScript script;

    private final WebSocketConfig config;

    private final @Nullable ApplicationKey application;

    private final AtomicBoolean released = new AtomicBoolean();

    public WebSocketEndpointImpl( final WebSocketConfig config, final ControllerScript script,
                                  final @Nullable ApplicationKey application )
    {
        this.config = config;
        this.script = script;
        this.application = application;
    }

    @Override
    public void onEvent( final WebSocketEvent event )
    {
        final WebSocketEventType type = event.getType();
        if ( type == WebSocketEventType.OPEN )
        {
            // the connection now references the handshake context: keep it out of the request
            // pool until the connection ends
            this.script.retain();
            boolean opened = false;
            try
            {
                this.script.onSocketEvent( event );
                opened = true;
            }
            finally
            {
                // the container fails the session on an open-handler error, so ERROR/CLOSE would
                // release anyway — but do not depend on container semantics for pool capacity:
                // release here, and the CAS makes the later terminal event a no-op
                if ( !opened && this.released.compareAndSet( false, true ) )
                {
                    this.script.release();
                }
            }
            return;
        }
        try
        {
            this.script.onSocketEvent( event );
        }
        finally
        {
            // ERROR may or may not be followed by CLOSE: release exactly once on the first
            // terminal event
            if ( ( type == WebSocketEventType.CLOSE || type == WebSocketEventType.ERROR ) && this.released.compareAndSet( false, true ) )
            {
                this.script.release();
            }
        }
    }

    @Override
    public WebSocketConfig getConfig()
    {
        return this.config;
    }

    @Override
    public @Nullable ApplicationKey getApplication()
    {
        return this.application;
    }
}
