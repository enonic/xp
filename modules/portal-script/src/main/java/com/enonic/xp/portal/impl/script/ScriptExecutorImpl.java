package com.enonic.xp.portal.impl.script;

import java.util.Map;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;

import com.google.common.collect.Maps;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import com.enonic.xp.bean.BeanManager;
import com.enonic.xp.portal.impl.script.bean.ScriptValueFactoryImpl;
import com.enonic.xp.portal.impl.script.error.ErrorHelper;
import com.enonic.xp.portal.impl.script.function.CallFunction;
import com.enonic.xp.portal.impl.script.function.ScriptFunctions;
import com.enonic.xp.portal.impl.script.invoker.CommandInvoker;
import com.enonic.xp.portal.script.ScriptValue;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;

final class ScriptExecutorImpl
    implements ScriptExecutor
{
    private ScriptEngine engine;

    private CommandInvoker invoker;

    private Map<String, Object> globalMap;

    private Bindings global;

    private Map<ResourceKey, Object> exportsCache;

    private BeanManager beanManager;

    public void setEngine( final ScriptEngine engine )
    {
        this.engine = engine;
    }

    public void setInvoker( final CommandInvoker invoker )
    {
        this.invoker = invoker;
    }

    public void setGlobalMap( final Map<String, Object> globalMap )
    {
        this.globalMap = globalMap;
    }

    public void setBeanManager( final BeanManager beanManager )
    {
        this.beanManager = beanManager;
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
            return func.call( null, functions.getLog(), functions.getExecute(), functions.getRequire(), functions.getResolve(), functions );
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
            final Resource resource = Resource.from( script );
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
    public CommandInvoker getInvoker()
    {
        return this.invoker;
    }

    @Override
    public BeanManager getBeanManager()
    {
        return this.beanManager;
    }
}
