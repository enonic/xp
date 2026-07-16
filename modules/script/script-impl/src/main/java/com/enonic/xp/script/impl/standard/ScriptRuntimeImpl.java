package com.enonic.xp.script.impl.standard;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.AppNotRegisteredException;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.runtime.BootstrapParams;
import com.enonic.xp.script.runtime.ScriptRuntime;

public class ScriptRuntimeImpl
    implements ScriptRuntime
{
    private static final Logger LOG = LoggerFactory.getLogger( ScriptRuntimeImpl.class );

    private static final long BOOTSTRAP_TIMEOUT_SECONDS = 300;

    /**
     * The application whose bootstrap is running on the current thread, if any. The bootstrap script
     * and everything it triggers synchronously must run before the application is bootstrapped, so a
     * re-entrant execution on the bootstrap thread must not wait for the bootstrap it is performing.
     */
    private static final ScopedValue<ApplicationKey> BOOTSTRAPPING = ScopedValue.newInstance();

    private final ConcurrentMap<ApplicationKey, AppExecutor> executors = new ConcurrentHashMap<>();

    private final Function<ApplicationKey, ScriptExecutor> scriptExecutorFactory;

    public ScriptRuntimeImpl( final Function<ApplicationKey, ScriptExecutor> scriptExecutorFactory )
    {
        this.scriptExecutorFactory = scriptExecutorFactory;
    }

    @Override
    public boolean hasScript( final ResourceKey script )
    {
        final AppExecutor app;
        try
        {
            app = getExecutor( script.getApplicationKey() );
        }
        catch ( AppNotRegisteredException e )
        {
            return false;
        }

        return app.executor.getResourceService().getResource( script ).exists();
    }

    @Override
    public void bootstrap( final BootstrapParams params )
    {
        final ApplicationKey key = params.getApplication();
        final AppExecutor app = getExecutor( key );
        if ( app.bootstrapStarted.compareAndSet( false, true ) )
        {
            runBootstrap( key, app, params.getMainScript().orElse( null ) );
        }
        else
        {
            await( key, app );
        }
    }

    @Override
    public ScriptExports execute( final ResourceKey script )
    {
        final AppExecutor app = getExecutor( script.getApplicationKey() );
        await( script.getApplicationKey(), app );
        return app.executor.executeMain( script );
    }

    @Deprecated
    @Override
    public CompletableFuture<ScriptExports> executeAsync( final ResourceKey script )
    {
        final AppExecutor app = getExecutor( script.getApplicationKey() );
        await( script.getApplicationKey(), app );
        return app.executor.executeMainAsync( script );
    }

    @Override
    public void invalidate( final ApplicationKey key )
    {
        LOG.debug( "Remove Script Executor for {}", key );
        final AppExecutor removed = executors.remove( key );
        if ( removed == null )
        {
            return;
        }
        // release any waiters: the app is gone, so its bootstrap will never complete on its own
        removed.bootstrapped.complete( null );
        // instance-owned teardown (#10844): the removed executor's own disposers run against its
        // own (still open) contexts — never a name-keyed lookup, which under application replacement
        // can resolve to the successor instance
        try
        {
            removed.executor.runDisposers();
        }
        catch ( Exception e )
        {
            LOG.warn( "Error while running disposers for {}", key, e );
        }
        if ( removed.executor instanceof Closeable )
        {
            try
            {
                ( (Closeable) removed.executor ).close();
            }
            catch ( IOException e )
            {
                LOG.warn( "Could not close Script Executor for {}", key, e );
            }
        }
    }

    @Override
    public ScriptValue toScriptValue( final ResourceKey script, final Object value )
    {
        return getExecutor( script.getApplicationKey() ).executor.newScriptValue( value );
    }

    @Override
    public Object toNativeObject( final ResourceKey script, final Object value )
    {
        return getExecutor( script.getApplicationKey() ).executor.getObjectConverter().toJs( value );
    }

    /**
     * Tears down every executor this runtime owns — used when the runtime itself is disposed.
     */
    public void close()
    {
        executors.keySet().forEach( this::invalidate );
    }

    /**
     * Runs the application's optional bootstrap script once, on the dedicated main context, then
     * opens the gate that {@link #execute} waits on (<a href="https://github.com/enonic/xp/issues/7821">
     * #7821</a>). The gate lives on the per-application executor, created fresh for each application
     * incarnation and discarded on {@link #invalidate}, so two installs of the same key can neither
     * share nor race a gate. When no bootstrap script is given (or it is missing) the gate simply
     * opens; a broken bootstrap script also still opens it (logged, never a permanently
     * un-bootstrapped application).
     */
    private void runBootstrap( final ApplicationKey key, final AppExecutor app, final ResourceKey mainScript )
    {
        try
        {
            if ( mainScript != null && app.executor.getResourceService().getResource( mainScript ).exists() )
            {
                ScopedValue.where( BOOTSTRAPPING, key ).run( () -> app.executor.bootstrap( mainScript ) );
            }
        }
        catch ( Exception e )
        {
            LOG.error( "Error while executing {}", mainScript, e );
        }
        finally
        {
            app.bootstrapped.complete( null );
        }
    }

    /**
     * Waits for the application's bootstrap to complete, so a top-level execution observes a fully
     * bootstrapped application. Executions re-entrant within the application's own bootstrap do not
     * wait — they are performing it.
     */
    private void await( final ApplicationKey key, final AppExecutor app )
    {
        if ( BOOTSTRAPPING.isBound() && BOOTSTRAPPING.get().equals( key ) )
        {
            return;
        }
        try
        {
            app.bootstrapped.get( BOOTSTRAP_TIMEOUT_SECONDS, TimeUnit.SECONDS );
        }
        catch ( TimeoutException e )
        {
            // fail open: a hanging or never-armed bootstrap must not dam the application forever
            LOG.warn( "Application {} has not bootstrapped within {}s - proceeding", key, BOOTSTRAP_TIMEOUT_SECONDS );
        }
        catch ( ExecutionException e )
        {
            // bootstrap completes even on failure; this is not expected, but must not block the caller
            LOG.warn( "Application {} bootstrap failed", key, e );
        }
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt();
            throw new RuntimeException( "Interrupted while waiting for " + key + " to bootstrap", e );
        }
    }

    private AppExecutor getExecutor( final ApplicationKey key )
    {
        return executors.computeIfAbsent( key, k -> new AppExecutor( scriptExecutorFactory.apply( k ) ) );
    }

    private static final class AppExecutor
    {
        final ScriptExecutor executor;

        final AtomicBoolean bootstrapStarted = new AtomicBoolean();

        final CompletableFuture<Void> bootstrapped = new CompletableFuture<>();

        AppExecutor( final ScriptExecutor executor )
        {
            this.executor = executor;
        }
    }
}
