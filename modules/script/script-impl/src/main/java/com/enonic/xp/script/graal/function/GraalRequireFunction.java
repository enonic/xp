package com.enonic.xp.script.graal.function;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.impl.function.RequireResolver;

public class GraalRequireFunction
    extends GraalAbstractFunction
{
    private final Context context;

    private final ScriptExecutor executor;

    private final RequireResolver resolver;

    protected GraalRequireFunction( final Context context, final ResourceKey script, final ScriptExecutor executor )
    {
        super( "require" );
        this.context = context;
        this.executor = executor;
        this.resolver = new RequireResolver( this.executor.getResourceService(), script );
    }

    @Override
    public Object execute( final Value... args )
    {
        synchronized ( context )
        {
            if ( args.length != 1 )
            {
                throw new IllegalArgumentException( "require(..) must have one parameter" );
            }

            final String name = args[0].toString();
            final ResourceKey key = resolve( name );

            return this.executor.executeRequire( key );
        }
    }

    private ResourceKey resolve( final String name )
    {
        return this.resolver.resolve( name );
    }
}
