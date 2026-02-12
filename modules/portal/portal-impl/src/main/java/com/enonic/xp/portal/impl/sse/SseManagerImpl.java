package com.enonic.xp.portal.impl.sse;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.UUID;

import org.osgi.service.component.annotations.Component;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.portal.sse.SseEndpoint;
import com.enonic.xp.portal.sse.SseEvent;
import com.enonic.xp.portal.sse.SseEventType;
import com.enonic.xp.portal.sse.SseManager;

@Component(service = SseManager.class)
public final class SseManagerImpl
    implements SseManager
{
    private final SseRegistryImpl registry;

    public SseManagerImpl()
    {
        this.registry = new SseRegistryImpl();
    }

    @Override
    public String setupSse( final HttpServletRequest req, final HttpServletResponse res, final SseEndpoint endpoint )
    {
        res.setContentType( "text/event-stream" );
        res.setCharacterEncoding( "UTF-8" );
        res.setHeader( "Cache-Control", "no-cache" );
        res.setHeader( "Connection", "keep-alive" );
        res.setStatus( HttpServletResponse.SC_OK );
        try
        {
            res.flushBuffer();
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }

        final AsyncContext asyncContext = req.startAsync( req, res );
        asyncContext.setTimeout( 0 );

        final String id = UUID.randomUUID().toString();
        final SseEntryImpl entry = new SseEntryImpl( id, asyncContext, endpoint, this.registry );

        asyncContext.addListener( entry );
        this.registry.add( entry );

        final SseEvent connectEvent = SseEvent.create()
            .type( SseEventType.CONNECT )
            .id( id )
            .data( endpoint.getConfig().getData() )
            .build();

        endpoint.onEvent( connectEvent );

        return id;
    }

    @Override
    public void send( final String id, final String event, final String data, final String eventId )
    {
        final SseEntry entry = this.registry.getById( id );
        if ( entry != null )
        {
            entry.sendEvent( event, data, eventId );
        }
    }

    @Override
    public void sendToGroup( final String group, final String event, final String data )
    {
        this.registry.getByGroup( group ).forEach( e -> e.sendEvent( event, data, null ) );
    }

    @Override
    public void close( final String id )
    {
        final SseEntry entry = this.registry.getById( id );
        if ( entry != null )
        {
            entry.close();
        }
    }

    @Override
    public long getGroupSize( final String group )
    {
        return this.registry.getByGroup( group ).count();
    }

    @Override
    public void addToGroup( final String group, final String id )
    {
        final SseEntry entry = this.registry.getById( id );
        if ( entry != null )
        {
            entry.addGroup( group );
        }
    }

    @Override
    public void removeFromGroup( final String group, final String id )
    {
        final SseEntry entry = this.registry.getById( id );
        if ( entry != null )
        {
            entry.removeGroup( group );
        }
    }
}
