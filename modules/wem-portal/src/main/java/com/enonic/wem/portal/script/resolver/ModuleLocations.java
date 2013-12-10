package com.enonic.wem.portal.script.resolver;

import java.nio.file.Path;
import java.util.HashMap;

import com.enonic.wem.api.module.ModuleName;

public final class ModuleLocations
    extends HashMap<ModuleName, Path>
{
    public ModuleLocations add( final ModuleName module, final Path path )
    {
        put( module, path );
        return this;
    }
}
