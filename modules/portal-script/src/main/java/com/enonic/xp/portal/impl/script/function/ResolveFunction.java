package com.enonic.xp.portal.impl.script.function;

import javax.script.Bindings;

import com.enonic.xp.core.resource.ResourceKey;

public final class ResolveFunction
    extends AbstractFunction
{
    private final ResourceKey script;

    public ResolveFunction( final ResourceKey script )
    {
        this.script = script;
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

    @Override
    public void register( final Bindings bindings )
    {
        bindings.put( "resolve", this );
    }
}
