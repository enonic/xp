package com.enonic.xp.portal.url;

public interface PortalUrlService
{
    String assetUrl( AssetUrlParams params );

    String serviceUrl( ServiceUrlParams params );

    /**
     * Resolves the base URL a content is addressed under: the Base URL configured for the
     * nearest site at or above it, or for the project when it is in no site, and the site engine
     * address of that site or project when none is configured.
     * <p>
     * When {@code api} is set on the params, resolves the mount point of that API there instead:
     * {@code <baseUrl>/_} when a Base URL is configured and the API is mounted on the site, the
     * {@code media.defaultBaseUrl} configuration for media APIs when set, or {@code null} when
     * URLs should stay request-based.
     * <p>
     * Never returns an error URL: failures are reported to the caller.
     *
     * @throws com.enonic.xp.content.ContentNotFoundException if the content does not exist
     */
    String baseUrl( BaseUrlParams params );

    /**
     * @throws ContentOutOfScopeException if the site or project the URL is asked to belong to
     * does not contain the content
     */
    String pageUrl( PageUrlParams params );

    /**
     * Resolves the parts of a page URL, for building the full URL from segments:
     * {@code url = <baseUrl> + path + queryString}. The path is the URL-escaped content path
     * relative to the site or project the URL belongs to; base URL resolution from configuration
     * and from the current request is not involved.
     *
     * @throws ContentOutOfScopeException if the site or project the URL is asked to belong to
     * does not contain the content
     */
    PageUrlParts pageUrlParts( PageUrlParams params );

    String componentUrl( ComponentUrlParams params );

    String imageUrl( ImageUrlParams params );

    String attachmentUrl( AttachmentUrlParams params );

    String identityUrl( IdentityUrlParams params );

    String generateUrl( GenerateUrlParams params );

    String processHtml( ProcessHtmlParams params );

    String apiUrl( ApiUrlParams params );
}
