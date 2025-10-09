package com.enonic.xp.core.impl.export.xml;

import com.enonic.xp.exception.BaseException;

public final class XmlException
    extends BaseException
{
    public XmlException( final Throwable cause )
    {
        super( cause, cause != null ? cause.getMessage() : null );
    }

    public XmlException( final String message )
    {
        super( message );
    }

    public XmlException( final Throwable t, final String message )
    {
        super( t, message );
    }
}
