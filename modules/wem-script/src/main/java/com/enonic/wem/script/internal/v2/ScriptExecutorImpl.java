package com.enonic.wem.script.internal.v2;

import java.io.IOException;
import java.net.URL;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;

final class ScriptExecutorImpl
    implements ScriptExecutor
{
    private final ScriptEngine engine;

    private final Invocable invocable;

    private final String globalScript;

    public ScriptExecutorImpl( final ScriptEngine engine )
    {
        this.engine = engine;
        this.invocable = (Invocable) this.engine;
        this.globalScript = loadScript( "global.js" );
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
            final Resource resource = Resource.from( script );
            final String source = resource.readString();

            this.engine.eval( this.globalScript, bindings );
            this.engine.eval( source, bindings );
        }
        catch ( final ScriptException e )
        {
            throw Throwables.propagate( e );
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
        catch ( final NoSuchMethodException | ScriptException e )
        {
            throw Throwables.propagate( e );
        }
    }
}
