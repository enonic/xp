package com.enonic.xp.module;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.exception.NotFoundException;

@Beta
public final class ModuleNotStartedException
    extends NotFoundException
{
    public ModuleNotStartedException( final ApplicationKey applicationKey )
    {
        super( "Module [{0}] not started", applicationKey );
    }
}
