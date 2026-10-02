package com.enonic.xp.portal.url;

import org.jspecify.annotations.NullMarked;

/**
 * Generates portal URLs.
 * <p>
 * Unless a method says otherwise, URLs are generated as XP serves them: they follow the current
 * request and its virtual host mapping, and a URL that cannot be generated is returned as an error
 * URL. {@link #pageUrlParts(PageUrlPartsParams)} and {@link #processHtmlParts(ProcessHtmlPartsParams)} are resolved
 * from configuration alone, for callers that serve content somewhere else.
 */
@NullMarked
public interface PortalUrlService
{
    /**
     * Generates the URL of an asset of an application.
     *
     * @param params the application and the path of the asset
     * @return the URL, or an error URL when it cannot be generated
     */
    String assetUrl( AssetUrlParams params );

    /**
     * Generates the URL of a service of an application.
     *
     * @param params the application and the service
     * @return the URL, or an error URL when it cannot be generated
     */
    String serviceUrl( ServiceUrlParams params );

    /**
     * Resolves the base URL of the current site request: the address of the level of the content tree the matched
     * virtual host mapping points at, rewritten for that host, or the site engine address of the project when no
     * virtual host narrows the request. Page URLs following the request, and their paths, are relative to it, and so
     * are routes the site serves outside the content tree. Without a site request, it is the Base URL configured for
     * the project of the current context, or else its site engine address.
     * <p>
     * A content, project or branch in the params resolves the base URL from configuration in place of the request:
     * the Base URL configured for the nearest site of the content, or for the project. This use is deprecated:
     * {@link #urlBase(UrlBaseParams)} resolves it.
     * <p>
     * Failures are reported to the caller as exceptions.
     *
     * @param params the URL type
     * @return the base URL, without a trailing slash
     */
    String baseUrl( BaseUrlParams params );

    /**
     * Generates the URL of a page: the base URL of the nearest site containing the content, or of
     * its project, followed by the content path relative to it. The URL follows the current request
     * when served from a site. {@link #pageUrlParts(PageUrlPartsParams)} resolves the URL of a page
     * from configuration alone.
     *
     * @param params the content, and the project and branch it is in
     * @return the URL, or an error URL answered with 404 when the page does not resolve
     */
    String pageUrl( PageUrlParams params );

    /**
     * Resolves the site or project URLs belong to, from configuration alone, for
     * {@link #pageUrlParts(PageUrlPartsParams)} and {@link #processHtmlParts(ProcessHtmlPartsParams)}. Resolve it once
     * and pass it to every call of the same request.
     *
     * @param params the site or project, and the project and branch it is in
     * @return the resolved base
     * @throws com.enonic.xp.content.ContentNotFoundException if the content the params name does not exist
     */
    UrlBase urlBase( UrlBaseParams params );

    /**
     * Resolves the parts of a page URL, for building the full URL from segments:
     * {@code url = baseUrl + path + queryString}.
     * <p>
     * Resolution is from configuration alone. The URL belongs to the {@link PageUrlPartsParams#getBase() base},
     * the project of the current context unless given, and the content is looked up in its project
     * and branch. The base URL is the one configured there, {@code null} when none is; the path is
     * the URL-escaped content path relative to it.
     *
     * @param params the content and the site or project the URL belongs to
     * @return the parts of the URL
     * @throws ContentOutOfScopeException if the content is outside the site or project the URL
     *                                    belongs to
     */
    PageUrlParts pageUrlParts( PageUrlPartsParams params );

    /**
     * Generates the URL of a component of a page.
     *
     * @param params the component, and the content it is on - the current one unless named
     * @return the URL, or an error URL when it cannot be generated
     */
    String componentUrl( ComponentUrlParams params );

    /**
     * Generates the URL of a processed image: an image or a vector image.
     *
     * @param params the image and how to process it
     * @return the URL; one the image API answers with 404 when the image does not resolve,
     *         and an error URL when no URL can be generated
     */
    String imageUrl( ImageUrlParams params );

    /**
     * Generates the URL of an attachment.
     *
     * @param params the content and the attachment of it
     * @return the URL; one the attachment API answers with 404 when the attachment does not resolve,
     *         and an error URL when no URL can be generated
     */
    String attachmentUrl( AttachmentUrlParams params );

    /**
     * Generates the URL of an identity provider function, such as login or logout.
     *
     * @param params the identity provider and the function
     * @return the URL, or an error URL when it cannot be generated
     */
    String identityUrl( IdentityUrlParams params );

    /**
     * Generates a URL from a URL or path segments given by the caller.
     *
     * @param params the URL or path segments
     * @return the URL, or an error URL when it cannot be generated
     */
    String generateUrl( GenerateUrlParams params );

    /**
     * Replaces the internal links of an HTML fragment - to contents, images and attachments -
     * with URLs, and processes its macros.
     * {@link #processHtmlParts(ProcessHtmlPartsParams)} resolves the links from configuration alone.
     * <p>
     * An image names its style in its {@code style} parameter: {@code <application>:<name>} for the style of that
     * application, or a name alone for the first style of that name.
     *
     * @param params the HTML and how to process it
     * @return the processed HTML; empty when there is no HTML
     */
    String processHtml( ProcessHtmlParams params );

    /**
     * Resolves the parts of the internal links of an HTML fragment - to contents, images and attachments - from
     * configuration alone, replaces each link with a placeholder, and processes its macros.
     * <p>
     * Everything is resolved for the {@link ProcessHtmlPartsParams#getBase() base}, the project of the current context
     * unless given: the Base URL, the content path each content link is relative to, the project and branch contents are
     * looked up in, and the applications image styles come from. The base is resolved once for every link.
     * <p>
     * An image names its style in its {@code style} parameter: {@code <application>:<name>} for the style of that
     * application, or a name alone for the first style of that name.
     * <p>
     * The caller renders each element from the parts of its entry in {@link ProcessedHtml#links()} or
     * {@link ProcessedHtml#images()}, which the {@value ProcessedHtml#LINK_REF_ATTRIBUTE} or
     * {@value ProcessedHtml#IMAGE_REF_ATTRIBUTE} attribute of the element names.
     *
     * @param params the HTML, the site or project it belongs to, and how to process it
     * @return the processed HTML and the parts of each link and image in it
     */
    ProcessedHtml processHtmlParts( ProcessHtmlPartsParams params );

    /**
     * Generates the URL of an API.
     *
     * @param params the API and the path within it
     * @return the URL, or an error URL when it cannot be generated
     */
    String apiUrl( ApiUrlParams params );
}
