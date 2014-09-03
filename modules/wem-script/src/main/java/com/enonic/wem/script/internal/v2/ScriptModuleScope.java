package com.enonic.wem.script.internal.v2;

import javax.script.Bindings;

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
        if ( name.contains( ":" ) )
        {
            return ResourceKey.from( name );
        }
        else if ( name.startsWith( "/" ) )
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

        return resolve( name );
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
