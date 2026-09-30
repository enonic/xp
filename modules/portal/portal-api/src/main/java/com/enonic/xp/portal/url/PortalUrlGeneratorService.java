package com.enonic.xp.portal.url;

public interface PortalUrlGeneratorService
{
    String imageUrl( ImageUrlGeneratorParams params );

    String attachmentUrl( AttachmentUrlGeneratorParams params );

    /**
     * Resolves the parts of an image URL, for building the full URL from segments:
     * {@code url = <mediaBaseUrl> + path + queryString}, where {@code mediaBaseUrl} is supplied by the caller.
     * No base URL is resolved: {@code media.defaultBaseUrl}, vhost mappings, context attributes and the
     * current request are not consulted.
     */
    ImageUrlParts imageUrlParts( ImageUrlPartsParams params );

    /**
     * Resolves the parts of an attachment URL, for building the full URL from segments:
     * {@code url = <mediaBaseUrl> + path + queryString}, where {@code mediaBaseUrl} is supplied by the caller.
     * No base URL is resolved: {@code media.defaultBaseUrl}, vhost mappings, context attributes and the
     * current request are not consulted.
     */
    AttachmentUrlParts attachmentUrlParts( AttachmentUrlPartsParams params );

    String apiUrl( ApiUrlGeneratorParams params );

    String generateUrl( UrlGeneratorParams params );
}
