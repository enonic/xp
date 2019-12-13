package com.enonic.xp.script.impl.executor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
    private final ConcurrentMap<ApplicationKey, ScriptExecutor> executors;

    private ApplicationService applicationService;

    private ResourceService resourceService;

    private ScriptSettings scriptSettings;

    public ScriptExecutorManager()
    {
        this.executors = new ConcurrentHashMap<>();
    }

    public ScriptExecutor getExecutor( final ApplicationKey key )
    {
        return this.executors.computeIfAbsent( key, this::createExecutor );
    }

    private ScriptExecutor createExecutor( final ApplicationKey key )
    {
        final Application application = this.applicationService.getInstalledApplication( key );

        if ( application == null )
        {
            throw new ApplicationNotFoundException( key );
        }

        final ClassLoader classLoader = application.getClassLoader();

        final ScriptExecutorImpl executor = new ScriptExecutorImpl();
        executor.setScriptSettings( this.scriptSettings );
        executor.setClassLoader( classLoader );
        executor.setServiceRegistry( new ServiceRegistryImpl( application.getBundle().getBundleContext() ) );
        executor.setResourceService( this.resourceService );
        executor.setApplication( application );
        executor.setRunMode( RunMode.get() );
        executor.initialize();
        return executor;
    }

    public void invalidate( final ApplicationKey key )
    {
        final ScriptExecutor executor = this.executors.remove( key );
        if ( executor != null )
        {
            executor.runDisposers();
        }
    }

    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    public void setScriptSettings( final ScriptSettings scriptSettings )
    {
        this.scriptSettings = scriptSettings;
    }
}
