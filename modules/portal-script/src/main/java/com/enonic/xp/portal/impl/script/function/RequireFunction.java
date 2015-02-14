package com.enonic.xp.portal.impl.script.function;

import javax.script.Bindings;

import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.portal.impl.script.ScriptExecutor;

public final class RequireFunction
    extends AbstractFunction
{
    private final static String SCRIPT_SUFFIX = ".js";

    private final ResourceKey script;

    private final ScriptExecutor executor;

    public RequireFunction( final ResourceKey script, final ScriptExecutor executor )
    {
        this.script = script;
        this.executor = executor;
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

        if ( name.startsWith( "/" ) )
        {
            return this.script.resolve( name );
        }

        if ( name.startsWith( "./" ) )
        {
            return this.script.resolve( "../" + name );
        }

        final ResourceKey resolved = this.script.resolve( "../" + name );
        if ( Resource.from( resolved ).exists() )
        {
            return resolved;
        }

        return this.script.resolve( "/cms/lib/" + name );
    }

    @Override
    public void register( final Bindings bindings )
    {
        bindings.put( "require", this );
    }
}
