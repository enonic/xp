package com.enonic.wem.portal.script.runner;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.TopLevel;

import com.enonic.wem.api.resource.ResourceKey;

final class RequireModuleScope
    extends TopLevel
{
    private final static String SCRIPT_SUFFIX = ".js";

    private final ResourceKey resource;

    public RequireModuleScope( final Scriptable prototype, final ResourceKey resource )
    {
        this.resource = resource;
        setPrototype( prototype );
    }

    public ResourceKey getResource()
    {
        return this.resource;
    }

    public ResourceKey resolveScript( final String name )
    {
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
