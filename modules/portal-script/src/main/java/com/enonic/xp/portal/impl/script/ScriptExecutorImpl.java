package com.enonic.xp.portal.impl.script;

import java.util.Map;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;

import com.google.common.collect.Maps;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

import com.enonic.xp.portal.impl.script.bean.ModuleScriptInfo;
import com.enonic.xp.portal.impl.script.bean.ScriptValueFactoryImpl;
import com.enonic.xp.portal.impl.script.error.ErrorHelper;
import com.enonic.xp.portal.impl.script.function.CallFunction;
import com.enonic.xp.portal.impl.script.function.ExecuteFunction;
import com.enonic.xp.portal.impl.script.function.RequireFunction;
import com.enonic.xp.portal.impl.script.function.ResolveFunction;
import com.enonic.xp.portal.impl.script.invoker.CommandInvoker;
import com.enonic.xp.portal.impl.script.logger.ScriptLogger;
import com.enonic.xp.portal.script.ScriptValue;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;

final class ScriptExecutorImpl
    implements ScriptExecutor
{
    private ScriptEngine engine;

    private CommandInvoker invoker;

    private ResourceKey script;

    private Map<String, Object> globalMap;

    private Bindings global;

    private Map<ResourceKey, Object> exportsCache;

    public void setEngine( final ScriptEngine engine )
    {
        this.engine = engine;
    }

    public void setInvoker( final CommandInvoker invoker )
    {
        this.invoker = invoker;
    }

    public void setScript( final ResourceKey script )
    {
        this.script = script;
    }

    public void setGlobalMap( final Map<String, Object> globalMap )
    {
        this.globalMap = globalMap;
    }

    @Override
    public Object executeMain()
    {
        this.exportsCache = Maps.newHashMap();
        this.global = this.engine.createBindings();

        new CallFunction().register( this.global );

        if ( this.globalMap != null )
        {
            this.global.putAll( this.globalMap );
        }

        return executeRequire( this.script );
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
            final ResolveFunction resolve = new ResolveFunction( script );
            final RequireFunction require = new RequireFunction( script, this );
            final ScriptLogger logger = new ScriptLogger( script );
            final ExecuteFunction execute = new ExecuteFunction( script, this.invoker );
            final ModuleScriptInfo moduleInfo = new ModuleScriptInfo( script );

            return func.call( moduleInfo, script, logger, execute, require, resolve );
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
            return ( (Invocable) this.engine ).invokeMethod( this.global, "__call", func, args );
        }
        catch ( final Exception e )
        {
            throw ErrorHelper.handleError( e );
        }
    }
}
