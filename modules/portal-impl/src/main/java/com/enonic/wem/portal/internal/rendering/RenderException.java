package com.enonic.wem.portal.internal.rendering;

import com.enonic.wem.api.exception.BaseException;

public final class RenderException
    extends BaseException
{
    public RenderException( final String message )
    {
        super( message );
    }

    public RenderException( final Throwable t, final String message )
    {
        super( message, t );
    }

    public RenderException( final String message, final Object... args )
    {
        super( message, args );
    }

    public RenderException( final Throwable cause, final String message, final Object... args )
    {
        super( cause, message, args );
    }
}
