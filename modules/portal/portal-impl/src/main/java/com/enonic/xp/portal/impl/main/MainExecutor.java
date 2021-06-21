package com.enonic.xp.portal.impl.main;

import java.util.concurrent.CompletableFuture;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationListener;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;

@Component(immediate = true)
public final class MainExecutor
    implements ApplicationListener
{
    private static final Logger LOG = LoggerFactory.getLogger( MainExecutor.class );

    private final PortalScriptService scriptService;

    @Activate
    public MainExecutor( @Reference final PortalScriptService scriptService )
    {
        this.scriptService = scriptService;
    }

    @Override
    public void activated( final Application app )
    {
        executeMain( ResourceKey.from( app.getKey(), "/main.js" ) );
    }

    @Override
    public void deactivated( final Application app )
    {
    }

    private void executeMain( final ResourceKey key )
    {
        if ( this.scriptService.hasScript( key ) )
        {
            final CompletableFuture<ScriptExports> completableFuture = this.scriptService.executeAsync( key );
            completableFuture.whenComplete( ( u, e ) -> {
                if ( e != null )
                {
                    LOG.error( "Error while executing {} Application controller", key.getApplicationKey(), e );
                }
                else
                {
                    LOG.debug( "Completed execution of {} Application controller", key.getApplicationKey() );
                }
            } );
        }
    }
}
