package com.enonic.xp.portal.impl.sse;

import java.util.concurrent.atomic.AtomicBoolean;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.web.sse.SseEndpoint;
import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.sse.SseEventType;
import com.enonic.xp.web.sse.SseConfig;

@NullMarked
public final class SseEndpointImpl
    implements SseEndpoint
{
    private final SseConfig config;

    /**
     * Pinned to the exact script context that executed the subscribing request, so every event
     * of this client sees the module state that request initialized.
     */
    private final ControllerScript script;

    private final AtomicBoolean released = new AtomicBoolean();

    public SseEndpointImpl( final SseConfig config, final ControllerScript script )
    {
        this.config = config;
        this.script = script;
    }

    @Override
    public void onEvent( final SseEvent event )
    {
        final SseEventType type = event.getType();
        if ( type == SseEventType.OPEN )
        {
            // the connection now references the subscribing context: keep it out of the request
            // pool until the connection ends
            this.script.retain();
            boolean opened = false;
            try
            {
                this.script.onSseEvent( event );
                opened = true;
            }
            finally
            {
                // a failed open gets no terminal event (the servlet layer swallows the failure,
                // and the default SSE timeout is infinite): release here or the pin leaks the
                // slot out of the request pool for good
                if ( !opened && this.released.compareAndSet( false, true ) )
                {
                    this.script.release();
                }
            }
            return;
        }
        try
        {
            this.script.onSseEvent( event );
        }
        finally
        {
            // every non-OPEN type (CLOSE/TIMEOUT/ERROR) is terminal — SSE delivers no inbound
            // message events — and TIMEOUT/ERROR may or may not be followed by CLOSE: release
            // exactly once on the first terminal event
            if ( this.released.compareAndSet( false, true ) )
            {
                this.script.release();
            }
        }
    }

    @Override
    public SseConfig getConfig()
    {
        return this.config;
    }
}
