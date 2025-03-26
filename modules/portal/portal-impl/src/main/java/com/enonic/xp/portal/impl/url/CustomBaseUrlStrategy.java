package com.enonic.xp.portal.impl.url;

import java.util.Objects;

import com.enonic.xp.portal.url.BaseUrlStrategy;

final class CustomBaseUrlStrategy
    implements BaseUrlStrategy
{
    private final String baseUrl;

    CustomBaseUrlStrategy( final String baseUrl )
    {
        this.baseUrl = Objects.requireNonNull( baseUrl );
    }

    @Override
    public String generateBaseUrl()
    {
        return baseUrl + "/_/";
    }
}
