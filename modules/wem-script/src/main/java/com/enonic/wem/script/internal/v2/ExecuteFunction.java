package com.enonic.wem.script.internal.v2;

import javax.script.Bindings;

import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.ScriptUtils;

import com.enonic.wem.script.v2.CommandInvoker2;

public final class ExecuteFunction
    extends AbstractJSObject
{
    private final CommandInvoker2 invoker;

    public ExecuteFunction( final CommandInvoker2 invoker )
    {
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
        if ( args.length < 1 )
        {
            throw new IllegalArgumentException( "execute(..) must have atleast one parameter" );
        }

        final String name = args[0].toString();
        final Object params = ( args.length > 1 ) ? ScriptUtils.wrap( args[1] ) : null;



        return null;
    }

    public void register( final Bindings bindings )
    {
        bindings.put( "execute2", this );
    }
}
