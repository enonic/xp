package com.enonic.xp.script.impl;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

import org.graalvm.polyglot.Engine;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
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
import com.enonic.xp.app.ApplicationNotFoundException;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.graal.GraalJSContextFactory;
import com.enonic.xp.script.graal.executor.GraalScriptExecutor;
import com.enonic.xp.script.impl.async.ScriptAsyncService;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.impl.executor.ScriptExecutorImpl;
import com.enonic.xp.script.impl.service.ServiceRegistryImpl;
import com.enonic.xp.script.impl.standard.ScriptRuntimeImpl;
import com.enonic.xp.script.runtime.ScriptRuntime;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.server.RunMode;

@Component
public class ScriptRuntimeFactoryImpl
    implements ScriptRuntimeFactory, ApplicationInvalidator, ApplicationListener
{
    private static final Logger LOG = LoggerFactory.getLogger( ScriptRuntimeFactoryImpl.class );

    private static final String GRAAL_JS_SCRIPT_ENGINE = "GraalJS";

    private static final String NASHORN_SCRIPT_ENGINE = "Nashorn";

    private final List<ScriptRuntimeImpl> list = new CopyOnWriteArrayList<>();

    private final ApplicationService applicationService;

    private final ResourceService resourceService;

    private final ScriptAsyncService scriptAsyncService;

    private Engine engine;

    @Activate
    public ScriptRuntimeFactoryImpl( @Reference final ApplicationService applicationService,
                                     @Reference final ResourceService resourceService,
                                     @Reference final ScriptAsyncService scriptAsyncService )
    {
        this.applicationService = applicationService;
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
    public void invalidate( final ApplicationKey key )
    {
        invalidate( key, ApplicationInvalidationLevel.FULL );
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
        this.list.forEach( runtime -> runtime.runDisposers( app.getKey() ) );
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
    }

    private static String defaultEngineName()
    {
        return normalizeEngineName( System.getProperty( "xp.script-engine", NASHORN_SCRIPT_ENGINE ) );
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

    private class ScripExecutorFactory {
        final ScriptSettings settings;

        ScripExecutorFactory( final ScriptSettings settings )
        {
            this.settings = settings;
        }

        ScriptExecutor create( final ApplicationKey applicationKey )
        {
            LOG.debug( "Create Script Executor for {}", applicationKey );
            final Application application = applicationService.getInstalledApplication( applicationKey );

            if ( application == null || !application.isStarted() || application.getConfig() == null )
            {
                throw new ApplicationNotFoundException( applicationKey );
            }
            final Bundle bundle = application.getBundle();
            final BundleContext bundleContext = Objects.requireNonNull( bundle.getBundleContext(),
                                                                        String.format( "application bundle %s context must not be null",
                                                                                       bundle.getBundleId() ) );

            final String appScriptEngine = normalizeEngineName( Objects.requireNonNullElseGet( bundle.getHeaders().get( "X-Script-Engine" ),
                                                                                               ScriptRuntimeFactoryImpl::defaultEngineName ) );

            if ( GRAAL_JS_SCRIPT_ENGINE.equals( appScriptEngine ) )
            {
                synchronized ( this )
                {
                    if ( engine == null )
                    {
                        final Engine.Builder builder = Engine.newBuilder();

                        if ( Boolean.getBoolean( "xp.script-engine.nashorn-compat" ) )
                        {
                            builder.allowExperimentalOptions( true );
                        }
                        engine = Engine.newBuilder().build();
                    }
                }
                return new GraalScriptExecutor( new GraalJSContextFactory( application.getClassLoader(), engine ),
                                                scriptAsyncService.getAsyncExecutor( application.getKey() ), settings,
                                                new ServiceRegistryImpl( bundleContext ), resourceService, application, RunMode.get() );
            }
            else if ( NASHORN_SCRIPT_ENGINE.equals( appScriptEngine ) )
            {
                return new ScriptExecutorImpl( scriptAsyncService.getAsyncExecutor( application.getKey() ), settings,
                                               application.getClassLoader(), new ServiceRegistryImpl( bundleContext ), resourceService,
                                               application, RunMode.get() );
            }
            else
            {
                throw new IllegalArgumentException( "Unsupported script engine " + appScriptEngine );
            }
        }
    }
}
