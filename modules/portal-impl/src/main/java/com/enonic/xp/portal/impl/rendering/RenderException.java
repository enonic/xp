package com.enonic.xp.portal.impl.rendering;

import com.enonic.xp.core.exception.BaseException;

public final class RenderException
    extends BaseException
{
    public RenderException( final String message )
    {
        super( message );
    }

    public RenderException( final String message, final Object... args )
    {
        super( message, args );
    }
}
