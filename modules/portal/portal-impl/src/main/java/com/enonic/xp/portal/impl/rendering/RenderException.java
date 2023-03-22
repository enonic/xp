package com.enonic.xp.portal.impl.rendering;

import com.enonic.xp.exception.BaseException;

public final class RenderException
    extends BaseException
{
    public RenderException( final String message )
    {
        super( message );
    }

    public RenderException( final String message, final Throwable cause )
    {
        super( cause, message );
    }
}
