package com.enonic.wem.portal.script.require;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleName;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.core.module.ModuleKeyResolver;

final class ModuleResourceKeyResolver
{
    private final ModuleKeyResolver resolver;

    private final ModuleKey current;

    public ModuleResourceKeyResolver( final ModuleKeyResolver resolver, final ModuleKey current )
    {
        this.resolver = resolver;
        this.current = current;
    }

    public ModuleResourceKey resolve( final String name )
    {
        if ( name.endsWith( ".js" ) )
        {
            return doResolve( name );
        }
        else
        {
            return doResolve( name + ".js" );
        }
    }

    private ModuleResourceKey doResolve( final String name )
    {
        if ( !name.contains( ":" ) )
        {
            return null;
        }

        if ( name.startsWith( ":" ) )
        {
            return resolveFromDefault( name.substring( 1 ) );
        }

        try
        {
            return ModuleResourceKey.from( name );
        }
        catch ( final Exception e )
        {
            return resolveWithoutVersion( name );
        }
    }

    private ModuleResourceKey resolveFromDefault( final String path )
    {
        if ( this.current == null )
        {
            return null;
        }

        return new ModuleResourceKey( this.current, ResourcePath.from( path ) );
    }

    private ModuleResourceKey resolveWithoutVersion( final String name )
    {
        final int pos = name.indexOf( ':' );
        final String modulePart = name.substring( 0, pos );
        final String pathPart = name.substring( pos + 1 );

        return resolveWithoutVersion( ModuleName.from( modulePart ), ResourcePath.from( pathPart ) );
    }

    private ModuleResourceKey resolveWithoutVersion( final ModuleName module, final ResourcePath path )
    {
        final ModuleKey key = this.resolver.resolve( module );
        if ( key == null )
        {
            return null;
        }

        return new ModuleResourceKey( key, path );
    }
}
