package com.enonic.xp.script.impl.standard;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
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
import com.enonic.xp.script.runtime.ScriptRuntime;

public class ScriptRuntimeImpl
    implements ScriptRuntime
{
    private static final Logger LOG = LoggerFactory.getLogger( ScriptRuntimeImpl.class );

    private static final String MAIN_SCRIPT = "/main.js";

    private static final long BOOTSTRAP_TIMEOUT_SECONDS = 300;

    /**
     * The application whose {@code main.js} is running on the current thread, if any. {@code main.js}
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
    public void bootstrap( final ResourceKey mainScript )
    {
        ensureBootstrapped( getExecutor( mainScript.getApplicationKey() ), mainScript );
    }

    @Override
    public ScriptExports execute( final ResourceKey script )
    {
        final AppExecutor app = getExecutor( script.getApplicationKey() );
        ensureBootstrapped( app, mainScript( script ) );
        return app.executor.executeMain( script );
    }

    @Deprecated
    @Override
    public CompletableFuture<ScriptExports> executeAsync( final ResourceKey script )
    {
        final AppExecutor app = getExecutor( script.getApplicationKey() );
        ensureBootstrapped( app, mainScript( script ) );
        return app.executor.executeMainAsync( script );
    }

    private static ResourceKey mainScript( final ResourceKey script )
    {
        return ResourceKey.from( script.getApplicationKey(), MAIN_SCRIPT );
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
        removed.bootstrapped.countDown();
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
     * Runs the application's {@code main.js} exactly once before any of its controllers and blocks
     * until it has (<a href="https://github.com/enonic/xp/issues/7821">#7821</a>). The gate lives on
     * the per-application executor, created fresh for each application incarnation and discarded on
     * {@link #invalidate}, so two installs of the same key can neither share nor race a gate — the
     * caller always waits on the exact executor it is about to run.
     */
    private void ensureBootstrapped( final AppExecutor app, final ResourceKey mainScript )
    {
        final ApplicationKey key = mainScript.getApplicationKey();
        if ( BOOTSTRAPPING.isBound() && BOOTSTRAPPING.get().equals( key ) )
        {
            return;
        }
        if ( app.bootstrapStarted.compareAndSet( false, true ) )
        {
            runMainScript( app, mainScript );
        }
        else
        {
            awaitBootstrapped( key, app );
        }
    }

    private void runMainScript( final AppExecutor app, final ResourceKey mainScript )
    {
        try
        {
            if ( app.executor.getResourceService().getResource( mainScript ).exists() )
            {
                ScopedValue.where( BOOTSTRAPPING, mainScript.getApplicationKey() ).run( () -> app.executor.bootstrap( mainScript ) );
            }
        }
        catch ( Exception e )
        {
            // a broken main.js surfaces in the log, never as a permanently un-bootstrapped application
            LOG.error( "Error while executing {}", mainScript, e );
        }
        finally
        {
            app.bootstrapped.countDown();
        }
    }

    private void awaitBootstrapped( final ApplicationKey key, final AppExecutor app )
    {
        try
        {
            if ( !app.bootstrapped.await( BOOTSTRAP_TIMEOUT_SECONDS, TimeUnit.SECONDS ) )
            {
                // fail open: a hanging main.js must not dam the application forever
                LOG.warn( "Application {} has not bootstrapped within {}s - proceeding", key, BOOTSTRAP_TIMEOUT_SECONDS );
            }
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

        final CountDownLatch bootstrapped = new CountDownLatch( 1 );

        final AtomicBoolean bootstrapStarted = new AtomicBoolean();

        AppExecutor( final ScriptExecutor executor )
        {
            this.executor = executor;
        }
    }
}
