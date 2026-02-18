package com.enonic.xp.portal.impl.sse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.AsyncEvent;
import jakarta.servlet.AsyncListener;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.portal.sse.SseEndpoint;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.sse.SseEvent;
import com.enonic.xp.portal.sse.SseEventType;

final class SseEntryImpl
    implements SseEntry, AsyncListener
{
    private static final Logger LOG = LoggerFactory.getLogger( SseEntryImpl.class );

    private final String id;

    private final AsyncContext asyncContext;

    private final SseEndpoint endpoint;

    private final SseRegistry registry;

    private final Set<String> groups = ConcurrentHashMap.newKeySet();

    private final Context contextCopy;

    SseEntryImpl( final String id, final AsyncContext asyncContext, final SseEndpoint endpoint, final SseRegistry registry )
    {
        this.id = id;
        this.asyncContext = asyncContext;
        this.endpoint = endpoint;
        this.registry = registry;
        this.contextCopy = ContextBuilder.copyOf( ContextAccessor.current() ).build();
    }

    @Override
    public String getId()
    {
        return this.id;
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
    public void sendEvent( final String event, final String data, final String eventId )
    {
        try
        {
            final HttpServletResponse response = (HttpServletResponse) this.asyncContext.getResponse();
            final PrintWriter writer = response.getWriter();

            if ( eventId != null )
            {
                writer.write( "id:" + eventId + "\n" );
            }
            if ( event != null )
            {
                writer.write( "event:" + event + "\n" );
            }
            for ( final String line : data.split( "\n", -1 ) )
            {
                writer.write( "data:" + line + "\n" );
            }
            writer.write( "\n" );
            writer.flush();
        }
        catch ( IOException e )
        {
            LOG.debug( "Failed to send SSE event to [{}]", this.id, e );
            doClose();
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
        fireEvent( SseEventType.CLOSE );
        this.registry.remove( this );
    }

    @Override
    public void onTimeout( final AsyncEvent event )
    {
        fireEvent( SseEventType.CLOSE );
        this.registry.remove( this );
        doComplete();
    }

    @Override
    public void onError( final AsyncEvent event )
    {
        fireEvent( SseEventType.ERROR, event.getThrowable() );
        this.registry.remove( this );
        doComplete();
    }

    @Override
    public void onStartAsync( final AsyncEvent event )
    {
        // not used
    }

    private void fireEvent( final SseEventType type )
    {
        fireEvent( type, null );
    }

    private void fireEvent( final SseEventType type, final Throwable error )
    {
        final SseEvent event = SseEvent.create()
            .type( type )
            .id( this.id )
            .data( this.endpoint.getConfig().getData() )
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
            LOG.debug( "Failed to complete async context for SSE [{}]", this.id, e );
        }
    }
}
