package com.enonic.xp.portal.url;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class PageUrlGeneratorParams
{
    private final BaseUrlStrategy baseUrlStrategy;

    public PageUrlGeneratorParams( final BaseUrlStrategy baseUrlStrategy )
    {
        this.baseUrlStrategy = baseUrlStrategy;
    }

    public BaseUrlStrategy getBaseUrlStrategy()
    {
        return baseUrlStrategy;
    }
}
