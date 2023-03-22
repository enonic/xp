package com.enonic.xp.app;

import java.text.MessageFormat;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.exception.NotFoundException;

@PublicApi
public final class ApplicationNotFoundException
    extends NotFoundException
{
    public ApplicationNotFoundException( final ApplicationKey applicationKey )
    {
        super( MessageFormat.format( "Application [{0}] was not found", applicationKey ) );
    }
}
