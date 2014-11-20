package com.enonic.wem.script.internal;

import java.io.IOException;
import java.net.URL;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;

import jdk.nashorn.api.scripting.NashornException;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.api.resource.ResourceProblemException;
import com.enonic.wem.script.command.CommandInvoker;
import com.enonic.wem.script.command.CommandInvoker2;
import com.enonic.wem.script.internal.logger.ScriptLogger;
import com.enonic.wem.script.internal.v2.ExecuteFunction;

final class ScriptExecutorImpl
    implements ScriptExecutor
{
    private final static String GLOBAL_SCRIPT = "global.js";

    private final ScriptEngine engine;

    private final Invocable invocable;

    private final String globalScript;

    private final CommandInvoker invoker;

    private final CommandInvoker2 invoker2;

    public ScriptExecutorImpl( final ScriptEngine engine, final CommandInvoker invoker, final CommandInvoker2 invoker2 )
    {
        this.engine = engine;
        this.invocable = (Invocable) this.engine;
        this.globalScript = loadScript( GLOBAL_SCRIPT );
        this.invoker = invoker;
        this.invoker2 = invoker2;
    }

    @Override
    public Bindings createBindings()
    {
        return this.engine.createBindings();
    }

    @Override
    public void execute( final Bindings bindings, final ResourceKey script )
    {
        try
        {
            new ScriptLogger( script ).register( bindings );
            new ExecuteFunction( script, this.invoker2 ).register( bindings );

            final Resource resource = Resource.from( script );
            final String source = resource.readString();

            bindings.put( ScriptEngine.FILENAME, GLOBAL_SCRIPT );
            this.engine.eval( this.globalScript, bindings );

            bindings.put( ScriptEngine.FILENAME, script.toString() );
            this.engine.eval( source, bindings );
        }
        catch ( final ScriptException e )
        {
            throw handleException( e );
        }
        catch ( final RuntimeException e )
        {
            throw handleException( e );
        }
    }

    private String loadScript( final String name )
    {
        try
        {
            final URL url = getClass().getResource( name );
            return Resources.toString( url, Charsets.UTF_8 );
        }
        catch ( final IOException e )
        {
            throw Throwables.propagate( e );
        }
    }

    @Override
    public Object invokeMethod( final Object scope, final String name, final Object... args )
    {
        try
        {
            return this.invocable.invokeMethod( scope, name, args );

        }
        catch ( final NoSuchMethodException e )
        {
            return null;
        }
        catch ( final ScriptException e )
        {
            throw handleException( e );
        }
    }

    @Override
    public CommandInvoker getInvoker()
    {
        return this.invoker;
    }

    private ResourceProblemException handleException( final ScriptException e )
    {
        final ResourceProblemException.Builder builder = ResourceProblemException.newBuilder();
        builder.cause( e.getCause() );
        builder.lineNumber( e.getLineNumber() );
        builder.resource( ResourceKey.from( e.getFileName() ) );
        return builder.build();
    }

    private RuntimeException handleException( final RuntimeException e )
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
        for ( final StackTraceElement element : elements )
        {
            if ( !element.getFileName().equals( GLOBAL_SCRIPT ) )
            {
                return element;
            }
        }

        return null;
    }
}
