package com.enonic.wem.api.module;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeyResolver;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.module.ModuleResourceKey;

public final class ModuleResourceKeyResolver
{
    private final ModuleKeyResolver moduleKeyResolver;

    public ModuleResourceKeyResolver( final ModuleKeyResolver moduleKeyResolver )
    {
        this.moduleKeyResolver = moduleKeyResolver;
    }

    public ModuleResourceKey resolve( final ModuleResourceKey parent, final String name )
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

        return ModuleResourceKey.from( parent.getModule(), name );
    }

    private ModuleResourceKey resolveResource( final String module, final String path )
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

    private ModuleResourceKey resolveResource( final ModuleKey moduleKey, final String path )
    {
        return ModuleResourceKey.from( moduleKey, path );
    }

    private ModuleResourceKey resolveResource( final ModuleName moduleName, final String path )
    {
        final ModuleKey moduleKey = this.moduleKeyResolver.resolve( moduleName );
        return resolveResource( moduleKey, path );
    }
}
