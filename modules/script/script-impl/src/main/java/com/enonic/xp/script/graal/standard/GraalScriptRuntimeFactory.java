package com.enonic.xp.script.graal.standard;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.graalvm.polyglot.Engine;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.condition.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidationLevel;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationListener;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.graal.executor.GraalScriptExecutorFactory;
import com.enonic.xp.script.impl.async.ScriptAsyncService;
import com.enonic.xp.script.impl.standard.ScriptRuntimeImpl;
import com.enonic.xp.script.runtime.ScriptRuntime;
import com.enonic.xp.script.runtime.ScriptRuntimeFactory;
import com.enonic.xp.script.runtime.ScriptSettings;

@Component
public class GraalScriptRuntimeFactory
    implements ScriptRuntimeFactory, ApplicationInvalidator, ApplicationListener
{
    private static final Logger LOG = LoggerFactory.getLogger( GraalScriptRuntimeFactory.class );

    private final List<ScriptRuntimeImpl> list = new CopyOnWriteArrayList<>();

    private final ApplicationService applicationService;

    private final ResourceService resourceService;

    private final ScriptAsyncService scriptAsyncService;

    private final Engine engine;

    @Reference(target = "(osgi.condition.id=GraalJS)")
    private Condition graaljsCondition;

    @Activate
    public GraalScriptRuntimeFactory( @Reference final ApplicationService applicationService,
                                      @Reference final ResourceService resourceService,
                                      @Reference final ScriptAsyncService scriptAsyncService )
    {
        final Engine.Builder builder = Engine.newBuilder();

        if ( Boolean.getBoolean( "xp.script-engine.nashorn-compat" ) )
        {
            builder.allowExperimentalOptions( true );
        }
        this.engine = builder.build();
        this.applicationService = applicationService;
        this.resourceService = resourceService;
        this.scriptAsyncService = scriptAsyncService;
    }

    @Deactivate
    public void destroy()
    {
        this.engine.close();
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

    @Override
    public void dispose( final ScriptRuntime runtime )
    {
        this.list.remove( runtime );
    }

    ScriptRuntimeImpl doCreate( final ScriptSettings settings )
    {
        final GraalScriptExecutorFactory scriptExecutorFactory =
            new GraalScriptExecutorFactory( engine, applicationService, resourceService, scriptAsyncService, settings );

        return new ScriptRuntimeImpl( scriptExecutorFactory::create );
    }
}
