package com.enonic.xp.script.impl;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import org.graalvm.polyglot.Engine;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationListener;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.graal.GraalJSContextFactory;
import com.enonic.xp.script.graal.executor.GraalContextBudget;
import com.enonic.xp.script.graal.executor.GraalScriptExecutor;
import com.enonic.xp.script.impl.async.ScriptAsyncService;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.impl.executor.ScriptExecutorImpl;
import com.enonic.xp.script.impl.function.ApplicationInfoBuilder;
import com.enonic.xp.script.impl.service.ServiceRegistryImpl;
import com.enonic.xp.script.impl.standard.ScriptRuntimeImpl;
import com.enonic.xp.script.runtime.ScriptRuntime;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.server.RunMode;

import static java.util.Objects.requireNonNullElseGet;

@Component
public class ScriptRuntimeFactoryImpl
    implements ScriptRuntimeFactory, ApplicationInvalidator, ApplicationListener
{
    private static final Logger LOG = LoggerFactory.getLogger( ScriptRuntimeFactoryImpl.class );

    private static final String GRAAL_JS_SCRIPT_ENGINE = "GraalJS";

    private static final String NASHORN_SCRIPT_ENGINE = "Nashorn";

    private final List<ScriptRuntimeImpl> list = new CopyOnWriteArrayList<>();

    private final GraalContextBudget graalContextBudget = new GraalContextBudget( maxContexts(), maxTaskContexts() );

    private final ResourceService resourceService;

    private final ScriptAsyncService scriptAsyncService;

    private Engine engine;

    private final BundleContext context;

    @Activate
    public ScriptRuntimeFactoryImpl( final BundleContext context, @Reference final ResourceService resourceService,
                                     @Reference final ScriptAsyncService scriptAsyncService )
    {
        this.context = context;
        this.resourceService = resourceService;
        this.scriptAsyncService = scriptAsyncService;
    }

    @Deactivate
    public void destroy()
    {
        synchronized ( this )
        {
            if ( this.engine != null )
            {
                this.engine.close();
            }
        }
    }

    @Override
    public void invalidate( final ApplicationKey key, final ApplicationInvalidationLevel level )
    {
        this.list.forEach( runtime -> runtime.invalidate( key ) );
    }

    @Override
    public void activated( final Application app )
    {
    }

    @Override
    public void deactivated( final Application app )
    {
        // full instance teardown, not a name-keyed disposer lookup (#10844): the executor of
        // the deactivated incarnation runs its disposers, closes its contexts and returns its
        // budget; a replacement incarnation gets a fresh executor lazily
        this.list.forEach( runtime -> runtime.invalidate( app.getKey() ) );
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
        return new ScriptRuntimeImpl( new ScripExecutorFactory( settings )::create );
    }

    @Override
    public void dispose( final ScriptRuntime runtime )
    {
        this.list.remove( runtime );
        if ( runtime instanceof ScriptRuntimeImpl )
        {
            // instance-owned teardown: run disposers, close contexts, return budget permits
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
     * {@code xp.script-engine.graal.pool-size}. Dev mode stays at 1 so script reloading keeps a
     * single context to invalidate. Above 1, module state is per-context (see the
     * execution-pipeline design doc).
     */
    private static int contextPoolCapacity()
    {
        if ( RunMode.isDev() )
        {
            return 1;
        }
        final Integer poolSize = Integer.getInteger( "xp.script-engine.graal.pool-size" );
        return poolSize != null ? Math.max( 1, poolSize ) : maxContexts();
    }

    private static int maxContexts()
    {
        return Math.max( 1, Integer.getInteger( "xp.script-engine.graal.max-contexts", 200 ) );
    }

    private static int maxTaskContexts()
    {
        return Math.max( 1, Integer.getInteger( "xp.script-engine.graal.max-task-contexts", 100 ) );
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

            final AppBundleData appBundleData = getAppBundleData( applicationKey );

            final String appScriptEngine = normalizeEngineName(
                requireNonNullElseGet( appBundleData.bundle.getHeaders().get( "X-Script-Engine" ),
                                               ScriptRuntimeFactoryImpl::defaultEngineName ) );

            final ClassLoader appClassloader = appBundleData.appClassloader;
            final BundleContext appBundleContext = appBundleData.bundle.getBundleContext();
            final ApplicationInfoBuilder appInfo = appBundleData.appInfo;

            if ( GRAAL_JS_SCRIPT_ENGINE.equals( appScriptEngine ) )
            {
                synchronized ( this )
                {
                    if ( engine == null )
                    {
                        engine = Engine.newBuilder().build();
                    }
                }
                return new GraalScriptExecutor( new GraalJSContextFactory( appClassloader, engine ),
                                                scriptAsyncService.getAsyncExecutor( applicationKey ), appClassloader, settings,
                                                new ServiceRegistryImpl( appBundleContext ), resourceService, appInfo,
                                                contextPoolCapacity(), graalContextBudget );
            }
            else if ( NASHORN_SCRIPT_ENGINE.equals( appScriptEngine ) )
            {
                return new ScriptExecutorImpl( scriptAsyncService.getAsyncExecutor( applicationKey ), appClassloader, settings,
                                               new ServiceRegistryImpl( appBundleContext ), resourceService, appInfo );
            }
            else
            {
                throw new IllegalArgumentException( "Unsupported script engine " + appScriptEngine );
            }
        }
    }

    private AppBundleData getAppBundleData( final ApplicationKey applicationKey )
    {
        ServiceReference<Application> appRef = null;
        try
        {
            final Collection<ServiceReference<Application>> appRefs =
                context.getServiceReferences( Application.class, "(name=" + applicationKey + ")" );
            for ( ServiceReference<Application> ref : appRefs )
            {
                final Bundle aBundle = ref.getBundle();
                if ( aBundle != null && aBundle.getState() == Bundle.ACTIVE )
                {
                    appRef = ref;
                    break;
                }
            }
        }
        catch ( InvalidSyntaxException e )
        {
            throw new RuntimeException( e );
        }

        if ( appRef == null )
        {
            throw new AppNotRegisteredException();
        }
        final Bundle bundle = appRef.getBundle();

        final Application application = context.getService( appRef );
        try
        {
            if ( application == null || application.getConfig() == null || !application.isStarted() )
            {
                throw new AppNotRegisteredException();
            }

            return new AppBundleData( bundle, application.getClassLoader(),
                                      new ApplicationInfoBuilder( applicationKey, application.getConfig(), application.getVersion() ) );
        }
        finally
        {
            context.ungetService( appRef );
        }
    }

    private record AppBundleData(Bundle bundle, ClassLoader appClassloader, ApplicationInfoBuilder appInfo)
    {
    }
}
