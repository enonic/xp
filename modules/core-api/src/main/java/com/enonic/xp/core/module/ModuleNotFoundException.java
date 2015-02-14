package com.enonic.xp.core.module;

import com.enonic.xp.core.exception.NotFoundException;

public final class ModuleNotFoundException
    extends NotFoundException
{
    public ModuleNotFoundException( final ModuleKey moduleKey )
    {
        super( "Module [{0}] was not found", moduleKey );
    }
}
