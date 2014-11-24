package com.enonic.wem.script.internal;

import javax.script.Bindings;

import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.api.resource.ResourceKey;

public final class ScriptModuleScope
{
    private final static String SCRIPT_SUFFIX = ".js";

    private final ResourceKey script;

    private final ScriptExecutor executor;

    public ScriptModuleScope( final ResourceKey script, final ScriptExecutor executor )
    {
        this.script = script;
        this.executor = executor;
    }

    public ResourceKey resolve( final String name )
    {
        if ( name.startsWith( "/" ) )
        {
            return this.script.resolve( name );
        }
        else
        {
            return this.script.resolve( "../" + name );
        }
    }

    public ResourceKey resolveScript( final String name )
    {
        if ( !name.endsWith( SCRIPT_SUFFIX ) )
        {
            return resolveScript( name + SCRIPT_SUFFIX );
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

        return this.script.resolve( "/lib/" + name );
    }

    public Object require( final String name )
    {
        final ResourceKey key = resolveScript( name );
        final ScriptModuleScope scope = new ScriptModuleScope( key, this.executor );
        return scope.executeThis();
    }

    private Bindings createBindings()
    {
        final Bindings bindings = this.executor.createBindings();
        bindings.put( "__helper", this );
        return bindings;
    }

    public Bindings executeThis()
    {
        final Bindings bindings = createBindings();
        this.executor.execute( bindings, this.script );

        final Object exports = bindings.get( "exports" );
        if ( exports instanceof Bindings )
        {
            return (Bindings) exports;
        }

        return null;
    }
}
