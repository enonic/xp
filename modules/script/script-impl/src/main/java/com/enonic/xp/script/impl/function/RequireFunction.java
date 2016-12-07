package com.enonic.xp.script.impl.function;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.impl.executor.ScriptExecutor;

public final class RequireFunction
    extends AbstractFunction
{
    private final ScriptExecutor executor;

    private final RequireResolver resolver;

    public RequireFunction( final ResourceKey script, final ScriptExecutor executor )
    {
        super( "require" );
        this.executor = executor;
        this.resolver = new RequireResolver( this.executor.getResourceService(), script );
    }

    @Override
    public Object call( final Object thiz, final Object... args )
    {
        if ( args.length != 1 )
        {
            throw new IllegalArgumentException( "require(..) must have one parameter" );
        }

        final String name = args[0].toString();
        final ResourceKey key = resolve( name );

        return this.executor.executeRequire( key );
    }

    private ResourceKey resolve( final String name )
    {
        return this.resolver.resolve( name );
    }
}
