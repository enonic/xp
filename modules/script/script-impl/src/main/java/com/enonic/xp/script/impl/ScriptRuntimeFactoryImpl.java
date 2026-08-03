package com.enonic.xp.script.impl;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.graalvm.polyglot.Engine;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.graal.GraalJSContextFactory;
import com.enonic.xp.script.graal.executor.GraalContextBudget;
import com.enonic.xp.script.graal.executor.GraalScriptExecutor;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.impl.executor.ScriptExecutorImpl;
import com.enonic.xp.script.impl.function.ApplicationInfoBuilder;
import com.enonic.xp.script.impl.service.ServiceRegistryImpl;
import com.enonic.xp.script.impl.standard.ScriptRuntimeImpl;
import com.enonic.xp.script.runtime.ScriptRuntime;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.script.runtime.ScriptSettings;

import static java.util.Objects.requireNonNullElseGet;

// tracking applications is an implementation detail; only the factory itself is a service
@Component(service = ScriptRuntimeFactory.class)
public class ScriptRuntimeFactoryImpl
    implements ScriptRuntimeFactory, ServiceTrackerCustomizer<Application, Application>
{
    private static final Logger LOG = LoggerFactory.getLogger( ScriptRuntimeFactoryImpl.class );

    private static final String GRAAL_JS_SCRIPT_ENGINE = "GraalJS";

    private static final String NASHORN_SCRIPT_ENGINE = "Nashorn";

    private final List<ScriptRuntimeImpl> list = new CopyOnWriteArrayList<>();

    private final GraalContextBudget graalContextBudget =
        new GraalContextBudget( maxContexts(), maxRetainedContexts(), maxIsolatedContexts() );

    /**
     * The current incarnation of each application: its live service registration and the service
     * it delivered. Maintained by the tracker; executor creation resolves through it (no service
     * registry scan), and runtimes revalidate their executors against it on every use. The
     * {@link ServiceReference} doubles as the incarnation identity — application reconfigure
     * re-registers the <em>same</em> {@code Application} object under a <em>new</em> registration,
     * so the registration is what actually changes per incarnation, not the service object.
     */
    private final ConcurrentMap<ApplicationKey, TrackedApplication> apps = new ConcurrentHashMap<>();

    private final ResourceService resourceService;

    private final Object engineLock = new Object();

    private volatile Engine engine;

    private final BundleContext context;

    private final ServiceTracker<Application, Application> tracker;

    @Activate
    public ScriptRuntimeFactoryImpl( final BundleContext context, @Reference final ResourceService resourceService )
    {
        this.context = context;
        this.resourceService = resourceService;
        this.tracker = new ServiceTracker<>( context, Application.class, this );
        this.tracker.open();
    }

    @Deactivate
    public void destroy()
    {
        this.tracker.close();
        this.apps.clear();
        // consumers normally dispose their runtimes before this component deactivates; sweep
        // whatever remains so disposers run and contexts close before the shared engine does
        this.list.forEach( ScriptRuntimeImpl::close );
        this.list.clear();
        synchronized ( engineLock )
        {
            if ( this.engine != null )
            {
                this.engine.close();
            }
        }
    }

    /**
     * The engine shared by every GraalJS application, created on first use. One engine for the
     * whole installation is what makes its code cache shared: a script parsed for one application
     * context is reused by every other context built from it (pool growth, isolated runs). Two
     * engines would silently halve that reuse and leak the one nobody closes.
     */
    Engine sharedEngine()
    {
        Engine result = this.engine;
        if ( result == null )
        {
            synchronized ( engineLock )
            {
                result = this.engine;
                if ( result == null )
                {
                    result = Engine.newBuilder().build();
                    this.engine = result;
                }
            }
        }
        return result;
    }

    @Override
    public Application addingService( final ServiceReference<Application> reference )
    {
        final Application application = this.context.getService( reference );
        if ( application == null )
        {
            return null;
        }
        this.apps.put( application.getKey(), new TrackedApplication( reference, application ) );
        return application;
    }

    @Override
    public void modifiedService( final ServiceReference<Application> reference, final Application application )
    {
    }

    @Override
    public void removedService( final ServiceReference<Application> reference, final Application application )
    {
        // this fires synchronously inside unregister(): on reconfigure that is BEFORE the
        // replacement registers, so a key-wide teardown can never hit a successor incarnation
        this.apps.remove( application.getKey(), new TrackedApplication( reference, application ) );
        this.list.forEach( runtime -> runtime.invalidate( application.getKey() ) );
        this.context.ungetService( reference );
    }

    @Override
    public ScriptRuntime create( final ScriptSettings settings )
    {
        final ScriptRuntimeImpl runtime = doCreate( settings );

        this.list.add( runtime );
        return runtime;
    }

    ScriptRuntimeImpl doCreate( final ScriptSettings settings )
    {
        return new ScriptRuntimeImpl( new ScripExecutorFactory( settings )::create, this::currentIncarnation );
    }

    /**
     * The identity of the application's current service registration, or {@code null} when the
     * application is not registered. Runtimes compare their executors' creation-time incarnation
     * against this on every use, so an executor built from a registration that is gone dies on
     * its next touch instead of serving a stopped or replaced application.
     */
    private Object currentIncarnation( final ApplicationKey key )
    {
        final TrackedApplication tracked = this.apps.get( key );
        return tracked == null ? null : tracked.reference();
    }

    @Override
    public void dispose( final ScriptRuntime runtime )
    {
        this.list.remove( runtime );
        if ( runtime instanceof ScriptRuntimeImpl )
        {
            ( (ScriptRuntimeImpl) runtime ).close();
        }
    }

    private static String defaultEngineName()
    {
        return normalizeEngineName( System.getProperty( "xp.script-engine", NASHORN_SCRIPT_ENGINE ) );
    }

    /**
     * Logical GraalJS slot capacity per application. Slots are created
     * lazily on demand within the global cross-app budget, so capacity is cheap; it defaults to
     * the global maximum and can be overridden per installation with
     * {@code xp.script-engine.graal.pool-size}. Dev mode uses the same capacity: a retained slot
     * (live websocket/SSE connection) is never shared, so a capacity of one would freeze the
     * whole application behind a single open connection. Script reloading is unaffected — each
     * slot's exports cache expires lazily on that slot's next execution. Above 1, module state
     * is per-context.
     */
    private static int contextPoolCapacity()
    {
        final Integer poolSize = Integer.getInteger( "xp.script-engine.graal.pool-size" );
        // growth beyond the first slot is budgeted, so capacity above the global budget is
        // unreachable: clamp rather than allocate entries that can never materialize
        return poolSize != null ? Math.min( maxContexts(), Math.max( 1, poolSize ) ) : maxContexts();
    }

    private static int maxContexts()
    {
        return Math.max( 1, Integer.getInteger( "xp.script-engine.graal.max-contexts", 1024 ) );
    }

    /**
     * Cap on contexts retained by live connections (websocket/SSE) — one permit per connection.
     * Defaults to half the context budget, so connections can never consume the whole pool:
     * request serving always keeps headroom, and the marginal connection is what gets rejected.
     */
    private static int maxRetainedContexts()
    {
        return Math.max( 1, Integer.getInteger( "xp.script-engine.graal.max-retained-contexts", maxContexts() / 2 ) );
    }

    private static int maxIsolatedContexts()
    {
        return Math.max( 1, Integer.getInteger( "xp.script-engine.graal.max-isolated-contexts", 1024 ) );
    }

    private static String normalizeEngineName( final String scriptEngine )
    {
        final String se = scriptEngine.toLowerCase( Locale.ROOT );
        if ( se.equalsIgnoreCase( GRAAL_JS_SCRIPT_ENGINE ) )
        {
            return GRAAL_JS_SCRIPT_ENGINE;
        }
        else if ( se.equalsIgnoreCase( NASHORN_SCRIPT_ENGINE ) )
        {
            return NASHORN_SCRIPT_ENGINE;
        }
        else
        {
            throw new IllegalArgumentException( "Unsupported script engine " + scriptEngine );
        }
    }

    private class ScripExecutorFactory
    {
        final ScriptSettings settings;

        ScripExecutorFactory( final ScriptSettings settings )
        {
            this.settings = settings;
        }

        ScriptExecutor create( final ApplicationKey applicationKey )
        {
            LOG.debug( "Create Script Executor for {}", applicationKey );

            final TrackedApplication tracked = apps.get( applicationKey );
            if ( tracked == null )
            {
                throw new AppNotRegisteredException();
            }
            final Application application = tracked.application();
            final Bundle bundle = tracked.reference().getBundle();
            if ( bundle == null || application.getConfig() == null || !application.isStarted() )
            {
                throw new AppNotRegisteredException();
            }

            final String appScriptEngine = normalizeEngineName(
                requireNonNullElseGet( bundle.getHeaders().get( "X-Script-Engine" ), ScriptRuntimeFactoryImpl::defaultEngineName ) );

            final ClassLoader appClassloader = application.getClassLoader();
            final BundleContext appBundleContext = bundle.getBundleContext();
            final ApplicationInfoBuilder appInfo =
                new ApplicationInfoBuilder( applicationKey, application.getConfig(), application.getVersion() );

            if ( GRAAL_JS_SCRIPT_ENGINE.equals( appScriptEngine ) )
            {
                // the engine follows the first context, not the executor: an application without
                // scripts is bootstrapped like any other, and must not bring an engine to life
                return new GraalScriptExecutor( new GraalJSContextFactory( appClassloader, ScriptRuntimeFactoryImpl.this::sharedEngine ),
                                                appClassloader, settings,
                                                new ServiceRegistryImpl( appBundleContext ), resourceService, appInfo,
                                                contextPoolCapacity(), graalContextBudget );
            }
            else if ( NASHORN_SCRIPT_ENGINE.equals( appScriptEngine ) )
            {
                return new ScriptExecutorImpl( appClassloader, settings, new ServiceRegistryImpl( appBundleContext ), resourceService,
                                               appInfo );
            }
            else
            {
                throw new IllegalArgumentException( "Unsupported script engine " + appScriptEngine );
            }
        }
    }

    private record TrackedApplication(ServiceReference<Application> reference, Application application)
    {
    }
}
