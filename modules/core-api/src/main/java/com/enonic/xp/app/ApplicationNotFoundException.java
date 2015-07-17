package com.enonic.xp.app;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.NotFoundException;

@Beta
public final class ApplicationNotFoundException
    extends NotFoundException
{
    public ApplicationNotFoundException( final ApplicationKey applicationKey )
    {
        super( "Module [{0}] was not found", applicationKey );
    }
}
