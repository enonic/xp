package com.enonic.xp.script.graal.executor;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Logger LOG = LoggerFactory.getLogger( GraalScriptExecutor.class );

    private static final String PRE_SCRIPT = "(function( log, require, resolve, __, exports, module) { ";

    private static final String POST_SCRIPT = "\n});";

    private static final long SLOT_WAIT_SECONDS = 300;

    /**
     * Pinned executions (websocket/SSE events) run on shared event-dispatch threads — they must
     * fail fast when their slot is saturated instead of holding those threads for minutes. The
     * bound covers the slot-lock wait, which is what grows with queue depth; the context-monitor
     * acquisition that follows is not timed ({@code synchronized} cannot be) and waits out
     * in-flight foreign-thread callbacks ({@code JsFunctionHandle} holds only the monitor) — a
     * tail bounded by callback execution time, not by waiter count. A hard total bound would
     * require replacing the monitor ownership discipline with timed locks.
     */
    private static final long PINNED_SLOT_WAIT_SECONDS = 30;

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
     * Lazily populated, fixed logical capacity: slots retained by live connections leave the
     * request rotation, and growth fills free indices instead.
     */
    private final AtomicReferenceArray<ContextSlot> slots;

    private final Object slotCreationLock = new Object();

    private int budgetedSlots;

    private boolean hasSlots;

    /**
     * Written under {@link #slotCreationLock}, read anywhere (volatile). An execution racing
     * application stop must not resurrect contexts on an executor whose teardown already ran —
     * they would be unreachable from any teardown path and leak.
     */
    private volatile boolean closed;

    /**
     * The dedicated {@code main.js} context — the "main worker rule": the app's bootstrap
     * script, the event listeners it registers and the disposers it leaves behind all share
     * this one context (handles bind to their creating context), and it lives outside the
     * request pool, so requests never disturb it and it never serves requests. Unbudgeted,
     * like the first pooled slot: at most one per application.
     */
    private volatile ContextSlot mainSlot;

    private final AtomicInteger roundRobin = new AtomicInteger();

    /**
     * The slot bound to the current execution scope (nested executions must stay on it). A
     * ScopedValue, aligned with the platform-wide ThreadLocal elimination; shared across
     * executors, so reads check slot ownership — a nested cross-app call must not adopt the
     * outer app's slot.
     */
    private static final ScopedValue<ContextSlot> BOUND_SLOT = ScopedValue.newInstance();

    public GraalScriptExecutor( final GraalJSContextFactory contextFactory, final ClassLoader classLoader,
                                final ScriptSettings scriptSettings, final ServiceRegistry serviceRegistry,
                                final ResourceService resourceService, final ApplicationInfoBuilder application,
                                final int contextPoolCapacity )
    {
        this( contextFactory, classLoader, scriptSettings, serviceRegistry, resourceService, application, contextPoolCapacity,
              GraalContextBudget.unlimited() );
    }

    public GraalScriptExecutor( final GraalJSContextFactory contextFactory, final ClassLoader classLoader,
                                final ScriptSettings scriptSettings, final ServiceRegistry serviceRegistry,
                                final ResourceService resourceService, final ApplicationInfoBuilder application,
                                final int contextPoolCapacity, final GraalContextBudget budget )
    {
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
        return doExecute( key, null );
    }

    @Override
    public ScriptExports bootstrap( final ResourceKey key )
    {
        return doExecute( key, mainSlot() );
    }

    private ScriptExports doExecute( final ResourceKey key, final ContextSlot pinned )
    {
        withSlot( pinned, slot -> {
            if ( RunMode.isDev() )
            {
                slot.exportsCache.expireCacheIfNeeded();
            }
            return requireInSlot( slot, key );
        } );
        return pinned == null ? new GraalScriptExports( this, key ) : GraalScriptExports.pinnedTo( this, key, pinned );
    }

    private ContextSlot mainSlot()
    {
        final ContextSlot existing = this.mainSlot;
        if ( existing != null )
        {
            return existing;
        }
        synchronized ( slotCreationLock )
        {
            requireOpen();
            if ( this.mainSlot == null )
            {
                this.mainSlot = new ContextSlot( contextFactory, application );
            }
            return this.mainSlot;
        }
    }

    private void requireOpen()
    {
        if ( closed )
        {
            throw new IllegalStateException( "Script executor is closed" );
        }
    }

    @Override
    public void executeBackground( final ResourceKey key, final String method, final Object... args )
    {
        // no slot is touched: the call runs in a fresh private context (withIsolatedExports),
        // where the script's top level executes lazily, and nothing is shared with any other
        // call. Named tasks execute this way — a pooled checkout here would make every task run
        // compete with live requests for request-serving slots.
        GraalScriptExports.isolated( this, key ).executeMethodRequired( method, args );
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
        // a disposer only outlives its registration meaningfully on the main context: bootstrap's is
        // stable and torn down with the app. A pool slot's is per-context (one per slot the module
        // loads into) and a task's ephemeral context is already closed by teardown — so ignore both.
        final ContextSlot main = this.mainSlot;
        if ( main == null || heldSlot() != main )
        {
            LOG.warn( "__.disposer is only supported during bootstrap (main.js); ignoring registration from {}", key );
            return;
        }
        this.disposers.computeIfAbsent( key, k -> new ConcurrentLinkedQueue<>() ).add( callback );
    }

    @Override
    public void runDisposers()
    {
        // drain, so each registered disposer runs at most once — teardown calls this twice
        // (invalidate's explicit run, then close())
        this.disposers.forEach( ( key, queue ) -> {
            Runnable disposer;
            while ( ( disposer = queue.poll() ) != null )
            {
                try
                {
                    disposer.run();
                }
                catch ( Exception e )
                {
                    // teardown is best-effort: one bad disposer must neither stop the rest nor
                    // keep close() from freeing contexts and returning budget permits
                    LOG.warn( "Error while running disposer registered by {}", key, e );
                }
            }
        } );
    }

    @Override
    public void close()
    {
        // this executor's disposers run against this executor's contexts, while they are
        // still open (#10844); the queues drain, so a prior run makes this a no-op
        runDisposers();
        synchronized ( slotCreationLock )
        {
            closed = true;
            try
            {
                if ( mainSlot != null )
                {
                    closeContext( mainSlot );
                    mainSlot = null;
                }
                for ( int i = 0; i < slots.length(); i++ )
                {
                    final ContextSlot slot = slots.get( i );
                    if ( slot != null )
                    {
                        closeContext( slot );
                    }
                }
            }
            finally
            {
                // the shared budget must get its permits back even if a context refuses to close —
                // a leaked permit shrinks the global pool for every application, forever
                budget.releaseContexts( budgetedSlots );
                budgetedSlots = 0;
            }
        }
    }

    private static void closeContext( final ContextSlot slot )
    {
        try
        {
            // cancel: a context still executing on another thread (in-flight request, live
            // connection dispatch) must not veto app teardown — the plain close() throws for it.
            // The cancelled execution fails on its own thread; the app is going away regardless.
            slot.context.close( true );
        }
        catch ( RuntimeException e )
        {
            LOG.warn( "Could not close script context", e );
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
        // close() cancels the pooled contexts but leaves the slot entries in place: without this
        // guard an execution racing app stop would lock a cancelled context and fail with a
        // confusing engine error instead of a clear rejection
        requireOpen();
        final ContextSlot bound = boundSlot();
        if ( bound != null && bound.owner() == this )
        {
            return work.apply( bound );
        }

        final ContextSlot held = heldSlot();
        if ( held != null )
        {
            return runBound( held, work );
        }

        if ( pinned != null )
        {
            return lockAndRun( pinned, work, PINNED_SLOT_WAIT_SECONDS );
        }

        return withAnySlot( work );
    }

    private static ContextSlot boundSlot()
    {
        // not orElse(null): ScopedValue.orElse rejects null by spec
        return BOUND_SLOT.isBound() ? BOUND_SLOT.get() : null;
    }

    private ContextSlot heldSlot()
    {
        // listeners and disposers execute on the main context: their nested require() must
        // resolve there, like any other held-monitor callback
        final ContextSlot main = this.mainSlot;
        if ( main != null && Thread.holdsLock( main.context ) )
        {
            return main;
        }
        for ( int i = 0; i < slots.length(); i++ )
        {
            final ContextSlot slot = slots.get( i );
            if ( slot != null && Thread.holdsLock( slot.context ) )
            {
                return slot;
            }
        }
        return null;
    }

    private <T> T withAnySlot( final Function<ContextSlot, T> work )
    {
        final int start = this.roundRobin.getAndIncrement();
        final int size = slots.length();
        for ( int i = 0; i < size; i++ )
        {
            final ContextSlot slot = slots.get( Math.floorMod( start + i, size ) );
            // retained slots belong to live connections (websocket/SSE): the request pool
            // leaves them alone and grows replacements instead. The untimed tryLock deliberately
            // barges past the lock's fairness on this fast path — only anonymous requests race
            // here (no ordering requirement); every queued wait, pinned events included, uses the
            // timed tryLock below, which honors fairness, and retained/main slots never enter
            // this scan at all
            if ( slot != null && !slot.isRetained() && slot.lock.tryLock() )
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
        return lockAndRun( grownOrExistingSlot( start, size ), work, SLOT_WAIT_SECONDS );
    }

    private ContextSlot grownOrExistingSlot( final int start, final int size )
    {
        // every eligible slot is busy: grow within the budget
        for ( int i = 0; i < size; i++ )
        {
            final int index = Math.floorMod( start + i, size );
            if ( slots.get( index ) == null )
            {
                final ContextSlot created = slotAt( index );
                if ( created != null )
                {
                    return created;
                }
                break;
            }
        }
        // at capacity or out of budget: wait fairly on an existing free slot
        ContextSlot retainedFallback = null;
        for ( int i = 0; i < size; i++ )
        {
            final ContextSlot slot = slots.get( Math.floorMod( start + i, size ) );
            if ( slot != null )
            {
                if ( !slot.isRetained() )
                {
                    return slot;
                }
                retainedFallback = slot;
            }
        }
        // liveness over exclusivity: when every existing slot is retained by a connection and
        // nothing can grow, sharing a retained slot beats starving requests
        if ( retainedFallback != null )
        {
            return retainedFallback;
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
     * execution model for background (named-task) runs: task threads are virtual and effectively unbounded,
     * and an IO-waiting task holds its context for the entire wait, so tasks must not compete
     * for request-serving slots. Bounded by the task-context budget; the shared source registry
     * keeps re-initialization parse-free.
     */
    <T> T withIsolatedExports( final ResourceKey key, final BiFunction<ContextSlot, Value, T> work )
    {
        requireOpen();
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

    private ContextSlot slotAt( final int index )
    {
        final ContextSlot existing = slots.get( index );
        if ( existing != null )
        {
            return existing;
        }
        synchronized ( slotCreationLock )
        {
            requireOpen();
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
            try
            {
                slot = new ContextSlot( contextFactory, application );
            }
            catch ( RuntimeException e )
            {
                if ( budgeted )
                {
                    budget.releaseContexts( 1 );
                }
                throw e;
            }
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
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt();
            throw new RuntimeException( "Script require failed: [" + key + "]", e );
        }
        catch ( TimeoutException e )
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
        // disposers are NOT drained here: only bootstrap (main context) can register them, and a
        // dev-mode reload re-executes controllers, never main.js — draining would run the app's
        // teardown mid-life and leave nothing for the actual stop
        // stale sources must not outlive the exports cache — dev-mode reloads re-parse
        this.sources.clear();
    }

    /**
     * The slot to use for conversions requested outside a slot execution — the bound/held slot
     * when inside one, any existing slot otherwise (matching the single-context behavior).
     */
    private ContextSlot currentSlot()
    {
        final ContextSlot bound = boundSlot();
        if ( bound != null && bound.owner() == this )
        {
            return bound;
        }
        final ContextSlot held = heldSlot();
        if ( held != null )
        {
            return held;
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

        /**
         * Live connections referencing this slot. While positive, the slot serves only its
         * connections' events (and executions explicitly pinned to it) — the request pool
         * skips it, except as a last resort when nothing else exists.
         */
        private final AtomicInteger pins = new AtomicInteger();

        final GraalScriptValueFactory scriptValueFactory;

        final JavascriptHelper<Value> javascriptHelper;

        final Context context;

        final ScriptExportsCache<Value> exportsCache;

        GraalScriptExecutor owner()
        {
            return GraalScriptExecutor.this;
        }

        void retain()
        {
            pins.incrementAndGet();
        }

        void release()
        {
            pins.updateAndGet( count -> Math.max( 0, count - 1 ) );
        }

        boolean isRetained()
        {
            return pins.get() > 0;
        }

        @SuppressWarnings("deprecation") // globalVariables kept for the xp-testing harness only
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
