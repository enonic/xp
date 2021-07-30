package com.enonic.xp.script.impl.executor;

import java.util.Objects;

import org.graalvm.polyglot.Engine;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationNotFoundException;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.impl.async.ScriptAsyncService;
import com.enonic.xp.script.impl.service.ServiceRegistryImpl;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.server.RunMode;

public class ScriptExecutorFactory
{
    private static final Logger LOG = LoggerFactory.getLogger( ScriptExecutorFactory.class );

    private final ApplicationService applicationService;

    private final ResourceService resourceService;

    private final ScriptAsyncService scriptAsyncService;

    private final ScriptSettings scriptSettings;

    private final Engine engine;

    public ScriptExecutorFactory( final Engine engine, final ApplicationService applicationService,
                                  final ResourceService resourceService, final ScriptAsyncService scriptAsyncService,
                                  final ScriptSettings scriptSettings )
    {
        this.engine = engine;
        this.applicationService = applicationService;
        this.resourceService = resourceService;
        this.scriptAsyncService = scriptAsyncService;
        this.scriptSettings = scriptSettings;
    }

    public ScriptExecutor create( final ApplicationKey applicationKey )
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

        return new ScriptExecutorImpl( engine, scriptAsyncService.getAsyncExecutor( application.getKey() ),
                                       scriptSettings, new ServiceRegistryImpl( bundleContext ), resourceService, application,
                                       RunMode.get() );
    }
}
