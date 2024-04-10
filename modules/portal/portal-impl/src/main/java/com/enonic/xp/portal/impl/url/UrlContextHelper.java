package com.enonic.xp.portal.impl.url;

import com.enonic.xp.context.ContextAccessor;

public class UrlContextHelper
{
    private static final String MEDIA_SERVICE_BASE_URL = "mediaService.baseUrl";

    public static String getProperty( final String property )
    {
        return (String) ContextAccessor.current().getAttribute( property );
    }

    public static String getMediaServiceBaseUrl()
    {
        return getProperty( MEDIA_SERVICE_BASE_URL );
    }
}
