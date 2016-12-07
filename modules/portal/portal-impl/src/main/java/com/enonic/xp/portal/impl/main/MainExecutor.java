package com.enonic.xp.portal.impl.main;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

@Component(immediate = true)
public final class MainExecutor
    implements EventListener
{
    private final static Logger LOG = LoggerFactory.getLogger( MainExecutor.class );

    private PortalScriptService scriptService;

    private ResourceService resourceService;

    @Override
    public void onEvent( final Event event )
    {
        if ( !event.getType().equals( "application" ) )
        {
            return;
        }

        if ( !event.isLocalOrigin() )
        {
            return;
        }

        final String state = event.getValueAs( String.class, "eventType" ).orElse( null );
        final String application = event.getValueAs( String.class, "applicationKey" ).orElse( null );

        if ( "STARTED".equals( state ) && ( application != null ) )
        {
            started( application );
        }
    }

    private void started( final String key )
    {
        started( ApplicationKey.from( key ) );
    }

    private void started( final ApplicationKey key )
    {
        executeMain( ResourceKey.from( key, "/main.js" ) );
    }

    private void executeMain( final ResourceKey key )
    {
        if ( !this.scriptService.hasScript( key ) )
        {
            return;
        }

        try
        {
            this.scriptService.execute( key );
        }
        catch ( final Exception e )
        {
            LOG.error( "Error executing [" + key.toString() + "]", e );
        }
    }

    @Reference
    public void setScriptService( final PortalScriptService scriptService )
    {
        this.scriptService = scriptService;
    }
}
