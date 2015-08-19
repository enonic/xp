package com.enonic.xp.script.impl.function;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.impl.executor.ScriptExecutor;

public final class ResolveFunction
    extends AbstractFunction
{
    private final ResourceKey script;

    private final String basePath;

    public ResolveFunction( final ResourceKey script, final ScriptExecutor executor )
    {
        super( "resolve" );
        this.script = script;
        this.basePath = executor.getScriptSettings().getBasePath();
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
            return this.script.resolve( this.basePath + name );
        }
        else
        {
            return this.script.resolve( "../" + name );
        }
    }
}
