package com.enonic.wem.portal.url;

import com.enonic.wem.portal.internal.url.GeneralUrlBuilderImpl;
import com.enonic.wem.portal.internal.url.ImageByIdUrlBuilderImpl;
import com.enonic.wem.portal.internal.url.ImageUrlBuilderImpl;
import com.enonic.wem.portal.internal.url.ServiceUrlBuilderImpl;
import com.enonic.wem.portal.url.GeneralUrlBuilder;
import com.enonic.wem.portal.url.ImageByIdUrlBuilder;
import com.enonic.wem.portal.url.ImageUrlBuilder;
import com.enonic.wem.portal.url.ServiceUrlBuilder;

public final class PortalUrlBuilder
{

    public static GeneralUrlBuilder createUrl( final String baseUrl )
    {
        return new GeneralUrlBuilderImpl( baseUrl );
    }

    public static ImageUrlBuilder createImageUrl( final String baseUrl )
    {
        return new ImageUrlBuilderImpl( baseUrl );
    }

    public static ImageByIdUrlBuilder createImageByIdUrl( final String baseUrl )
    {
        return new ImageByIdUrlBuilderImpl( baseUrl );
    }

    public static ServiceUrlBuilder createServiceUrl( final String baseUrl )
    {
        return new ServiceUrlBuilderImpl( baseUrl );
    }

}
