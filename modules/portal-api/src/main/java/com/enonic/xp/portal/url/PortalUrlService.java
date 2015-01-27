package com.enonic.xp.portal.url;

public interface PortalUrlService
{
    public String assetUrl( AssetUrlParams params );

    public String serviceUrl( ServiceUrlParams params );

    public String pageUrl( PageUrlParams params );

    public String componentUrl( ComponentUrlParams params );

    public String imageUrl( ImageUrlParams params );
}
