package com.enonic.xp.portal.url;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public interface PortalUrlGeneratorService
{
    String imageUrl( ImageUrlGeneratorParams params );

    String attachmentUrl( AttachmentUrlGeneratorParams params );

    String apiUrl( ApiUrlGeneratorParams params );

    String generateUrl( UrlGeneratorParams params );
}
