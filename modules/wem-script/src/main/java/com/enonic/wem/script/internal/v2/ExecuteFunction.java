package com.enonic.wem.script.internal.v2;

import javax.script.Bindings;

import jdk.nashorn.api.scripting.AbstractJSObject;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.command.CommandInvoker2;

public final class ExecuteFunction
    extends AbstractJSObject
{
    private final ResourceKey script;

    private final CommandInvoker2 invoker;

    public ExecuteFunction( final ResourceKey script, final CommandInvoker2 invoker )
    {
        this.script = script;
        this.invoker = invoker;
    }

    @Override
    public boolean isFunction()
    {
        return true;
    }

    @Override
    public boolean isStrictFunction()
    {
        return true;
    }

    @Override
    public Object call( final Object thiz, final Object... args )
    {
        if ( args.length != 2 )
        {
            throw new IllegalArgumentException( "execute2(..) must have two parameters" );
        }

        final String name = args[0].toString();
        final Object params = args[1];

        final CommandRequestImpl request = new CommandRequestImpl();
        request.setName( name );
        request.setScript( this.script );
        request.setParamsMap( ScriptObjectConverter.toMap( params ) );
        return this.invoker.invoke( request );
    }

    public void register( final Bindings bindings )
    {
        bindings.put( "execute2", this );
    }
}
