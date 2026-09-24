package com.enonic.xp.portal.url;

public interface PortalUrlGeneratorService
{
    String imageUrl( ImageUrlGeneratorParams params );

    String attachmentUrl( AttachmentUrlGeneratorParams params );

    /**
     * Resolves the parts of an image URL, for building the full URL from segments:
     * {@code url = <mediaBaseUrl> + path + queryString}.
     */
    ImageUrlParts imageUrlParts( ImageUrlPartsParams params );

    /**
     * Resolves the parts of an attachment URL, for building the full URL from segments:
     * {@code url = <mediaBaseUrl> + path + queryString}.
     */
    AttachmentUrlParts attachmentUrlParts( AttachmentUrlPartsParams params );

    String apiUrl( ApiUrlGeneratorParams params );

    String generateUrl( UrlGeneratorParams params );
}
