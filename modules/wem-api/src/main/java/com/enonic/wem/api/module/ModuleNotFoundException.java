package com.enonic.wem.api.module;

import com.enonic.wem.api.exception.NotFoundException;

public final class ModuleNotFoundException
    extends NotFoundException
{
    public ModuleNotFoundException( final ModuleKey moduleKey )
    {
        super( "Module [{0}] was not found", moduleKey );
    }

    public ModuleNotFoundException( final ModuleName moduleName )
    {
        super( "Module [{0}] could not be resolved", moduleName );
    }
}
