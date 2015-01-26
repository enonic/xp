package com.enonic.xp.portal.thymeleaf.impl;

import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ServiceUrlParams;

public class MockPortalUrlService
    implements PortalUrlService
{
    @Override
    public String assetUrl( final AssetUrlParams params )
    {
        return params.toString();
    }

    @Override
    public String serviceUrl( final ServiceUrlParams params )
    {
        return params.toString();
    }
}
