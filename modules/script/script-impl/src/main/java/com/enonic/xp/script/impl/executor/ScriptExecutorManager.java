package com.enonic.xp.script.impl.executor;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationNotFoundException;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.impl.service.ServiceRegistryImpl;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.server.RunMode;

public final class ScriptExecutorManager
{
    private static final Logger LOG = LoggerFactory.getLogger( ScriptExecutorManager.class );

    private final ConcurrentMap<ApplicationKey, ScriptExecutor> executors;

    private final ApplicationService applicationService;

    private final ResourceService resourceService;

    private final ScriptSettings scriptSettings;

    public ScriptExecutorManager( final ApplicationService applicationService, final ResourceService resourceService,
                                  final ScriptSettings scriptSettings )
    {
        this.executors = new ConcurrentHashMap<>();
        this.applicationService = applicationService;
        this.resourceService = resourceService;
        this.scriptSettings = scriptSettings;
    }

    public ScriptExecutor getExecutor( final ApplicationKey key )
    {
        return this.executors.computeIfAbsent( key, this::createExecutor );
    }

    private ScriptExecutor createExecutor( final ApplicationKey key )
    {
        LOG.debug( "Create Script Executor for {}", key );
        final Application application = this.applicationService.getInstalledApplication( key );

        if ( application == null )
        {
            throw new ApplicationNotFoundException( key );
        }

        final ClassLoader classLoader = application.getClassLoader();

        final Bundle bundle = application.getBundle();
        final BundleContext bundleContext = Objects.requireNonNull( bundle.getBundleContext(),
                                                                    String.format( "application bundle %s context must not be null",
                                                                                   bundle.getBundleId() ) );

        final ScriptExecutorImpl executor = new ScriptExecutorImpl();
        executor.setScriptSettings( this.scriptSettings );
        executor.setClassLoader( classLoader );
        executor.setServiceRegistry( new ServiceRegistryImpl( bundleContext ) );
        executor.setResourceService( this.resourceService );
        executor.setApplication( application );
        executor.setRunMode( RunMode.get() );
        executor.initialize();
        return executor;
    }

    public void runDisposers( final ApplicationKey key )
    {
        final ScriptExecutor executor = this.executors.get( key );
        if ( executor != null )
        {
            LOG.debug( "Run script disposers for {}", key );
            executor.runDisposers();
        }
    }

    public void invalidate( final ApplicationKey key )
    {
        LOG.debug( "Remove Script Executor for {}", key );
        this.executors.remove( key );
    }
}
