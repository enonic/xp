package com.enonic.xp.script.graal.executor;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.graalvm.polyglot.proxy.ProxyObject;

import com.google.common.io.Files;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceError;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.graal.GraalJSContextFactory;
import com.enonic.xp.script.graal.function.GraalScriptFunctions;
import com.enonic.xp.script.graal.util.GraalErrorHelper;
import com.enonic.xp.script.graal.util.GraalJavascriptHelperFactory;
import com.enonic.xp.script.graal.value.GraalScriptValueFactory;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.impl.executor.ScriptExportsCache;
import com.enonic.xp.script.impl.function.ApplicationInfoBuilder;
import com.enonic.xp.script.impl.service.ServiceRegistry;
import com.enonic.xp.script.impl.util.JavascriptHelper;
import com.enonic.xp.script.impl.util.ObjectConverter;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.server.RunMode;

public class GraalScriptExecutor
    implements ScriptExecutor, Closeable
{
    private static final String PRE_SCRIPT = "(function( log, require, resolve, __, exports, module) { ";

    private static final String POST_SCRIPT = "\n});";

    private final Executor asyncExecutor;

    private final ScriptSettings scriptSettings;

    private final ClassLoader classLoader;

    private final ServiceRegistry serviceRegistry;

    private final ResourceService resourceService;

    private final Map<String, Object> mocks = new ConcurrentHashMap<>();

    private final Map<ResourceKey, Queue<Runnable>> disposers = new ConcurrentHashMap<>();

    private final List<ContextSlot> slots;

    private final AtomicInteger roundRobin = new AtomicInteger();

    private final ThreadLocal<ContextSlot> boundSlot = new ThreadLocal<>();

    public GraalScriptExecutor( final GraalJSContextFactory contextFactory, final Executor asyncExecutor, final ClassLoader classLoader,
                                final ScriptSettings scriptSettings, final ServiceRegistry serviceRegistry,
                                final ResourceService resourceService, final ApplicationInfoBuilder application,
                                final int contextPoolSize )
    {
        this.asyncExecutor = asyncExecutor;
        this.scriptSettings = scriptSettings;
        this.resourceService = resourceService;
        this.serviceRegistry = serviceRegistry;
        this.classLoader = classLoader;

        final int poolSize = Math.max( 1, contextPoolSize );
        this.slots = new ArrayList<>( poolSize );
        for ( int i = 0; i < poolSize; i++ )
        {
            this.slots.add( new ContextSlot( contextFactory, application ) );
        }
    }

    @Override
    public ScriptExports executeMain( final ResourceKey key )
    {
        withSlot( slot -> {
            if ( RunMode.isDev() )
            {
                slot.exportsCache.expireCacheIfNeeded();
            }
            return requireInSlot( slot, key );
        } );
        return new GraalScriptExports( this, key );
    }

    @Override
    public CompletableFuture<ScriptExports> executeMainAsync( final ResourceKey key )
    {
        return CompletableFuture.completedFuture( key ).thenApplyAsync( this::executeMain, asyncExecutor );
    }

    @Override
    public Object executeRequire( final ResourceKey key )
    {
        final Object mock = this.mocks.get( key.getPath() );
        if ( mock != null )
        {
            return mock;
        }

        return withSlot( slot -> requireInSlot( slot, key ) );
    }

    @Override
    public ScriptValue newScriptValue( final Object value )
    {
        return withSlot( slot -> slot.scriptValueFactory.newValue( value ) );
    }

    @Override
    public ClassLoader getClassLoader()
    {
        return classLoader;
    }

    @Override
    public ServiceRegistry getServiceRegistry()
    {
        return serviceRegistry;
    }

    @Override
    public ResourceService getResourceService()
    {
        return resourceService;
    }

    @Override
    public ScriptSettings getScriptSettings()
    {
        return scriptSettings;
    }

    @Override
    public ObjectConverter getObjectConverter()
    {
        return currentSlot().javascriptHelper.objectConverter();
    }

    @Override
    public void registerMock( final String name, final Object value )
    {
        this.mocks.put( name, value );

        if ( Files.getFileExtension( name ).isEmpty() )
        {
            this.mocks.put( name + ".js", value );
        }
    }

    @Override
    public void registerDisposer( final ResourceKey key, final Runnable callback )
    {
        this.disposers.computeIfAbsent( key, k -> new ConcurrentLinkedQueue<>() ).add( callback );
    }

    @Override
    public void runDisposers()
    {
        // drain, so each registered disposer runs at most once — a script reload (dev-mode cache
        // expiry) re-registers its disposer, and leftovers must not run again on the next expiry
        this.disposers.values().forEach( queue -> {
            Runnable disposer;
            while ( ( disposer = queue.poll() ) != null )
            {
                disposer.run();
            }
        } );
    }

    @Override
    public void close()
    {
        for ( final ContextSlot slot : slots )
        {
            slot.context.close();
        }
    }

    /**
     * Runs work on an exclusively owned context slot. Resolution order: the slot already bound to
     * this thread (nested executions during a request — affinity is best-effort under nesting,
     * the bound slot always wins to keep re-entrancy deadlock-free), a slot whose context monitor
     * this thread already holds (callbacks routed through {@code JsFunctionHandle}/
     * {@code ScriptValue} monitors calling back into the executor, e.g. {@code require} on a
     * foreign thread — must not acquire a different slot, both for correctness and to avoid
     * slot/monitor deadlocks), then the pinned slot if given, and only then any free slot.
     */
    <T> T withSlot( final ContextSlot pinned, final Function<ContextSlot, T> work )
    {
        final ContextSlot bound = this.boundSlot.get();
        if ( bound != null )
        {
            return work.apply( bound );
        }

        for ( final ContextSlot slot : slots )
        {
            if ( Thread.holdsLock( slot.context ) )
            {
                return runBound( slot, work );
            }
        }

        if ( pinned != null )
        {
            return lockAndRun( pinned, work );
        }

        final int start = this.roundRobin.getAndIncrement();
        final int size = slots.size();
        for ( int i = 0; i < size; i++ )
        {
            final ContextSlot slot = slots.get( Math.floorMod( start + i, size ) );
            if ( slot.lock.tryLock() )
            {
                try
                {
                    synchronized ( slot.context )
                    {
                        return runBound( slot, work );
                    }
                }
                finally
                {
                    slot.lock.unlock();
                }
            }
        }
        return lockAndRun( slots.get( Math.floorMod( start, size ) ), work );
    }

    <T> T withSlot( final Function<ContextSlot, T> work )
    {
        return withSlot( null, work );
    }

    <T> T withExports( final ResourceKey key, final ContextSlot pinned, final BiFunction<ContextSlot, Value, T> work )
    {
        return withSlot( pinned, slot -> work.apply( slot, requireInSlot( slot, key ) ) );
    }

    /**
     * The slot serving executions pinned to the given stable key: deterministic by key hash, so
     * equal keys (e.g. one websocket connection) always resolve to the same slot without any
     * per-connection bookkeeping.
     */
    ContextSlot slotFor( final Object affinityKey )
    {
        return slots.get( Math.floorMod( affinityKey.hashCode(), slots.size() ) );
    }

    private <T> T runBound( final ContextSlot slot, final Function<ContextSlot, T> work )
    {
        this.boundSlot.set( slot );
        try
        {
            return work.apply( slot );
        }
        finally
        {
            this.boundSlot.remove();
        }
    }

    private <T> T lockAndRun( final ContextSlot slot, final Function<ContextSlot, T> work )
    {
        try
        {
            if ( !slot.lock.tryLock( 5, TimeUnit.MINUTES ) )
            {
                throw new RuntimeException( "Timed out waiting for a free script context" );
            }
        }
        catch ( final InterruptedException e )
        {
            Thread.currentThread().interrupt();
            throw new RuntimeException( "Interrupted while waiting for a free script context", e );
        }
        try
        {
            synchronized ( slot.context )
            {
                return runBound( slot, work );
            }
        }
        finally
        {
            slot.lock.unlock();
        }
    }

    private Value requireInSlot( final ContextSlot slot, final ResourceKey key )
    {
        try
        {
            return slot.exportsCache.getOrCompute( key, resource -> requireJsOrJson( slot, resource ) );
        }
        catch ( InterruptedException | TimeoutException e )
        {
            throw new RuntimeException( "Script require failed: [" + key + "]", e );
        }
    }

    private Value requireJsOrJson( final ContextSlot slot, final Resource resource )
    {
        return "json".equals( resource.getKey().getExtension() ) ? requireJson( slot, resource ) : requireJs( slot, resource );
    }

    private Value requireJs( final ContextSlot slot, final Resource resource )
    {
        final SimpleBindings bindings = new SimpleBindings();
        bindings.put( ScriptEngine.FILENAME, getFileName( resource ) );

        final Value func = doExecute( slot, bindings, resource );
        return executeRequire( slot, resource.getKey(), func );
    }

    private Value requireJson( final ContextSlot slot, final Resource resource )
    {
        try
        {
            final String text = resource.readString();
            return slot.javascriptHelper.parseJson( text );
        }
        catch ( final Exception e )
        {
            throw GraalErrorHelper.handleError( e );
        }
    }

    private Value executeRequire( final ContextSlot slot, final ResourceKey script, final Value func )
    {
        try
        {
            Value exports = slot.javascriptHelper.newJsObject();

            Value module = slot.javascriptHelper.newJsObject();
            module.putMember( "id", script.toString() );
            module.putMember( "exports", exports );

            final GraalScriptFunctions functions = new GraalScriptFunctions( slot.context, script, this );
            func.execute( functions.getLog(), functions.getRequire(), functions.getResolve(), functions, exports, module );
            return module.getMember( "exports" );
        }
        catch ( final Exception e )
        {
            throw GraalErrorHelper.handleError( e );
        }
        catch ( final StackOverflowError e )
        {
            throw new ResourceError( script, "Script require failed: [" + script + "]", e );
        }
    }

    private String getFileName( final Resource resource )
    {
        if ( this.scriptSettings.getDebug() != null )
        {
            return this.scriptSettings.getDebug().scriptName( resource );
        }

        return resource.getKey().toString();
    }

    private Value doExecute( final ContextSlot slot, final Bindings bindings, final Resource script )
    {
        try
        {
            final String text = script.readString();
            final String source = PRE_SCRIPT + text + POST_SCRIPT;
            bindings.forEach( ( key, value ) -> slot.context.getBindings( "js" ).putMember( key, value ) );
            return slot.context.eval( Source.newBuilder( "js", source, script.getKey().toString() ).build() );
        }
        catch ( final Exception e )
        {
            throw GraalErrorHelper.handleError( e );
        }
        catch ( final StackOverflowError e )
        {
            throw new ResourceError( script.getKey(), "Script execute failed: [" + script.getKey() + "]", e );
        }
    }

    /**
     * The slot to use for conversions requested outside a slot execution — the bound/held slot
     * when inside one, the first slot otherwise (matching the single-context behavior).
     */
    private ContextSlot currentSlot()
    {
        final ContextSlot bound = this.boundSlot.get();
        if ( bound != null )
        {
            return bound;
        }
        for ( final ContextSlot slot : slots )
        {
            if ( Thread.holdsLock( slot.context ) )
            {
                return slot;
            }
        }
        return slots.get( 0 );
    }

    /**
     * One pooled JS context with everything bound to it: value factory, helper and the
     * {@code require} cache. Cached exports are {@link Value}s of this slot's context and must
     * never mix with another slot's.
     */
    final class ContextSlot
    {
        /**
         * Fair, so pinned waiters (per-connection events) execute in arrival order.
         */
        final ReentrantLock lock = new ReentrantLock( true );

        final GraalScriptValueFactory scriptValueFactory;

        final JavascriptHelper<Value> javascriptHelper;

        final Context context;

        final ScriptExportsCache<Value> exportsCache;

        ContextSlot( final GraalJSContextFactory contextFactory, final ApplicationInfoBuilder application )
        {
            this.scriptValueFactory = new GraalScriptValueFactory( contextFactory, new GraalJavascriptHelperFactory() );
            this.javascriptHelper = this.scriptValueFactory.getJavascriptHelper();
            this.context = this.scriptValueFactory.getContext();
            this.exportsCache = new ScriptExportsCache<>( resourceService::getResource, GraalScriptExecutor.this::runDisposers );

            final Map<String, Object> globalVariables = new HashMap<>( scriptSettings.getGlobalVariables() );
            globalVariables.put( "app", ProxyObject.fromMap( application.buildMap( HashMap::new ) ) );
            globalVariables.forEach( ( key, value ) -> this.context.getBindings( "js" ).putMember( key, value ) );
        }
    }
}
