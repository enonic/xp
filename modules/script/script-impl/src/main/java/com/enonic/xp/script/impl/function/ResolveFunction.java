package com.enonic.xp.script.impl.function;

import com.enonic.xp.resource.ResourceKey;

public final class ResolveFunction
    extends AbstractFunction
{
    private final ResourceResolver resolver;

    public ResolveFunction( final ResourceKey script )
    {
        super( "resolve" );
        this.resolver = new ResourceResolver( script );
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
        return this.resolver.resolve( name );
    }
}
