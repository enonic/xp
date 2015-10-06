package com.enonic.xp.script.impl.function;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeyResolver;

public final class ResolveFunction
    extends AbstractFunction
{
    private final ResourceKey script;

    private final ResourceKeyResolver resourceKeyResolver;

    public ResolveFunction( final ResourceKey script, final ResourceKeyResolver resourceKeyResolver )
    {
        super( "resolve" );
        this.script = script;
        this.resourceKeyResolver = resourceKeyResolver;
    }

    @Override
    public Object call( final Object thiz, final Object... args )
    {
        if ( args.length != 1 )
        {
            throw new IllegalArgumentException( "resolve(..) must have one parameter" );
        }

        final String name = args[0].toString();
        return this.resourceKeyResolver.resolve( this.script, name );
    }
}
