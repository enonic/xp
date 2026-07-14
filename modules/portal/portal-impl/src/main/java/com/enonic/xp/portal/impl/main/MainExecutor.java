package com.enonic.xp.portal.impl.main;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationListener;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;

@Component(immediate = true, service = {ApplicationListener.class, BootstrapState.class})
public final class MainExecutor
    implements ApplicationListener, BootstrapState
{
    private static final Logger LOG = LoggerFactory.getLogger( MainExecutor.class );

    private static final long BOOTSTRAP_WAIT_SECONDS = 300;

    private final PortalScriptService scriptService;

    private final ConcurrentMap<ApplicationKey, CompletableFuture<Void>> bootstraps = new ConcurrentHashMap<>();

    @Activate
    public MainExecutor( @Reference final PortalScriptService scriptService )
    {
        this.scriptService = scriptService;
    }

    @Override
    public void activated( final Application app )
    {
        executeMain( app.getKey(), ResourceKey.from( app.getKey(), "/main.js" ) );
    }

    @Override
    public void deactivated( final Application app )
    {
        final CompletableFuture<Void> gate = this.bootstraps.remove( app.getKey() );
        if ( gate != null )
        {
            // release anyone awaiting the bootstrap of a dying application
            gate.complete( null );
        }
    }

    @Override
    public void awaitBootstrapped( final ApplicationKey key )
    {
        final CompletableFuture<Void> gate = this.bootstraps.get( key );
        if ( gate == null || gate.isDone() )
        {
            return;
        }
        try
        {
            gate.get( BOOTSTRAP_WAIT_SECONDS, TimeUnit.SECONDS );
        }
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt();
            throw new RuntimeException( "Interrupted while waiting for " + key + " to bootstrap", e );
        }
        catch ( ExecutionException e )
        {
            // unreachable: the gate always completes with null
        }
        catch ( TimeoutException e )
        {
            // fail open: a hanging main.js must not dam the application forever
            LOG.warn( "main.js of {} has not completed within {}s - proceeding without it", key, BOOTSTRAP_WAIT_SECONDS );
        }
    }

    private void executeMain( final ApplicationKey applicationKey, final ResourceKey key )
    {
        if ( !this.scriptService.hasScript( key ) )
        {
            return;
        }
        // armed before execution starts: controller executions await the gate (#7821), so
        // main.js initialization observably happens before any controller runs
        final CompletableFuture<Void> gate = new CompletableFuture<>();
        this.bootstraps.put( applicationKey, gate );

        this.scriptService.executeAsync( key ).whenComplete( ( u, e ) -> {
            if ( e != null )
            {
                LOG.error( "Error while executing {} Application controller", applicationKey, e );
            }
            else
            {
                LOG.debug( "Completed execution of {} Application controller", applicationKey );
            }
            // open on success AND failure: a broken main.js surfaces in the log, not as a
            // permanently dammed application
            gate.complete( null );
        } );
    }
}
