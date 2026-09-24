package com.enonic.xp.portal.url;

public interface PortalUrlGeneratorService
{
    String imageUrl( ImageUrlGeneratorParams params );

    String attachmentUrl( AttachmentUrlGeneratorParams params );

    /**
     * Resolves the parts of an image URL, for building the full URL from segments:
     * {@code url = apiUrl + path + queryString}. Resolved from configuration alone: base URL
     * parameters are ignored and the current request is not consulted.
     */
    ImageUrlParts imageUrlParts( ImageUrlGeneratorParams params );

    /**
     * Resolves the parts of an attachment URL, for building the full URL from segments:
     * {@code url = apiUrl + path + queryString}. Resolved from configuration alone: base URL
     * parameters are ignored and the current request is not consulted.
     */
    AttachmentUrlParts attachmentUrlParts( AttachmentUrlGeneratorParams params );

    String apiUrl( ApiUrlGeneratorParams params );

    String generateUrl( UrlGeneratorParams params );
}
