package com.enonic.xp.module;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.NotFoundException;

@Beta
public final class ModuleNotStartedException
    extends NotFoundException
{
    public ModuleNotStartedException( final ModuleKey moduleKey )
    {
        super( "Module [{0}] not started", moduleKey );
    }
}
