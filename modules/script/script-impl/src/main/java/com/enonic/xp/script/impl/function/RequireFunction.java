package com.enonic.xp.script.impl.function;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeyResolver;
import com.enonic.xp.script.impl.executor.ScriptExecutor;

public final class RequireFunction
    extends AbstractFunction
{
    private final static String SCRIPT_SUFFIX = ".js";

    private final ResourceKey script;

    private final ScriptExecutor executor;

    private final ResourceKeyResolver resourceKeyResolver;

    public RequireFunction( final ResourceKey script, final ScriptExecutor executor )
    {
        super( "require" );
        this.script = script;
        this.executor = executor;
        this.resourceKeyResolver = this.executor.getResourceKeyResolver();
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
        if ( !name.endsWith( SCRIPT_SUFFIX ) )
        {
            return resolve( name + SCRIPT_SUFFIX );
        }

        final ResourceKey resolved = this.resourceKeyResolver.resolve( this.script, name );
        if ( this.executor.getResourceService().getResource( resolved ).exists() )
        {
            return resolved;
        }

        return this.resourceKeyResolver.resolve( this.script, "/lib/" + name );
    }
}
