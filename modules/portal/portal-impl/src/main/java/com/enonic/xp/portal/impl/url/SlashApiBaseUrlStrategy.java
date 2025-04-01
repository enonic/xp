package com.enonic.xp.portal.impl.url;

import com.enonic.xp.portal.url.BaseUrlStrategy;

final class SlashApiBaseUrlStrategy
    implements BaseUrlStrategy
{
    @Override
    public String generateBaseUrl()
    {
        return "/api";
    }
}
