package com.enonic.wem.script.internal;

import java.util.Map;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.SimpleBindings;

import com.google.common.collect.Maps;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.ScriptValue;
import com.enonic.wem.script.internal.bean.ScriptValueFactoryImpl;
import com.enonic.wem.script.internal.error.ErrorHelper;
import com.enonic.wem.script.internal.function.CallFunction;
import com.enonic.wem.script.internal.function.ExecuteFunction;
import com.enonic.wem.script.internal.function.RequireFunction;
import com.enonic.wem.script.internal.function.ResolveFunction;
import com.enonic.wem.script.internal.invoker.CommandInvoker;
import com.enonic.wem.script.internal.logger.ScriptLogger;

final class ScriptExecutorImpl
    implements ScriptExecutor
{
    private final static String PRE_SCRIPT = "" + //
        "'use strict';" + //
        "(function(exports) {";

    private final static String POST_SCRIPT = "" + //
        "return exports;" + //
        "})({});";

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

        final Bindings bindings = new SimpleBindings();
        new ResolveFunction( script ).register( bindings );
        new RequireFunction( script, this ).register( bindings );
        new ScriptLogger( script ).register( bindings );
        new ExecuteFunction( script, this.invoker ).register( bindings );

        final ScriptContextImpl context = new ScriptContextImpl( script );
        context.setEngineScope( this.global );
        context.setGlobalScope( bindings );

        final Object result = doExecute( context, script );
        this.exportsCache.put( script, result );
        return result;
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
            final String source = PRE_SCRIPT + resource.readString() + POST_SCRIPT;

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
