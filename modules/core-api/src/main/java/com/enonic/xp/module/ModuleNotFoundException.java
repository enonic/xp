package com.enonic.xp.module;

import com.google.common.annotations.Beta;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.exception.NotFoundException;

@Beta
public final class ModuleNotFoundException
    extends NotFoundException
{
    public ModuleNotFoundException( final ApplicationKey applicationKey )
    {
        super( "Module [{0}] was not found", applicationKey );
    }
}
