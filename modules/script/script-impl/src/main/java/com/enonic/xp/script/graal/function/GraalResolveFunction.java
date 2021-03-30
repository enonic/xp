package com.enonic.xp.script.graal.function;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.impl.function.ResourceResolver;

public class GraalResolveFunction
    extends GraalAbstractFunction
{
    private final Context context;

    private final ResourceResolver resolver;

    public GraalResolveFunction( final Context context, final ResourceKey script, final ScriptExecutor executor )
    {
        super( "resolve" );
        this.context = context;
        this.resolver = new ResourceResolver( executor.getResourceService(), script );
    }

    @Override
    public Object execute( final Value... args )
    {
        synchronized ( context )
        {
            if ( args.length != 1 )
            {
                throw new IllegalArgumentException( "resolve(..) must have one parameter" );
            }

            final String name = args[0].asString();
            return resolve( name );
        }
    }

    private ResourceKey resolve( final String name )
    {
        return this.resolver.resolve( name );
    }
}
