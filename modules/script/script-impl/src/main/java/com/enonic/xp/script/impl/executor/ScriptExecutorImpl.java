package com.enonic.xp.script.impl.executor;

import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;

import com.google.common.collect.Maps;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import com.enonic.xp.app.Application;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.function.ScriptFunctions;
import com.enonic.xp.script.impl.service.ServiceRegistry;
import com.enonic.xp.script.impl.util.ErrorHelper;
import com.enonic.xp.script.impl.util.JavascriptHelper;
import com.enonic.xp.script.impl.util.JavascriptHelperFactory;
import com.enonic.xp.script.impl.value.ScriptValueFactory;
import com.enonic.xp.script.impl.value.ScriptValueFactoryImpl;
import com.enonic.xp.script.runtime.ScriptSettings;
import com.enonic.xp.server.RunMode;

final class ScriptExecutorImpl
    implements ScriptExecutor
{
    private ScriptEngine engine;

    private ScriptSettings scriptSettings;

    private ScriptExportsCache exportsCache;

    private ClassLoader classLoader;

    private ServiceRegistry serviceRegistry;

    private ResourceService resourceService;

    private Application application;

    private Map<String, Object> mocks;

    private RunMode runMode;

    private ScriptValueFactory scriptValueFactory;

    private JavascriptHelper javascriptHelper;

    public void setEngine( final ScriptEngine engine )
    {
        this.engine = engine;
    }

    public void setScriptSettings( final ScriptSettings scriptSettings )
    {
        this.scriptSettings = scriptSettings;
    }

    public void setClassLoader( final ClassLoader classLoader )
    {
        this.classLoader = classLoader;
    }

    public void setServiceRegistry( final ServiceRegistry serviceRegistry )
    {
        this.serviceRegistry = serviceRegistry;
    }

    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    public void setApplication( final Application application )
    {
        this.application = application;
    }

    public void setRunMode( final RunMode runMode )
    {
        this.runMode = runMode;
    }

    public void initialize()
    {
        this.mocks = Maps.newHashMap();
        this.exportsCache = new ScriptExportsCache();

        final Bindings global = new SimpleBindings();
        global.putAll( this.scriptSettings.getGlobalVariables() );
        this.engine.setBindings( global, ScriptContext.GLOBAL_SCOPE );

        final JavascriptHelperFactory javascriptHelperFactory = new JavascriptHelperFactory( this.engine );
        this.javascriptHelper = javascriptHelperFactory.create();

        this.scriptValueFactory = new ScriptValueFactoryImpl( this.javascriptHelper );
    }

    @Override
    public Object executeRequire( final ResourceKey key )
    {
        final Object mock = this.mocks.get( key.getPath() );
        if ( mock != null )
        {
            return mock;
        }

        final Object cached = this.exportsCache.get( key );
        final Resource resource = loadIfNeeded( key, cached );
        if ( resource == null )
        {
            return cached;
        }

        final SimpleBindings bindings = new SimpleBindings();
        bindings.put( ScriptEngine.FILENAME, getFileName( resource ) );

        final ScriptObjectMirror func = (ScriptObjectMirror) doExecute( bindings, resource );
        final Object result = executeRequire( key, func );

        this.exportsCache.put( resource, result );
        return result;
    }

    private String getFileName( final Resource resource )
    {
        if ( this.scriptSettings.getDebug() != null )
        {
            return this.scriptSettings.getDebug().scriptName( resource );
        }

        return resource.getKey().toString();
    }

    private Object executeRequire( final ResourceKey script, final ScriptObjectMirror func )
    {
        try
        {
            final ScriptFunctions functions = new ScriptFunctions( script, this );
            return func.call( null, functions.getApp(), functions.getLog(), functions.getRequire(), functions.getResolve(), functions );
        }
        catch ( final Exception e )
        {
            throw ErrorHelper.handleError( e );
        }
    }

    @Override
    public ScriptValue newScriptValue( final Object value )
    {
        return this.scriptValueFactory.newValue( value );
    }

    private Object doExecute( final Bindings bindings, final Resource script )
    {
        try
        {
            final String source = InitScriptReader.getScript( script.readString() );
            return this.engine.eval( source, bindings );
        }
        catch ( final Exception e )
        {
            throw ErrorHelper.handleError( e );
        }
    }

    private Resource loadResource( final ResourceKey key )
    {
        return this.resourceService.getResource( key );
    }

    private Resource loadIfNeeded( final ResourceKey key, final Object cached )
    {
        if ( cached == null )
        {
            return loadResource( key );
        }

        if ( this.runMode != RunMode.DEV )
        {
            return null;
        }

        final Resource resource = loadResource( key );
        if ( this.exportsCache.isModified( resource ) )
        {
            return resource;
        }

        return null;
    }

    @Override
    public ClassLoader getClassLoader()
    {
        return this.classLoader;
    }

    @Override
    public ServiceRegistry getServiceRegistry()
    {
        return serviceRegistry;
    }

    @Override
    public ResourceService getResourceService()
    {
        return resourceService;
    }

    @Override
    public Application getApplication()
    {
        return this.application;
    }

    @Override
    public ScriptSettings getScriptSettings()
    {
        return this.scriptSettings;
    }

    @Override
    public void registerMock( final String name, final Object value )
    {
        this.mocks.put( name, value );
    }

    @Override
    public JavascriptHelper getJavascriptHelper()
    {
        return this.javascriptHelper;
    }
}
