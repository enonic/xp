package com.enonic.xp.portal.impl;

import com.enonic.xp.context.ContextAccessor;

public final class VirtualHostContextHelper
{
    public static final String MEDIA_SERVICE_SCOPE = "mediaService.scope";

    private VirtualHostContextHelper()
    {
    }

    public static String getProperty( final String property )
    {
        return (String) ContextAccessor.current().getAttribute( property );
    }

    public static String getMediaServiceScope()
    {
        return getProperty( MEDIA_SERVICE_SCOPE );
    }
}
