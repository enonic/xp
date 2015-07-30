package com.enonic.xp.app;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.NotFoundException;

@Beta
public final class ApplicationNotStartedException
    extends NotFoundException
{
    public ApplicationNotStartedException( final ApplicationKey applicationKey )
    {
        super( "Application [{0}] not started", applicationKey );
    }
}
