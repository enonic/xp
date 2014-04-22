package com.enonic.wem.api.xml;

import com.enonic.wem.api.exception.BaseException;

public final class XmlException
    extends BaseException
{
    public XmlException( final Throwable cause )
    {
        super( cause.getMessage(), cause );
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
