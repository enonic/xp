package com.enonic.wem.api.resource;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeyResolver;
import com.enonic.wem.api.module.ModuleName;

public final class ResourceKeyResolver
{
    private final ModuleKeyResolver moduleKeyResolver;

    public ResourceKeyResolver( final ModuleKeyResolver moduleKeyResolver )
    {
        this.moduleKeyResolver = moduleKeyResolver;
    }

    public ResourceKey resolve( final ResourceKey parent, final String name )
    {
        final int pos = name.indexOf( ':' );
        if ( pos >= 0 )
        {
            return resolveResource( name.substring( 0, pos ), name.substring( pos + 1 ) );
        }

        if ( name.startsWith( "./" ) || name.startsWith( "../" ) )
        {
            return parent.resolve( "../" + name );
        }

        return ResourceKey.from( parent.getModule(), name );
    }

    private ResourceKey resolveResource( final String module, final String path )
    {
        try
        {
            return resolveResource( ModuleKey.from( module ), path );
        }
        catch ( final Exception e )
        {
            return resolveResource( ModuleName.from( module ), path );
        }
    }

    private ResourceKey resolveResource( final ModuleKey moduleKey, final String path )
    {
        return ResourceKey.from( moduleKey, path );
    }

    private ResourceKey resolveResource( final ModuleName moduleName, final String path )
    {
        final ModuleKey moduleKey = this.moduleKeyResolver.resolve( moduleName );
        return resolveResource( moduleKey, path );
    }
}
