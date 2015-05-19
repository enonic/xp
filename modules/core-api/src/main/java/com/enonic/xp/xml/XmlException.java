package com.enonic.xp.xml;

import com.google.common.annotations.Beta;

import com.enonic.xp.exception.BaseException;

@Beta
public final class XmlException
    extends BaseException
{
    public XmlException( final Throwable cause )
    {
        super( cause, cause.getMessage() );
    }

    public XmlException( final String message )
    {
        super( message );
    }

    public XmlException( final Throwable t, final String message )
    {
        super( t, message );
    }

    public XmlException( final String message, final Object... args )
    {
        super( message, args );
    }

    public XmlException( final Throwable cause, final String message, final Object... args )
    {
        super( cause, message, args );
    }
}
