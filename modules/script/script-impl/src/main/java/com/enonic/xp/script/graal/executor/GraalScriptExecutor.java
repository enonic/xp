package com.enonic.xp.script.graal.executor;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;
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

    private static final long SLOT_WAIT_SECONDS = 300;

    /**
     * Pinned executions (websocket/SSE events) run on shared event-dispatch threads — they must
     * fail fast when their slot is saturated instead of holding those threads for minutes.
     */
    private static final long PINNED_SLOT_WAIT_SECONDS = 30;

    private final Executor asyncExecutor;

    private final ScriptSettings scriptSettings;

    private final ClassLoader classLoader;

    private final ServiceRegistry serviceRegistry;

    private final ResourceService resourceService;

    private final GraalJSContextFactory contextFactory;

    private final ApplicationInfoBuilder application;

    private final GraalContextBudget budget;

    private final Map<String, Object> mocks = new ConcurrentHashMap<>();

    private final Map<ResourceKey, Queue<Runnable>> disposers = new ConcurrentHashMap<>();

    /**
     * Strong per-app source registry: the shared engine's code cache is weak and keyed by
     * {@link Source} equality, so entries survive only while an equal Source is strongly
     * reachable. Retaining them here keeps slot growth and ephemeral task contexts parse-free.
     * Cleared on dev-mode cache expiry so reloads pick up changed resources.
     */
    private final Map<ResourceKey, Source> sources = new ConcurrentHashMap<>();

    /**
     * Lazily populated, fixed logical capacity: affinity hashes over the array length, so a
     * pool growing under load never remaps pinned connections.
     */
    private final AtomicReferenceArray<ContextSlot> slots;

    private final Object slotCreationLock = new Object();

    private int budgetedSlots;

    private boolean hasSlots;

    private final AtomicInteger roundRobin = new AtomicInteger();

    /**
     * The slot bound to the current execution scope (nested executions must stay on it). A
     * ScopedValue, aligned with the platform-wide ThreadLocal elimination; shared across
     * executors, so reads check slot ownership — a nested cross-app call must not adopt the
     * outer app's slot.
     */
    private static final ScopedValue<ContextSlot> BOUND_SLOT = ScopedValue.newInstance();

    public GraalScriptExecutor( final GraalJSContextFactory contextFactory, final Executor asyncExecutor, final ClassLoader classLoader,
                                final ScriptSettings scriptSettings, final ServiceRegistry serviceRegistry,
                                final ResourceService resourceService, final ApplicationInfoBuilder application,
                                final int contextPoolCapacity )
    {
        this( contextFactory, asyncExecutor, classLoader, scriptSettings, serviceRegistry, resourceService, application,
              contextPoolCapacity, GraalContextBudget.unlimited() );
    }

    public GraalScriptExecutor( final GraalJSContextFactory contextFactory, final Executor asyncExecutor, final ClassLoader classLoader,
                                final ScriptSettings scriptSettings, final ServiceRegistry serviceRegistry,
                                final ResourceService resourceService, final ApplicationInfoBuilder application,
                                final int contextPoolCapacity, final GraalContextBudget budget )
    {
        this.asyncExecutor = asyncExecutor;
        this.scriptSettings = scriptSettings;
        this.resourceService = resourceService;
        this.serviceRegistry = serviceRegistry;
        this.classLoader = classLoader;
        this.contextFactory = contextFactory;
        this.application = application;
        this.budget = budget;
        this.slots = new AtomicReferenceArray<>( Math.max( 1, contextPoolCapacity ) );
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
        synchronized ( slotCreationLock )
        {
            for ( int i = 0; i < slots.length(); i++ )
            {
                final ContextSlot slot = slots.get( i );
                if ( slot != null )
                {
                    slot.context.close();
                }
            }
            budget.releaseContexts( budgetedSlots );
            budgetedSlots = 0;
        }
    }

    /**
     * Runs work on an exclusively owned context slot. Resolution order: the slot already bound to
     * this thread (nested executions during a request — affinity is best-effort under nesting,
     * the bound slot always wins to keep re-entrancy deadlock-free), a slot whose context monitor
     * this thread already holds (callbacks routed through {@code JsFunctionHandle}/
     * {@code ScriptValue} monitors calling back into the executor, e.g. {@code require} on a
     * foreign thread — must not acquire a different slot, both for correctness and to avoid
     * slot/monitor deadlocks), then the pinned slot if given; anonymous executions prefer a free
     * existing slot, then grow the pool within the budget, then wait fairly.
     */
    <T> T withSlot( final ContextSlot pinned, final Function<ContextSlot, T> work )
    {
        final ContextSlot bound = BOUND_SLOT.orElse( null );
        if ( bound != null && bound.owner() == this )
        {
            return work.apply( bound );
        }

        for ( int i = 0; i < slots.length(); i++ )
        {
            final ContextSlot slot = slots.get( i );
            if ( slot != null && Thread.holdsLock( slot.context ) )
            {
                return runBound( slot, work );
            }
        }

        if ( pinned != null )
        {
            return lockAndRun( pinned, work, PINNED_SLOT_WAIT_SECONDS );
        }

        final int start = this.roundRobin.getAndIncrement();
        final int size = slots.length();
        for ( int i = 0; i < size; i++ )
        {
            final ContextSlot slot = slots.get( Math.floorMod( start + i, size ) );
            if ( slot != null && slot.lock.tryLock() )
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

        // every existing slot is busy: grow within the budget
        for ( int i = 0; i < size; i++ )
        {
            final int index = Math.floorMod( start + i, size );
            if ( slots.get( index ) == null )
            {
                final ContextSlot created = slotAt( index );
                if ( created != null )
                {
                    return lockAndRun( created, work, SLOT_WAIT_SECONDS );
                }
                break;
            }
        }

        // at capacity or out of budget: wait fairly on an existing slot
        for ( int i = 0; i < size; i++ )
        {
            final ContextSlot slot = slots.get( Math.floorMod( start + i, size ) );
            if ( slot != null )
            {
                return lockAndRun( slot, work, SLOT_WAIT_SECONDS );
            }
        }

        throw new IllegalStateException( "No script context available" );
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
     * Executes against a fresh, private context that lives for this invocation only — the
     * execution model for detached tasks: task threads are virtual and effectively unbounded,
     * and an IO-waiting task holds its context for the entire wait, so tasks must not compete
     * for request-serving slots. Bounded by the task-context budget; the shared source registry
     * keeps re-initialization parse-free.
     */
    <T> T withIsolatedExports( final ResourceKey key, final BiFunction<ContextSlot, Value, T> work )
    {
        budget.acquireTaskContext();
        try
        {
            final ContextSlot slot = new ContextSlot( contextFactory, application );
            try
            {
                synchronized ( slot.context )
                {
                    return runBound( slot, s -> work.apply( s, requireInSlot( s, key ) ) );
                }
            }
            finally
            {
                slot.context.close();
            }
        }
        finally
        {
            budget.releaseTaskContext();
        }
    }

    /**
     * The slot serving executions pinned to the given stable key: deterministic by key hash
     * over the fixed capacity, so equal keys (e.g. one websocket connection) always resolve to
     * the same slot without any per-connection bookkeeping. Falls back to the nearest existing
     * slot when the budget is exhausted.
     */
    ContextSlot slotFor( final Object affinityKey )
    {
        final int size = slots.length();
        final int index = Math.floorMod( affinityKey.hashCode(), size );
        final ContextSlot slot = slotAt( index );
        if ( slot != null )
        {
            return slot;
        }
        for ( int i = 0; i < size; i++ )
        {
            final ContextSlot existing = slots.get( Math.floorMod( index + i, size ) );
            if ( existing != null )
            {
                return existing;
            }
        }
        throw new IllegalStateException( "No script context available" );
    }

    private ContextSlot slotAt( final int index )
    {
        final ContextSlot existing = slots.get( index );
        if ( existing != null )
        {
            return existing;
        }
        synchronized ( slotCreationLock )
        {
            ContextSlot slot = slots.get( index );
            if ( slot != null )
            {
                return slot;
            }
            // an app's first slot is always allowed — a full global budget must not lock a
            // fresh application out; only growth beyond it is budgeted
            final boolean budgeted = hasSlots;
            if ( budgeted && !budget.tryAcquireContext() )
            {
                return null;
            }
            slot = new ContextSlot( contextFactory, application );
            slots.set( index, slot );
            hasSlots = true;
            if ( budgeted )
            {
                budgetedSlots++;
            }
            return slot;
        }
    }

    private <T> T runBound( final ContextSlot slot, final Function<ContextSlot, T> work )
    {
        // scoped rebinding nests naturally: an isolated execution inside a slot-bound one
        // shadows the binding for its scope and the outer binding is restored on exit
        return ScopedValue.where( BOUND_SLOT, slot ).call( () -> work.apply( slot ) );
    }

    private <T> T lockAndRun( final ContextSlot slot, final Function<ContextSlot, T> work, final long waitSeconds )
    {
        try
        {
            if ( !slot.lock.tryLock( waitSeconds, TimeUnit.SECONDS ) )
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
            final Source source = this.sources.computeIfAbsent( script.getKey(), key -> buildSource( script ) );
            bindings.forEach( ( key, value ) -> slot.context.getBindings( "js" ).putMember( key, value ) );
            return slot.context.eval( source );
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

    private static Source buildSource( final Resource script )
    {
        try
        {
            final String source = PRE_SCRIPT + script.readString() + POST_SCRIPT;
            return Source.newBuilder( "js", source, script.getKey().toString() ).build();
        }
        catch ( final IOException e )
        {
            throw new UncheckedIOException( e );
        }
    }

    private void onCacheExpired()
    {
        runDisposers();
        // stale sources must not outlive the exports cache — dev-mode reloads re-parse
        this.sources.clear();
    }

    /**
     * The slot to use for conversions requested outside a slot execution — the bound/held slot
     * when inside one, any existing slot otherwise (matching the single-context behavior).
     */
    private ContextSlot currentSlot()
    {
        final ContextSlot bound = BOUND_SLOT.orElse( null );
        if ( bound != null && bound.owner() == this )
        {
            return bound;
        }
        for ( int i = 0; i < slots.length(); i++ )
        {
            final ContextSlot slot = slots.get( i );
            if ( slot != null )
            {
                if ( Thread.holdsLock( slot.context ) )
                {
                    return slot;
                }
            }
        }
        for ( int i = 0; i < slots.length(); i++ )
        {
            final ContextSlot slot = slots.get( i );
            if ( slot != null )
            {
                return slot;
            }
        }
        return slotAt( 0 );
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

        GraalScriptExecutor owner()
        {
            return GraalScriptExecutor.this;
        }

        ContextSlot( final GraalJSContextFactory contextFactory, final ApplicationInfoBuilder application )
        {
            this.scriptValueFactory = new GraalScriptValueFactory( contextFactory, new GraalJavascriptHelperFactory() );
            this.javascriptHelper = this.scriptValueFactory.getJavascriptHelper();
            this.context = this.scriptValueFactory.getContext();
            this.exportsCache = new ScriptExportsCache<>( resourceService::getResource, GraalScriptExecutor.this::onCacheExpired );

            final Map<String, Object> globalVariables = new HashMap<>( scriptSettings.getGlobalVariables() );
            globalVariables.put( "app", ProxyObject.fromMap( application.buildMap( HashMap::new ) ) );
            globalVariables.forEach( ( key, value ) -> this.context.getBindings( "js" ).putMember( key, value ) );
        }
    }
}
