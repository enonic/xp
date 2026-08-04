package com.enonic.xp.portal.impl.sse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.app.Application;
import com.enonic.xp.portal.sse.SseManager;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.sse.SseEndpoint;
import com.enonic.xp.web.sse.SseEvent;
import com.enonic.xp.web.sse.SseEventType;
import com.enonic.xp.web.sse.SseMessage;

@NullMarked
@Component(service = SseManager.class)
public final class SseManagerImpl
    implements SseManager, ServiceTrackerCustomizer<Application, Application>
{
    private final SseRegistry registry;

    private final BundleContext context;

    private final ServiceTracker<Application, Application> tracker;

    @Activate
    public SseManagerImpl( final BundleContext context )
    {
        this.registry = new SseRegistry();
        this.context = context;
        this.tracker = new ServiceTracker<>( context, Application.class, this );
        this.tracker.open();
    }

    @Deactivate
    public void deactivate()
    {
        this.tracker.close();
    }

    @Override
    public @Nullable Application addingService( final ServiceReference<Application> reference )
    {
        return this.context.getService( reference );
    }

    @Override
    public void modifiedService( final ServiceReference<Application> reference, final Application application )
    {
    }

    @Override
    public void removedService( final ServiceReference<Application> reference, final Application application )
    {
        // the application stopped or is being redeployed: its connections dispatch to a script
        // context of the gone incarnation and would otherwise linger until the client disconnects
        // (the default SSE timeout is infinite) — close them, so clients reconnect to the successor
        final List<SseEntry> entries = this.registry.getByApplication( application.getKey() ).toList();
        entries.forEach( SseEntry::close );
        this.context.ungetService( reference );
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

        try
        {
            endpoint.onEvent( connectEvent );
        }
        catch ( Throwable e )
        {
            // the response is already committed, so nothing meaningful can be rendered and the
            // exception ends up swallowed upstream — without this the connection would idle as a
            // zombie forever (the default SSE timeout is infinite). Close it so the client learns.
            // Throwable, not RuntimeException: a StackOverflowError escaping the handler must not
            // leave the zombie behind either
            entry.close();
            throw e;
        }

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
