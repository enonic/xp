package com.enonic.xp.portal.url;

public interface PortalUrlService
{
    String assetUrl( AssetUrlParams params );

    String serviceUrl( ServiceUrlParams params );

    /**
     * Resolves the base URL of a content anchor.
     * <p>
     * When {@code api} is set on the params, resolves the mount point of that API for the anchor
     * instead: {@code <baseUrl>/_} when a Base URL is configured and the API is mounted on the
     * anchored site, the {@code media.defaultBaseUrl} configuration for media APIs when set,
     * or {@code null} when URLs should stay request-based.
     * <p>
     * Never returns an error URL: failures are reported to the caller.
     *
     * @throws com.enonic.xp.content.ContentNotFoundException if the anchor does not exist
     */
    String baseUrl( BaseUrlParams params );

    String pageUrl( PageUrlParams params );

    /**
     * Resolves the parts of a page URL, for building the full URL from segments:
     * {@code url = <baseUrl> + path + queryString}. The path is the URL-escaped content path
     * relative to the nearest site (the full content path when there is no site); base URL
     * resolution from configuration and from the current request is not involved.
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
