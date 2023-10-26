package com.enonic.xp.portal.impl.url;

import com.enonic.xp.context.ContextAccessor;

public class UrlContextHelper
{
    private static final String MEDIA_SERVICE_BASE_URL = "mediaService.baseUrl";

    private static final String ID_PROVIDER_SERVICE_BASE_URL = "idProviderService.baseUrl";

    public static String getProperty( final String property )
    {
        return (String) ContextAccessor.current().getAttribute( property );
    }

    public static String getMediaServiceBaseUrl()
    {
        return getProperty( MEDIA_SERVICE_BASE_URL );
    }

    public static String getIdProviderServiceBaseUrl()
    {
        return getProperty( ID_PROVIDER_SERVICE_BASE_URL );
    }
}
