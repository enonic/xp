package com.enonic.xp.app;

import java.text.MessageFormat;

import com.enonic.xp.exception.BaseException;


public final class ApplicationNotFoundException
    extends BaseException
{
    public ApplicationNotFoundException( final ApplicationKey applicationKey )
    {
        super( MessageFormat.format( "Application [{0}] was not found", applicationKey ) );
    }
}
