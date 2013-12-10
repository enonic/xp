package com.enonic.wem.portal.script.resolver;

import java.nio.file.Files;
import java.nio.file.Path;

import com.enonic.wem.api.module.ModuleName;

public final class ModuleScriptResolver
    implements ScriptResolver
{
    private final ModuleLocations locations;

    public ModuleScriptResolver( final ModuleLocations locations )
    {
        this.locations = locations;
    }

    @Override
    public Path resolve( final String name )
    {
        final int pos = name.indexOf( '/' );
        if ( pos < 1 )
        {
            return null;
        }

        final ModuleName module = ModuleName.from( name.substring( 0, pos ) );
        final Path parentPath = this.locations.get( module );

        if ( parentPath == null )
        {
            return null;
        }

        return resolve( parentPath, name.substring( pos + 1 ) );
    }

    @Override
    public Path resolve( final Path parent, final String name )
    {
        final Path path = parent.resolve( name );
        if ( Files.isRegularFile( path ) )
        {
            return path;
        }

        return null;
    }
}
