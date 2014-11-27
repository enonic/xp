package com.enonic.wem.script.internal;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import jdk.nashorn.api.scripting.NashornException;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.api.util.Exceptions;
import com.enonic.wem.script.internal.function.ExecuteFunction;
import com.enonic.wem.script.internal.function.RequireFunction;
import com.enonic.wem.script.internal.function.ResolveFunction;
import com.enonic.wem.script.internal.invoker.CommandInvoker;
import com.enonic.wem.script.internal.logger.ScriptLogger;
import com.enonic.wem.script.internal.util.JsObjectConverter;

final class ScriptExecutorImpl
    implements ScriptExecutor
{
    private final ScriptEngine engine;

    private final Invocable invocable;

    private final CommandInvoker invoker;

    public ScriptExecutorImpl( final ScriptEngine engine, final CommandInvoker invoker )
    {
        this.engine = engine;
        this.invocable = (Invocable) this.engine;
        this.invoker = invoker;
    }

    private Bindings createBindings()
    {
        return this.engine.createBindings();
    }

    private void doExecute( final Bindings bindings, final ResourceKey script )
    {
        try
        {
            final Resource resource = Resource.from( script );
            final String source = resource.readString();

            bindings.put( ScriptEngine.FILENAME, script.toString() );
            this.engine.eval( source, bindings );
        }
        catch ( final Exception e )
        {
            throw handleException( e );
        }
    }

    @Override
    public Bindings executeRequire( final ResourceKey script )
    {
        final Bindings bindings = createBindings();

        final Bindings exports = createBindings();
        bindings.put( "exports", exports );

        new ScriptLogger( script ).register( bindings );
        new ResolveFunction( script ).register( bindings );
        new ExecuteFunction( script, this.invoker ).register( bindings );
        new RequireFunction( script, this ).register( bindings );

        doExecute( bindings, script );
        return exports;
    }

    @Override
    public Object invokeMethod( final Object scope, final String name, final Object... args )
    {
        try
        {
            final Object result = this.invocable.invokeMethod( scope, name, args );
            return JsObjectConverter.fromJs( result );

        }
        catch ( final NoSuchMethodException e )
        {
            return null;
        }
        catch ( final Exception e )
        {
            throw handleException( e );
        }
    }

    private RuntimeException handleException( final Exception e )
    {
        if ( e instanceof ResourceProblemException )
        {
            return (ResourceProblemException) e;
        }

        if ( e instanceof ScriptException )
        {
            return doHandleException( (ScriptException) e );
        }

        if ( e instanceof RuntimeException )
        {
            return doHandleException( (RuntimeException) e );
        }

        return Exceptions.unchecked( e );
    }

    private ResourceProblemException doHandleException( final ScriptException e )
    {
        final ResourceProblemException.Builder builder = ResourceProblemException.newBuilder();
        builder.cause( e.getCause() );
        builder.lineNumber( e.getLineNumber() );
        builder.resource( ResourceKey.from( e.getFileName() ) );
        return builder.build();
    }

    private RuntimeException doHandleException( final RuntimeException e )
    {
        final StackTraceElement elem = findScriptTraceElement( e );
        if ( elem == null )
        {
            return e;
        }

        final ResourceProblemException.Builder builder = ResourceProblemException.newBuilder();
        builder.cause( e );
        builder.lineNumber( elem.getLineNumber() );
        builder.resource( ResourceKey.from( elem.getFileName() ) );
        return builder.build();
    }

    private StackTraceElement findScriptTraceElement( final RuntimeException e )
    {
        final StackTraceElement[] elements = NashornException.getScriptFrames( e );
        return elements.length > 0 ? elements[0] : null;
    }
}
