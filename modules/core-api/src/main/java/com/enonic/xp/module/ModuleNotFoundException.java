package com.enonic.xp.module;

import com.enonic.xp.exception.NotFoundException;

public final class ModuleNotFoundException
    extends NotFoundException
{
    public ModuleNotFoundException( final ModuleKey moduleKey )
    {
        super( "Module [{0}] was not found", moduleKey );
    }
}
