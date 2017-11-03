package com.enonic.xp.server.internal.trace;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.server.internal.trace.event.TraceEventDispatcher;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.TraceEvent;
import com.enonic.xp.trace.TraceManager;
import com.enonic.xp.trace.Tracer;

@Component(immediate = true, configurationPid = "com.enonic.xp.server.trace")
public final class TraceService
    implements TraceManager
{
    private final static Logger LOG = LoggerFactory.getLogger( TraceService.class );

    private TraceEventDispatcher dispatcher;

    @Activate
    public void activate( final TraceConfig config )
    {
        if ( config.enabled() )
        {
            LOG.info( "Call tracing is enabled in config" );
            Tracer.setManager( this );
        }
        else
        {
            LOG.info( "Call tracing is disabled in config" );
        }
    }

    @Deactivate
    public void deactivate()
    {
        Tracer.setManager( null );
    }

    @Override
    public Trace newTrace( final String name, final Trace parent )
    {
        return new TraceImpl( name, parent != null ? parent.getId() : null, TraceLocationImpl.findLocation() );
    }

    @Override
    public void dispatch( final TraceEvent event )
    {
        this.dispatcher.queue( event );
    }

    @Override
    public void enable( final boolean enabled )
    {
        if ( enabled )
        {
            Tracer.setManager( this );
            LOG.info( "Call tracing is enabled" );
        }
        else
        {
            Tracer.setManager( null );
            LOG.info( "Call tracing is disabled" );
        }
    }

    @Reference
    public void setDispatcher( final TraceEventDispatcher dispatcher )
    {
        this.dispatcher = dispatcher;
    }
}
