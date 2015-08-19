package com.enonic.xp.script.impl.executor;

import java.util.Map;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;

import com.google.common.collect.Maps;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.script.impl.function.CallFunction;
import com.enonic.xp.script.impl.function.ScriptFunctions;
import com.enonic.xp.script.impl.service.ServiceRegistry;
import com.enonic.xp.script.impl.util.ErrorHelper;
import com.enonic.xp.script.impl.value.ScriptValueFactoryImpl;

final class ScriptExecutorImpl
    implements ScriptExecutor
{
    private ScriptEngine engine;

    private Map<String, Object> globalMap;

    private Bindings global;

    private Map<ResourceKey, Object> exportsCache;

    private ClassLoader classLoader;

    private ServiceRegistry serviceRegistry;

    private ResourceService resourceService;

    public void setEngine( final ScriptEngine engine )
    {
        this.engine = engine;
    }

    public void setGlobalMap( final Map<String, Object> globalMap )
    {
        this.globalMap = globalMap;
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

    public void initialize()
    {
        this.exportsCache = Maps.newHashMap();
        this.global = this.engine.createBindings();
        new CallFunction().register( this.global );

        if ( this.globalMap != null )
        {
            this.global.putAll( this.globalMap );
        }
    }

    @Override
    public Object executeRequire( final ResourceKey script )
    {
        final Object cached = this.exportsCache.get( script );
        if ( cached != null )
        {
            return cached;
        }

        final ScriptContextImpl context = new ScriptContextImpl( script );
        context.setEngineScope( this.global );
        context.setGlobalScope( new SimpleBindings() );

        final ScriptObjectMirror func = (ScriptObjectMirror) doExecute( context, script );
        final Object result = executeRequire( script, func );

        this.exportsCache.put( script, result );
        return result;
    }

    private Object executeRequire( final ResourceKey script, final ScriptObjectMirror func )
    {
        try
        {
            final ScriptFunctions functions = new ScriptFunctions( script, this );
            return func.call( null, functions.getLog(), functions.getRequire(), functions.getResolve(), functions );
        }
        catch ( final Exception e )
        {
            throw ErrorHelper.handleError( e );
        }
    }

    @Override
    public ScriptValue newScriptValue( final Object value )
    {
        return new ScriptValueFactoryImpl( this::invokeMethod ).newValue( value );
    }

    private Object doExecute( final ScriptContext context, final ResourceKey script )
    {
        try
        {
            final Resource resource = this.resourceService.getResource( script );
            final String source = InitScriptReader.getScript( resource.readString() );

            return this.engine.eval( source, context );
        }
        catch ( final Exception e )
        {
            throw ErrorHelper.handleError( e );
        }
    }

    private Object invokeMethod( final Object func, final Object... args )
    {
        try
        {
            return ( (Invocable) this.engine ).invokeMethod( this.global, CallFunction.NAME, func, args );
        }
        catch ( final Exception e )
        {
            throw ErrorHelper.handleError( e );
        }
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
}
