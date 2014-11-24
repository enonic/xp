package com.enonic.wem.script.internal.function;

import javax.script.Bindings;

import jdk.nashorn.api.scripting.AbstractJSObject;

import com.enonic.wem.api.resource.ResourceKey;

public final class ResolveFunction
    extends AbstractJSObject
{
    private final ResourceKey script;

    public ResolveFunction( final ResourceKey script )
    {
        this.script = script;
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
        if ( args.length != 1 )
        {
            throw new IllegalArgumentException( "resolve(..) must have one parameter" );
        }

        final String name = args[0].toString();
        return resolve( name );
    }

    private ResourceKey resolve( final String name )
    {
        if ( name.startsWith( "/" ) )
        {
            return this.script.resolve( name );
        }
        else
        {
            return this.script.resolve( "../" + name );
        }
    }

    public void register( final Bindings bindings )
    {
        bindings.put( "resolve", this );
    }
}
