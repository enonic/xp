package com.enonic.xp.script.impl.function;

import org.graalvm.polyglot.Value;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.impl.executor.ScriptExecutor;

public class ResolveFunction
    extends AbstractFunction
{
    private final ResourceResolver resolver;

    public ResolveFunction( final ResourceKey script, final ScriptExecutor executor )
    {
        super( "resolve" );
        this.resolver = new ResourceResolver( executor.getResourceService(), script );
    }

    @Override
    public Object execute( final Value... args )
    {
        if ( args.length != 1 )
        {
            throw new IllegalArgumentException( "resolve(..) must have one parameter" );
        }

        final String name = args[0].asString();
        return resolve( name );
    }

    private ResourceKey resolve( final String name )
    {
        return this.resolver.resolve( name );
    }
}
