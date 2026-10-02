package com.enonic.xp.portal.url;

import org.jspecify.annotations.NullMarked;

/**
 * Generates URLs from params that hold what the URL is built of, such as the media content
 * itself. The URL parts params name the media by id or path instead when it is not at hand, and
 * it is looked up in their project and branch.
 * <p>
 * The {@code ...Url} methods resolve a base URL as XP serves the URL: from the params, the current
 * request and virtual host configuration. The {@code ...UrlParts} methods build the parts that
 * follow the base from the params alone, and the caller supplies the base.
 */
@NullMarked
public interface PortalUrlGeneratorService
{
    /**
     * Generates the URL of a processed image: an image or a vector image. An image the image API serves as stored has
     * a single URL, with the {@code full} scale and none of the processing params.
     *
     * @param params the image and how to process it
     * @return the URL, or an error URL when it cannot be generated
     */
    String imageUrl( ImageUrlGeneratorParams params );

    /**
     * Generates the URL of an attachment.
     *
     * @param params the content and the attachment of it
     * @return the URL, or an error URL when it cannot be generated
     */
    String attachmentUrl( AttachmentUrlGeneratorParams params );

    /**
     * Resolves the parts of an image URL, for building the full URL from segments:
     * {@code url = <mediaBaseUrl> + path + queryString}, where {@code mediaBaseUrl} is supplied by the caller.
     * The parts are built from the params alone. An image the image API serves as stored has a single URL, with the
     * {@code full} scale and none of the processing params.
     *
     * @param params the image and how to process it
     * @return the parts of the URL
     * @throws IllegalArgumentException                       unless the media is an image or a vector image
     * @throws com.enonic.xp.content.ContentNotFoundException if the media the params name by id or path is missing
     */
    ImageUrlParts imageUrlParts( ImageUrlPartsParams params );

    /**
     * Resolves the parts of an attachment URL, for building the full URL from segments:
     * {@code url = <mediaBaseUrl> + path + queryString}, where {@code mediaBaseUrl} is supplied by the caller.
     * The parts are built from the params alone.
     *
     * @param params the content and the attachment of it
     * @return the parts of the URL
     * @throws com.enonic.xp.content.ContentNotFoundException if the content the params name by id or path is missing
     */
    AttachmentUrlParts attachmentUrlParts( AttachmentUrlPartsParams params );

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
