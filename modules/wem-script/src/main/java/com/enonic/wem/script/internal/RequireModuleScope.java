package com.enonic.wem.script.internal;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.TopLevel;

import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.script.internal.ScriptEnvironment;

final class RequireModuleScope
    extends TopLevel
{
    private final static String SCRIPT_SUFFIX = ".js";

    private final ResourceKey resource;

    private final ScriptEnvironment environment;

    public RequireModuleScope( final Scriptable prototype, final ResourceKey resource, final ScriptEnvironment environment )
    {
        this.resource = resource;
        this.environment = environment;
        setPrototype( prototype );
    }

    public ResourceKey getResource()
    {
        return this.resource;
    }

    public ResourceKey resolveScript( final String name )
    {
        final ResourceKey lib = this.environment.getLibrary( name );
        if ( lib != null )
        {
            return lib;
        }

        if ( !name.endsWith( SCRIPT_SUFFIX ) )
        {
            return resolveScript( name + SCRIPT_SUFFIX );
        }

        return resolveResource( name );
    }

    public ResourceKey resolveResource( final String name )
    {
        try
        {
            return ResourceKey.from( name );
        }
        catch ( final Exception e )
        {
            return this.resource.resolve( "../" + name );
        }
    }
}
