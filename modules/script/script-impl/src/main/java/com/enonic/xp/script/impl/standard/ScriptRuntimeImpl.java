package com.enonic.xp.script.impl.standard;

import java.io.Closeable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
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

    /**
     * Last bootstrap request per application. An executor discarded by a racing {@link #invalidate}
     * (application reconfigure re-registers the service before it calls invalidators) would leave
     * its lazily recreated successor's gate armed by no one — remembering the params lets the
     * successor re-arm itself on first use instead of every caller waiting out the gate timeout.
     * Deliberately NOT cleared by {@code invalidate}: the racing invalidate is precisely the moment
     * the memory must survive. Entries cannot go stale (the params are the application key plus the
     * {@code /main.js} convention, invariant across incarnations, and resources resolve through the
     * current executor), a fresh registration overwrites them, and the footprint is one small
     * object per application key for the runtime's lifetime.
     */
    private final ConcurrentMap<ApplicationKey, BootstrapParams> bootstrapParams = new ConcurrentHashMap<>();

    private final Function<ApplicationKey, ScriptExecutor> scriptExecutorFactory;

    private final Function<ApplicationKey, Executor> asyncExecutors;

    public ScriptRuntimeImpl( final Function<ApplicationKey, ScriptExecutor> scriptExecutorFactory,
                              final Function<ApplicationKey, Executor> asyncExecutors )
    {
        this.scriptExecutorFactory = scriptExecutorFactory;
        this.asyncExecutors = asyncExecutors;
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
        bootstrapParams.put( key, params );
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
        final AppExecutor app = executorFor( script.getApplicationKey() );
        return app.executor.executeMain( script );
    }

    @Deprecated
    @Override
    public CompletableFuture<ScriptExports> executeAsync( final ResourceKey script )
    {
        final AppExecutor app = executorFor( script.getApplicationKey() );
        // the deprecated contract (top level runs on the app's async executor) is preserved here,
        // as a wrapper - the internal executor SPI carries no async variant
        return CompletableFuture.supplyAsync( () -> app.executor.executeMain( script ),
                                              asyncExecutors.apply( script.getApplicationKey() ) );
    }

    @Override
    public ScriptExports executeBackground( final ResourceKey script )
    {
        final AppExecutor app = executorFor( script.getApplicationKey() );
        return app.executor.backgroundExports( script );
    }

    @Override
    public boolean isPooled( final ApplicationKey application )
    {
        // capability query, not an execution: no bootstrap-gate wait
        return getExecutor( application ).executor.isPooled();
    }

    /**
     * The bootstrapped executor for a top-level execution: re-arms the bootstrap gate if this
     * executor incarnation was created after its bootstrap call (see {@link #bootstrapParams}),
     * then waits for the gate.
     */
    private AppExecutor executorFor( final ApplicationKey key )
    {
        final AppExecutor app = getExecutor( key );
        rearmIfNeeded( key, app );
        await( key, app );
        return app;
    }

    private void rearmIfNeeded( final ApplicationKey key, final AppExecutor app )
    {
        if ( !app.bootstrapStarted.get() )
        {
            final BootstrapParams params = bootstrapParams.get( key );
            if ( params != null && app.bootstrapStarted.compareAndSet( false, true ) )
            {
                // a detached thread, like MainExecutor's bootstrap: the triggering caller then waits
                // on the gate with the bounded timeout instead of running main.js unboundedly itself
                Thread.ofVirtual()
                    .name( "re-bootstrap-" + key )
                    .start( () -> runBootstrap( key, app, params.getMainScript().orElse( null ) ) );
            }
        }
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
            catch ( Exception e )
            {
                // teardown must finish even if a context refuses to close — an escaping exception
                // here would abort the OSGi service-tracker callback that drives app stop
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
            // fail open, and latch it: a hanging or never-armed bootstrap must not dam the
            // application forever — nor make every subsequent caller wait the timeout out again
            app.bootstrapped.complete( null );
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
