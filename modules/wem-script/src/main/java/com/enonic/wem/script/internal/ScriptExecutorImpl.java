package com.enonic.wem.script.internal;

import java.util.Map;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.ScriptObject;
import com.enonic.wem.script.internal.bean.ScriptObjectImpl;
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
    private ScriptEngine engine;

    private CommandInvoker invoker;

    private ResourceKey script;

    private Map<String, Object> globalMap;

    private Bindings bindings;

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
        this.bindings = this.engine.createBindings();
        final Bindings exports = this.engine.createBindings();
        this.bindings.put( "exports", exports );

        new ScriptLogger( this.script ).register( this.bindings );
        new ResolveFunction( this.script ).register( this.bindings );
        new ExecuteFunction( this.script, this.invoker ).register( this.bindings );
        new RequireFunction( this.script, this ).register( this.bindings );
        new CallFunction().register( this.bindings );

        if ( this.globalMap != null )
        {
            this.bindings.putAll( this.globalMap );
        }

        doExecute();
        return exports;
    }

    @Override
    public Object executeRequire( final ResourceKey script )
    {
        final ScriptExecutorImpl executor = new ScriptExecutorImpl();
        executor.engine = this.engine;
        executor.globalMap = this.globalMap;
        executor.invoker = this.invoker;
        executor.script = script;
        return executor.executeMain();
    }

    @Override
    public ScriptObject newScriptValue( final Object value )
    {
        return new ScriptObjectImpl( value, this::invokeMethod );
    }

    private void doExecute()
    {
        try
        {
            final Resource resource = Resource.from( this.script );
            final String source = resource.readString();

            this.bindings.put( ScriptEngine.FILENAME, this.script.toString() );
            this.engine.eval( source, this.bindings );
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
            return ( (Invocable) this.engine ).invokeMethod( this.bindings, "__call", func, args );
        }
        catch ( final Exception e )
        {
            throw ErrorHelper.handleError( e );
        }
    }
}
