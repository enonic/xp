package com.enonic.xp.script.impl.function;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.impl.executor.ScriptExecutor;

public final class RequireFunction
    extends AbstractFunction
{
    private final static String SCRIPT_SUFFIX = ".js";

    private final static String DEFAULT_RESOURCE = "/index.js";

    private final ResourceKey script;

    private final ScriptExecutor executor;

    private final String basePath;

    public RequireFunction( final ResourceKey script, final ScriptExecutor executor )
    {
        super( "require" );
        this.script = script;
        this.executor = executor;
        this.basePath = this.executor.getScriptSettings().getBasePath();
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
            ResourceKey resolved = resolve( name + SCRIPT_SUFFIX );
            if ( this.executor.getResourceService().getResource( resolved ).exists() )
            {
                return resolved;
            }
            else
            {
                return resolve( name + DEFAULT_RESOURCE );
            }
        }

        if ( name.startsWith( "/" ) )
        {
            return this.script.resolve( this.basePath + name );
        }

        if ( name.startsWith( "./" ) )
        {
            return this.script.resolve( "../" + name );
        }

        final ResourceKey resolved = this.script.resolve( "../" + name );
        if ( this.executor.getResourceService().getResource( resolved ).exists() )
        {
            return resolved;
        }

        return this.script.resolve( this.basePath + "/lib/" + name );
    }
}
