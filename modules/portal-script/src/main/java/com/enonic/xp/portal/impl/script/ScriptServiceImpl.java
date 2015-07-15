package com.enonic.xp.portal.impl.script;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import javax.script.ScriptEngine;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Maps;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.module.Module;
import com.enonic.xp.module.ModuleService;
import com.enonic.xp.module.ModuleUpdatedEvent;
import com.enonic.xp.portal.impl.script.service.ServiceRegistryImpl;
import com.enonic.xp.portal.impl.script.util.NashornHelper;
import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.portal.script.ScriptService;
import com.enonic.xp.portal.script.ScriptValue;
import com.enonic.xp.resource.ResourceKey;

@Component(immediate = true, service = {ScriptService.class, EventListener.class})
public final class ScriptServiceImpl
    implements ScriptService, EventListener
{
    private final Map<String, Object> globalMap;

    private final ConcurrentMap<ApplicationKey, ScriptExecutor> executors;

    private ModuleService moduleService;

    public ScriptServiceImpl()
    {
        this.globalMap = Maps.newHashMap();
        this.executors = Maps.newConcurrentMap();
    }

    public void addGlobalVariable( final String key, final Object value )
    {
        this.globalMap.put( key, value );
    }

    @Override
    public ScriptExports execute( final ResourceKey script )
    {
        final ScriptExecutor executor = getExecutor( script.getApplicationKey() );

        final Object exports = executor.executeRequire( script );
        final ScriptValue value = executor.newScriptValue( exports );
        return new ScriptExportsImpl( script, value, exports );
    }

    private ScriptExecutor getExecutor( final ApplicationKey key )
    {
        return this.executors.computeIfAbsent( key, this::createExecutor );
    }

    private ScriptExecutor createExecutor( final ApplicationKey key )
    {
        final Module module = this.moduleService.getModule( key );
        ClassLoader classLoader = moduleService.getClassLoader( module );
        final ScriptEngine engine = NashornHelper.getScriptEngine( classLoader, "-strict" );

        final ScriptExecutorImpl executor = new ScriptExecutorImpl();
        executor.setEngine( engine );
        executor.setGlobalMap( this.globalMap );
        executor.setClassLoader( classLoader );
        executor.setServiceRegistry( new ServiceRegistryImpl( module.getBundle().getBundleContext() ) );
        executor.initialize();
        return executor;
    }

    private void invalidate( final ApplicationKey key )
    {
        this.executors.remove( key );
    }

    @Override
    public void onEvent( final Event event )
    {
        if ( event instanceof ModuleUpdatedEvent )
        {
            invalidate( ( (ModuleUpdatedEvent) event ).getApplicationKey() );
        }
    }

    @Reference
    public void setModuleService( final ModuleService moduleService )
    {
        this.moduleService = moduleService;
    }
}
