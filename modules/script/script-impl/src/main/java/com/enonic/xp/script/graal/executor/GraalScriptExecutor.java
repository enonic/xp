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
     * Unused in dev mode, which compiles every Source fresh so edits are picked up through the
     * engine's content-keyed caching.
     */
    private final Map<ResourceKey, Source> sources = new ConcurrentHashMap<>();

    /**
     * Lazily populated up to a fixed logical capacity, and populated <em>densely</em>: growth
     * always takes the next free index, so the live slots are exactly {@code [0, createdSlots)}.
     * Slots retained by live connections stay in place and leave the request rotation.
     */
    private final AtomicReferenceArray<ContextSlot> slots;

    private final Object slotCreationLock = new Object();

    private int budgetedSlots;

    /**
     * How much of {@link #slots} is populated, and the bound of every scan. Written under
     * {@link #slotCreationLock} after the slot itself is in the array, read anywhere (volatile).
     */
    private volatile int createdSlots;

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
     * The slot bound to the current execution scope (nested executions must stay on it). Shared
     * across executors, so reads check slot ownership — a nested cross-app call must not adopt
     * the outer app's slot.
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
    public Object executeMethod( final ResourceKey key, final String method, final Object... args )
    {
        return GraalScriptExports.isolated( this, key ).executeMethodRequired( method, args );
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
                catch ( Exception | StackOverflowError e )
                {
                    // teardown is best-effort: one bad disposer — including deeply recursive
                    // user JS — must neither stop the rest nor keep close() from freeing
                    // contexts and returning budget permits
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
                // a connection whose terminal event never arrives would hold its permit past its
                // executor's death: drain every slot's outstanding pins and return them — a late
                // endpoint release finds no pin to remove and returns nothing
                if ( mainSlot != null )
                {
                    closeContext( mainSlot );
                    budget.releaseRetainedContexts( mainSlot.drainPins() );
                    mainSlot = null;
                }
                // the whole array, not createdSlots: teardown must not depend on the
                // dense-population invariant to free a context
                for ( int i = 0; i < slots.length(); i++ )
                {
                    final ContextSlot slot = slots.get( i );
                    if ( slot != null )
                    {
                        closeContext( slot );
                        budget.releaseRetainedContexts( slot.drainPins() );
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
            return lockAndRunInterruptibly( pinned, work );
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
        final int size = createdSlots;
        for ( int i = 0; i < size; i++ )
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
        final int size = createdSlots;
        for ( int i = 0; i < size; i++ )
        {
            final ContextSlot slot = slots.get( Math.floorMod( start + i, size ) );
            // retained slots belong to live connections (websocket/SSE); the request pool leaves
            // them alone and grows replacements. Ordering does not matter on this fast path, so
            // the untimed tryLock may barge; ordered waits go through the fair paths below.
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
        return lockAndRun( grownOrExistingSlot( start ), work, SLOT_WAIT_SECONDS );
    }

    private ContextSlot grownOrExistingSlot( final int start )
    {
        // every eligible slot is busy: grow within capacity and the budget
        final boolean atCapacity = createdSlots >= slots.length();
        if ( !atCapacity )
        {
            final ContextSlot created = growSlot();
            if ( created != null )
            {
                return created;
            }
        }
        // at capacity or out of budget: wait fairly on an existing free slot. Re-read the count —
        // a concurrent growth may have added one since the check above
        final int size = createdSlots;
        for ( int i = 0; i < size; i++ )
        {
            final ContextSlot slot = slots.get( Math.floorMod( start + i, size ) );
            if ( slot != null && !slot.isRetained() )
            {
                return slot;
            }
        }
        final boolean budgetDenied = !atCapacity;
        // exclusivity over liveness: a retained context carries one connection's module state,
        // and GraalJS offers no way to share it safely — a request never intrudes on it, it
        // fails loudly instead
        throw new IllegalStateException( "No script context available for application [" + application.appKey() +
                                             "]: every pooled context is retained by a live websocket/SSE connection, and the pool cannot grow — " +
                                             ( budgetDenied
                                                 ? "the global context budget is exhausted (xp.script-engine.graal.max-contexts)"
                                                 : "the pool is at capacity (" + slots.length() +
                                                     ", xp.script-engine.graal.pool-size)" ) );
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
     * execution model for isolated runs ({@code executeMethod}, named tasks): their threads are
     * virtual and effectively unbounded, and an IO-waiting run holds its context for the entire
     * wait, so isolated runs must not compete for request-serving slots. Bounded by the
     * isolated-context budget; the shared source registry keeps re-initialization parse-free.
     */
    <T> T withIsolatedExports( final ResourceKey key, final BiFunction<ContextSlot, Value, T> work )
    {
        requireOpen();
        budget.acquireIsolatedContext();
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
                // cancel + swallow, like the pooled teardown: a callback the script handed out
                // could hold this context on another thread, and a throwing close here would
                // mask the invocation's primary outcome
                closeContext( slot );
            }
        }
        finally
        {
            budget.releaseIsolatedContext();
        }
    }

    /**
     * Adds one slot at the end of the created prefix, or answers {@code null} when the pool is at
     * capacity or the global budget is exhausted.
     */
    private ContextSlot growSlot()
    {
        synchronized ( slotCreationLock )
        {
            requireOpen();
            final int index = createdSlots;
            if ( index >= slots.length() )
            {
                return null;
            }
            // an app's first slot is always allowed — a full global budget must not lock a
            // fresh application out; only growth beyond it is budgeted
            final boolean budgeted = index > 0;
            if ( budgeted && !budget.tryAcquireContext() )
            {
                return null;
            }
            final ContextSlot slot;
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
            // published last: a reader that sees the count sees the slot behind it
            createdSlots = index + 1;
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

    /**
     * Pinned executions (websocket/SSE events, and anything queued behind them) wait for their
     * exact slot without a time bound: a pinned execution has exactly one legal context, so a
     * timeout could only break the connection sooner. The wait stays interruptible. The
     * context-monitor acquisition that follows is untimed as well ({@code synchronized}) and
     * waits out in-flight foreign-thread callbacks.
     */
    private <T> T lockAndRunInterruptibly( final ContextSlot slot, final Function<ContextSlot, T> work )
    {
        try
        {
            slot.lock.lockInterruptibly();
        }
        catch ( final InterruptedException e )
        {
            Thread.currentThread().interrupt();
            throw new RuntimeException( "Interrupted while waiting for the pinned script context", e );
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
            // `this` is the module's exports inside a module body (CommonJS, and what the other
            // engine binds): calling the wrapper without a receiver leaves `this` undefined under
            // strict mode, so `this.foo = ...` at module level fails instead of exporting
            func.invokeMember( "call", exports, functions.getLog(), functions.getRequire(), functions.getResolve(), functions, exports,
                               module );
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
            // dev mode compiles fresh: the engine's code cache is content-keyed, so an unchanged
            // file still parses once while an edited one misses by construction — the name-keyed
            // strong registry could hand a stale Source to a fresh context (an isolated run, a
            // grown slot) in the window before a request-path expiry check notices the edit
            final Source source = RunMode.isDev()
                ? buildSource( script )
                : this.sources.computeIfAbsent( script.getKey(), key -> buildSource( script ) );
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
        // teardown mid-life and leave nothing for the actual stop. The source registry needs no
        // clearing either: dev mode never populates it (every compile builds a fresh Source)
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
        final int size = createdSlots;
        for ( int i = 0; i < size; i++ )
        {
            final ContextSlot slot = slots.get( i );
            if ( slot != null )
            {
                return slot;
            }
        }
        // no slot exists yet: the first one is unbudgeted, so growth cannot be refused here
        return growSlot();
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
         * never touches it.
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
            // throws when the connection budget is exhausted: the failed open propagates to the
            // endpoint and rejects the marginal connection — requests are not the victim
            budget.acquireRetainedContext();
            pins.incrementAndGet();
        }

        void release()
        {
            // backstop guard: the view layer already confines each release to its own
            // successful retains, so a pin is normally always here to remove
            final int before = pins.getAndUpdate( count -> Math.max( 0, count - 1 ) );
            if ( before > 0 )
            {
                budget.releaseRetainedContext();
            }
        }

        boolean isRetained()
        {
            return pins.get() > 0;
        }

        int drainPins()
        {
            return pins.getAndSet( 0 );
        }

        @SuppressWarnings("deprecation") // globalVariables kept for the xp-testing harness only
        ContextSlot( final GraalJSContextFactory contextFactory, final ApplicationInfoBuilder application )
        {
            this.scriptValueFactory = new GraalScriptValueFactory( contextFactory, new GraalJavascriptHelperFactory() );
            this.javascriptHelper = this.scriptValueFactory.getJavascriptHelper();
            this.context = this.scriptValueFactory.getContext();
            this.exportsCache = new ScriptExportsCache<>( resourceService::getResource, GraalScriptExecutor.this::onCacheExpired );

            final Map<String, Object> globalVariables = new HashMap<>( scriptSettings.getGlobalVariables() );
            globalVariables.put( "app", this.javascriptHelper.objectConverter().toJs( application.buildMap( HashMap::new ) ) );
            globalVariables.forEach( ( key, value ) -> this.context.getBindings( "js" ).putMember( key, value ) );
            this.context.eval( "js", "Object.defineProperty( globalThis, 'app', { writable: false, configurable: false } );" );
        }
    }
}
