package com.enonic.xp.portal.url;

import org.jspecify.annotations.NullMarked;

/**
 * Generates URLs from params that hold what the URL is built of, such as the media content
 * itself, and looks nothing up. The base URL is resolved as XP serves the URL: from the params,
 * the current request and virtual host configuration. {@link PortalUrlService} names what a URL
 * addresses by id or path, and resolves it.
 */
@NullMarked
public interface PortalUrlGeneratorService
{
    /**
     * Generates the URL of a processed image: an image or a vector image. An image the image API serves as stored has
     * a single URL, with the {@code full} scale and none of the processing params.
     *
     * @param params the image and how to process it
     * @return the URL; one the image API answers with 404 when the image does not resolve,
     *         and an error URL when no URL can be generated
     */
    String imageUrl( ImageUrlGeneratorParams params );

    /**
     * Generates the URL of an attachment.
     *
     * @param params the content and the attachment of it
     * @return the URL; one the attachment API answers with 404 when the attachment does not resolve,
     *         and an error URL when no URL can be generated
     */
    String attachmentUrl( AttachmentUrlGeneratorParams params );

    /**
     * Generates the URL of an API.
     *
     * @param params the API and the path within it
     * @return the URL, or an error URL when it cannot be generated
     */
    String apiUrl( ApiUrlGeneratorParams params );

    /**
     * Assembles a URL as {@code baseUrl + path + queryString}, each supplied by the params.
     *
     * @param params suppliers of the three segments
     * @return the URL, or an error URL when a supplier fails
     * @throws ContentOutOfScopeException if a supplier finds the content outside the site the URL belongs to
     */
    String generateUrl( UrlGeneratorParams params );
}
