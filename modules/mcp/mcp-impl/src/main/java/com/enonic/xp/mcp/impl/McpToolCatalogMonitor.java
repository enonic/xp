package com.enonic.xp.mcp.impl;

import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jspecify.annotations.NullMarked;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;

@NullMarked
@Component(immediate = true, service = EventListener.class)
public final class McpToolCatalogMonitor
    implements EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( McpToolCatalogMonitor.class );

    private static final Set<String> APPLICATION_EVENTS = Set.of( "INSTALLED", "STARTED", "STOPPED", "UNINSTALLED" );

    private final McpToolCatalog catalog;

    private final ScheduledExecutorService scheduler;

    private boolean stopped;

    @Activate
    public McpToolCatalogMonitor( @Reference final McpToolCatalog catalog )
    {
        this( catalog, Executors.newSingleThreadScheduledExecutor( runnable -> {
            final var thread = new Thread( runnable, "mcp-tool-catalog-monitor" );
            thread.setDaemon( true );
            return thread;
        } ) );
    }

    McpToolCatalogMonitor( final McpToolCatalog catalog, final ScheduledExecutorService scheduler )
    {
        this.catalog = catalog;
        this.scheduler = scheduler;
        scan();
        scheduler.scheduleWithFixedDelay( this::scan, 1, 1, TimeUnit.SECONDS );
    }

    @Override
    public void onEvent( final Event event )
    {
        if ( event.isType( "application" ) && event.getValue( "eventType" ).filter( APPLICATION_EVENTS::contains ).isPresent() )
        {
            scan();
        }
    }

    synchronized void scan()
    {
        if ( stopped )
        {
            return;
        }
        try
        {
            catalog.refresh();
        }
        catch ( RuntimeException e )
        {
            LOG.warn( "Cannot refresh MCP tool catalog", e );
        }
    }

    @Deactivate
    public synchronized void deactivate()
    {
        stopped = true;
        scheduler.shutdownNow();
    }
}
