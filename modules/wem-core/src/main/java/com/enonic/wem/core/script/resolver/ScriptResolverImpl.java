package com.enonic.wem.core.script.resolver;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeyResolver;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.module.ModuleResourceKey;

public final class ScriptResolverImpl
    implements ScriptResolver
{
    private final ModuleResourceKey resourceKey;

    private final ModuleKeyResolver moduleKeyResolver;

    public ScriptResolverImpl( final ModuleResourceKey resourceKey, final ModuleKeyResolver moduleKeyResolver )
    {
        this.resourceKey = resourceKey;
        this.moduleKeyResolver = moduleKeyResolver;
    }

    @Override
    public ModuleResourceKey getResource()
    {
        return this.resourceKey;
    }

    @Override
    public ModuleResourceKey resolveScript( final String name )
    {
        if ( name.endsWith( ".js" ) )
        {
            return resolveResource( name );
        }

        return resolveResource( name + ".js" );
    }

    @Override
    public ModuleResourceKey resolveResource( final String name )
    {
        final int pos = name.indexOf( ':' );
        if ( pos >= 0 )
        {
            return resolveResource( name.substring( pos ), name.substring( pos + 1 ) );
        }

        if ( name.startsWith( "/" ) )
        {
            return ModuleResourceKey.from( getResource().getModule(), name );
        }

        return this.resourceKey.resolve( "../" + name );
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
