package com.enonic.xp.portal.impl.sse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.web.sse.SseEndpoint;
import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.sse.SseEventType;
import com.enonic.xp.web.sse.SseMessage;

final class SseEntryImpl
    implements SseEntry, AsyncListener
{
    private static final Logger LOG = LoggerFactory.getLogger( SseEntryImpl.class );

    private final UUID clientId;

    private final AsyncContext asyncContext;

    private final PrintWriter writer;

    private final SseEndpoint endpoint;

    private final SseRegistry registry;

    private final Set<String> groups = ConcurrentHashMap.newKeySet();

    private final Context contextCopy;

    private final Object writeLock = new Object();

    private final AtomicBoolean errorFired = new AtomicBoolean();

    SseEntryImpl( final UUID clientId, final AsyncContext asyncContext, final PrintWriter writer, final SseEndpoint endpoint,
                  final SseRegistry registry )
    {
        this.clientId = clientId;
        this.asyncContext = asyncContext;
        this.writer = writer;
        this.endpoint = endpoint;
        this.registry = registry;
        this.contextCopy = ContextBuilder.copyOf( ContextAccessor.current() ).build();
    }

    @Override
    public UUID getClientId()
    {
        return this.clientId;
    }

    @Override
    public void addGroup( final String group )
    {
        this.groups.add( group );
    }

    @Override
    public void removeGroup( final String group )
    {
        this.groups.remove( group );
    }

    @Override
    public void sendEvent( final SseMessage message )
    {
        synchronized ( writeLock )
        {
            try
            {
                message.writeTo( writer );
            }
            catch ( IOException e )
            {
                LOG.debug( "Failed to send SSE event to [{}]", this.clientId, e );
                handleWriteError( e );
                return;
            }

            if ( writer.checkError() )
            {
                LOG.debug( "Failed to send SSE event to [{}]", this.clientId );
                handleWriteError( new IOException( "SSE write error" ) );
            }
        }
    }

    private void handleWriteError( final IOException cause )
    {
        fireErrorOnce( cause );
        doClose();
    }

    private void fireErrorOnce( final Throwable cause )
    {
        if ( errorFired.compareAndSet( false, true ) )
        {
            fireEvent( SseEventType.ERROR, cause );
        }
    }

    @Override
    public void close()
    {
        doClose();
    }

    @Override
    public boolean isInGroup( final String group )
    {
        return this.groups.contains( group );
    }

    @Override
    public void onComplete( final AsyncEvent event )
    {
        fireEvent( SseEventType.CLOSE, null );
        this.registry.remove( this );
    }

    @Override
    public void onTimeout( final AsyncEvent event )
    {
        fireEvent( SseEventType.TIMEOUT, null );
        doClose();
    }

    @Override
    public void onError( final AsyncEvent event )
    {
        fireErrorOnce( event.getThrowable() );
        doClose();
    }

    @Override
    public void onStartAsync( final AsyncEvent event )
    {
        // not used
    }

    private void fireEvent( final SseEventType type, final Throwable error )
    {
        final SseEvent event = SseEvent.create()
            .type( type )
            .clientId( this.clientId )
            .attributes( this.endpoint.getConfig().attributes() )
            .error( error )
            .build();

        ContextBuilder.copyOf( contextCopy ).build().runWith( () -> this.endpoint.onEvent( event ) );
    }

    private void doClose()
    {
        this.registry.remove( this );
        doComplete();
    }

    private void doComplete()
    {
        try
        {
            this.asyncContext.complete();
        }
        catch ( Exception e )
        {
            LOG.debug( "Failed to complete async context for SSE [{}]", this.clientId, e );
        }
    }
}
