package com.enonic.xp.script.impl.executor;

import java.util.concurrent.ConcurrentMap;

import javax.script.ScriptEngine;

import com.google.common.collect.Maps;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.impl.service.ServiceRegistryImpl;
import com.enonic.xp.script.impl.util.NashornHelper;
import com.enonic.xp.script.runtime.ScriptSettings;

public final class ScriptExecutorManager
{
    private final ConcurrentMap<ApplicationKey, ScriptExecutor> executors;

    private ApplicationService applicationService;

    private ResourceService resourceService;

    private ScriptSettings scriptSettings;

    public ScriptExecutorManager()
    {
        this.executors = Maps.newConcurrentMap();
    }

    public ScriptExecutor getExecutor( final ApplicationKey key )
    {
        return this.executors.computeIfAbsent( key, this::createExecutor );
    }

    private ScriptExecutor createExecutor( final ApplicationKey key )
    {
        final Application application = this.applicationService.getApplication( key );
        ClassLoader classLoader = this.applicationService.getClassLoader( application );
        final ScriptEngine engine = NashornHelper.getScriptEngine( classLoader, "-strict" );

        final ScriptExecutorImpl executor = new ScriptExecutorImpl();
        executor.setEngine( engine );
        executor.setScriptSettings( this.scriptSettings );
        executor.setClassLoader( classLoader );
        executor.setServiceRegistry( new ServiceRegistryImpl( application.getBundle().getBundleContext() ) );
        executor.setResourceService( this.resourceService );
        executor.setApplication( application );
        executor.initialize();
        return executor;
    }

    public void invalidate( final ApplicationKey key )
    {
        this.executors.remove( key );
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
