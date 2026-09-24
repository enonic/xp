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
     * {@code url = baseUrl + path + queryString}.
     * <p>
     * Resolution is from configuration alone. The site or project the URL belongs to is the one
     * {@link PageUrlParams#base} selects, project and branch come from the params or the current
     * context, and the current request is never consulted. The base URL is the one configured
     * there, {@code null} when none is; the path is the URL-escaped content path relative to it.
     *
     * @throws IllegalArgumentException   if no base is given
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
