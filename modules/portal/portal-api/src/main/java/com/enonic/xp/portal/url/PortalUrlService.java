package com.enonic.xp.portal.url;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface PortalUrlService
{
    String assetUrl( AssetUrlParams params );

    String serviceUrl( ServiceUrlParams params );

    String baseUrl( BaseUrlParams params );

    String pageUrl( PageUrlParams params );

    String componentUrl( ComponentUrlParams params );

    String imageUrl( ImageUrlParams params );

    String attachmentUrl( AttachmentUrlParams params );

    String identityUrl( IdentityUrlParams params );

    String generateUrl( GenerateUrlParams params );

    String processHtml( ProcessHtmlParams params );

    String apiUrl( ApiUrlParams params );

    String imageUrl( ImageUrlGeneratorParams params );

    String attachmentUrl( AttachmentUrlGeneratorParams params );

    String apiUrl( ApiUrlGeneratorParams params );

    String generateUrl( UrlGeneratorParams params );
}
