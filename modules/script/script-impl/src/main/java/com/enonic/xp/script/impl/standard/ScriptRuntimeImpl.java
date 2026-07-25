package com.enonic.xp.script.impl.standard;

import java.io.Closeable;
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
import com.enonic.xp.resource.ResourceNotFoundException;
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

    /**
     * The identity of an application's current service registration ({@code null} when not
     * registered) — fed by the factory's service tracker. Executors are stamped with the
     * incarnation they were built from and revalidated against this on every use, so a stale
     * executor (its registration stopped or replaced while the creation raced the teardown)
     * dies on its next touch instead of serving a gone application.
     */
    private final Function<ApplicationKey, Object> incarnations;

    public ScriptRuntimeImpl( final Function<ApplicationKey, ScriptExecutor> scriptExecutorFactory,
                              final Function<ApplicationKey, Object> incarnations )
    {
        this.scriptExecutorFactory = scriptExecutorFactory;
        this.incarnations = incarnations;
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
        final AppExecutor app = executorFor( script.getApplicationKey() );
        return app.executor.executeMain( script );
    }

    @Deprecated
    @Override
    public CompletableFuture<ScriptExports> executeAsync( final ResourceKey script )
    {
        return CompletableFuture.supplyAsync( () -> execute( script ), Thread::startVirtualThread );
    }

    @Override
    public Object executeMethod( final ResourceKey script, final String method, final Object... args )
    {
        final AppExecutor app = executorFor( script.getApplicationKey() );
        // a missing script must fail with the same exception on every engine
        if ( !app.executor.getResourceService().getResource( script ).exists() )
        {
            throw new ResourceNotFoundException( script );
        }
        return app.executor.executeMethod( script, method, args );
    }

    /**
     * The bootstrapped executor for a top-level execution: waits for the gate, then confirms the
     * executor survived the wait. Teardown opens the gate to release waiters, and a released
     * waiter must not proceed on the torn-down executor — it resolves again, which fails fast
     * when the application is gone and lands on the successor when it was replaced.
     */
    private AppExecutor executorFor( final ApplicationKey key )
    {
        while ( true )
        {
            final AppExecutor app = getExecutor( key );
            await( key, app );
            if ( executors.get( key ) == app )
            {
                return app;
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
        teardown( key, removed );
    }

    private void teardown( final ApplicationKey key, final AppExecutor removed )
    {
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
        while ( true )
        {
            // the incarnation is read before the executor is built: a reconfigure racing the
            // creation can only make the stamp too old, never too new — the entry then fails
            // revalidation and is rebuilt, instead of a stale executor passing as current
            final AppExecutor app = executors.computeIfAbsent( key, k -> new AppExecutor( incarnations.apply( k ),
                                                                                          scriptExecutorFactory.apply( k ) ) );
            if ( app.incarnation == incarnations.apply( key ) )
            {
                return app;
            }
            // built from a service registration that is gone (application stopped, or replaced by
            // a reconfigure that raced this creation): tear it down and resolve the current one
            if ( executors.remove( key, app ) )
            {
                teardown( key, app );
            }
        }
    }

    private static final class AppExecutor
    {
        final Object incarnation;

        final ScriptExecutor executor;

        final AtomicBoolean bootstrapStarted = new AtomicBoolean();

        final CompletableFuture<Void> bootstrapped = new CompletableFuture<>();

        AppExecutor( final Object incarnation, final ScriptExecutor executor )
        {
            this.incarnation = incarnation;
            this.executor = executor;
        }
    }
}
