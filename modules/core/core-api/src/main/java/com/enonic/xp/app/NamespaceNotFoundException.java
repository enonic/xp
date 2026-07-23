package com.enonic.xp.app;

import java.text.MessageFormat;

import com.enonic.xp.exception.BaseException;


public final class NamespaceNotFoundException
    extends BaseException
{
    public NamespaceNotFoundException( final ApplicationKey applicationKey )
    {
        super( MessageFormat.format( "Namespace [{0}] was not found", applicationKey ) );
    }
}