package com.enonic.xp.portal.impl.script;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

import javax.script.ScriptEngine;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.Maps;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.ApplicationUpdatedEvent;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.portal.impl.script.service.ServiceRegistryImpl;
import com.enonic.xp.portal.impl.script.util.NashornHelper;
import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.portal.script.ScriptService;
import com.enonic.xp.portal.script.ScriptValue;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

@Component(immediate = true, service = {ScriptService.class, EventListener.class})
public final class ScriptServiceImpl
    implements ScriptService, EventListener
{
    private final Map<String, Object> globalMap;

    private final ConcurrentMap<ApplicationKey, ScriptExecutor> executors;

    private ApplicationService applicationService;

    private ResourceService resourceService;

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
        final Application application = this.applicationService.getModule( key );
        ClassLoader classLoader = applicationService.getClassLoader( application );
        final ScriptEngine engine = NashornHelper.getScriptEngine( classLoader, "-strict" );

        final ScriptExecutorImpl executor = new ScriptExecutorImpl();
        executor.setEngine( engine );
        executor.setGlobalMap( this.globalMap );
        executor.setClassLoader( classLoader );
        executor.setServiceRegistry( new ServiceRegistryImpl( application.getBundle().getBundleContext() ) );
        executor.setResourceService( this.resourceService );
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
        if ( event instanceof ApplicationUpdatedEvent )
        {
            invalidate( ( (ApplicationUpdatedEvent) event ).getApplicationKey() );
        }
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
