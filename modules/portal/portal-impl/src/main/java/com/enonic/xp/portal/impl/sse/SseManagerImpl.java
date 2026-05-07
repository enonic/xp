package com.enonic.xp.portal.impl.sse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.jspecify.annotations.NullMarked;
import org.osgi.service.component.annotations.Component;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.portal.sse.SseManager;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.sse.SseEndpoint;
import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.sse.SseEventType;
import com.enonic.xp.web.sse.SseMessage;

@NullMarked
@Component(service = SseManager.class)
public final class SseManagerImpl
    implements SseManager
{
    private final SseRegistry registry;

    public SseManagerImpl()
    {
        this.registry = new SseRegistry();
    }

    @Override
    public UUID setupSse( final WebRequest request, final SseEndpoint endpoint )
    {
        final HttpServletRequest rawRequest = request.getRawRequest();

        final AsyncContext asyncContext = rawRequest.startAsync();
        asyncContext.setTimeout( endpoint.getConfig().timeout() );

        final HttpServletResponse response = (HttpServletResponse) asyncContext.getResponse();
        response.setContentType( "text/event-stream" );
        response.setCharacterEncoding( StandardCharsets.UTF_8 );
        response.setHeader( "Cache-Control", "no-store" );
        response.setStatus( HttpServletResponse.SC_OK );

        final PrintWriter writer;
        try
        {
            writer = response.getWriter();
            final long retry = endpoint.getConfig().retry();
            if ( retry >= 0 )
            {
                writer.write( "retry:" + retry + "\n\n" );
            }
            response.flushBuffer();
        }
        catch ( IOException e )
        {
            throw new UncheckedIOException( e );
        }

        final UUID clientId = UUID.randomUUID();
        final SseEntryImpl entry = new SseEntryImpl( clientId, asyncContext, writer, endpoint, this.registry );

        asyncContext.addListener( entry );
        this.registry.add( entry );

        final String lastEventId = rawRequest.getHeader( "Last-Event-ID" );

        final SseEvent connectEvent = SseEvent.create()
            .type( SseEventType.OPEN )
            .clientId( clientId )
            .lastEventId( lastEventId )
            .attributes( endpoint.getConfig().attributes() )
            .build();

        endpoint.onEvent( connectEvent );

        return clientId;
    }

    @Override
    public void send( final UUID clientId, final SseMessage message )
    {
        final SseEntry entry = this.registry.getById( clientId );
        if ( entry != null )
        {
            entry.sendEvent( message );
        }
    }

    @Override
    public void sendToGroup( final String group, final SseMessage message )
    {
        this.registry.getByGroup( group ).forEach( e -> e.sendEvent( message ) );
    }

    @Override
    public void close( final UUID clientId )
    {
        final SseEntry entry = this.registry.getById( clientId );
        if ( entry != null )
        {
            entry.close();
        }
    }

    @Override
    public boolean isOpen( final UUID clientId )
    {
        return this.registry.getById( clientId ) != null;
    }

    @Override
    public int getGroupSize( final String group )
    {
        return Math.toIntExact( this.registry.getByGroup( group ).count() );
    }

    @Override
    public void addToGroup( final String group, final UUID clientId )
    {
        final SseEntry entry = this.registry.getById( clientId );
        if ( entry != null )
        {
            entry.addGroup( group );
        }
    }

    @Override
    public void removeFromGroup( final String group, final UUID clientId )
    {
        final SseEntry entry = this.registry.getById( clientId );
        if ( entry != null )
        {
            entry.removeGroup( group );
        }
    }
}
