package com.enonic.xp.portal.url;

import com.google.common.annotations.Beta;

@Beta
public interface PortalUrlService
{
    String assetUrl( AssetUrlParams params );

    String serviceUrl( ServiceUrlParams params );

    String pageUrl( PageUrlParams params );

    String componentUrl( ComponentUrlParams params );

    String imageUrl( ImageUrlParams params );

    String attachmentUrl( AttachmentUrlParams params );

    String identityUrl( IdentityUrlParams params );

    String generateUrl( GenerateUrlParams params );

    String processHtml( ProcessHtmlParams params );
}
