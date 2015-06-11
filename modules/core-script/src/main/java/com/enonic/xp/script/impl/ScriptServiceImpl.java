package com.enonic.xp.script.impl;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;

import com.google.common.collect.Maps;

import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptService;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.impl.executor.ScriptExecutorFactory;

public final class ScriptServiceImpl
    implements ScriptService, BundleListener
{
    private final Map<ModuleKey, ScriptExecutor> executors;

    private ModuleService moduleService;

    private ScriptExecutorFactory scriptExecutorFactory;

    private BundleContext bundleContext;

    public ScriptServiceImpl()
    {
        this.executors = Maps.newConcurrentMap();
    }

    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }

    public void setScriptExecutorFactory( final ScriptExecutorFactory scriptExecutorFactory )
    {
        this.scriptExecutorFactory = scriptExecutorFactory;
    }

    public void setBundleContext( final BundleContext bundleContext )
    {
        this.bundleContext = bundleContext;
    }

    @Override
    public ScriptExports execute( final ResourceKey script )
    {
        final ScriptExecutor executor = findExecutor( script.getModule() );
        return executor.execute( script );
    }

    private ScriptExecutor findExecutor( final ModuleKey key )
    {
        return this.executors.computeIfAbsent( key, this::createExecutor );
    }

    private ScriptExecutor createExecutor( final ModuleKey key )
    {
        final Module module = this.moduleService.getModule( key );
        module.checkIfStarted();

        return this.scriptExecutorFactory.newExecutor( module );
    }

    private void invalidate( final ModuleKey key )
    {
        this.executors.remove( key );
    }

    @Override
    public void bundleChanged( final BundleEvent event )
    {
        invalidate( ModuleKey.from( event.getBundle() ) );
    }

    public void start()
    {
        this.bundleContext.addBundleListener( this );
    }
}
