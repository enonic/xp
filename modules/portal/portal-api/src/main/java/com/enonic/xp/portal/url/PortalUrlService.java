package com.enonic.xp.portal.url;

import org.jspecify.annotations.NullMarked;

/**
 * Generates portal URLs.
 * <p>
 * Unless a method says otherwise, URLs are generated as XP serves them: they follow the current
 * request and its virtual host mapping, and a URL that cannot be generated is returned as an error
 * URL. The {@code ...Parts} methods - {@link #pageUrlParts(PageUrlPartsParams)},
 * {@link #imageUrlParts(ImageUrlPartsParams)}, {@link #attachmentUrlParts(AttachmentUrlPartsParams)} and
 * {@link #processHtmlParts(ProcessHtmlPartsParams)} - are resolved from configuration alone, for callers that serve
 * content somewhere else, for the {@link #portalScope(PortalScopeParams) scope} they are given.
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
     * {@link #portalScope(PortalScopeParams)} resolves it.
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
     * Resolves a {@link PortalScope}: an immutable, request-independent context for resolving page URLs and processing
     * rich text below a selected content or project, from configuration alone. It provides
     * {@link #pageUrlParts(PageUrlPartsParams)} and {@link #processHtmlParts(ProcessHtmlPartsParams)} what a site
     * request would, without one. Resolve it once and pass it to every call below the selected content.
     *
     * @param params the content or project of the scope, and the project and branch it is in
     * @return the resolved scope
     * @throws com.enonic.xp.content.ContentNotFoundException if the content the params name does not exist
     */
    PortalScope portalScope( PortalScopeParams params );

    /**
     * Resolves the parts of a page URL, for building the full URL from segments:
     * {@code url = baseUrl + path + queryString}.
     * <p>
     * Resolution is from configuration alone. The URL belongs to the {@link PageUrlPartsParams#getScope() scope},
     * the project of the current context unless given, and the content is looked up in its project
     * and branch. The base URL is that of the scope, {@code null} when none is configured; the path is
     * the URL-escaped content path relative to the content of the scope.
     *
     * @param params the content and the scope the URL belongs to
     * @return the parts of the URL
     * @throws ContentOutOfScopeException if the content is outside the scope the URL belongs to
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
     * Resolves the parts of an image URL, for building the full URL from segments:
     * {@code url = <mediaBaseUrl> + path + queryString}, where {@code mediaBaseUrl} is supplied by the caller.
     * Resolution is from configuration alone. An image the image API serves as stored has a single URL, with the
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
     * Resolution is from configuration alone.
     *
     * @param params the content and the attachment of it
     * @return the parts of the URL
     * @throws com.enonic.xp.content.ContentNotFoundException if the content the params name by id or path is missing
     */
    AttachmentUrlParts attachmentUrlParts( AttachmentUrlPartsParams params );

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
     * configuration alone, and replaces each link and macro with a placeholder.
     * <p>
     * Everything is resolved for the {@link ProcessHtmlPartsParams#getScope() scope}, the project of the current context
     * unless given: the Base URL, the content path each content link is relative to, the project and branch contents are
     * looked up in, and the applications image styles and macros come from. The scope is resolved once for every link.
     * <p>
     * An image names its style in its {@code style} parameter: {@code <application>:<name>} for the style of that
     * application, or a name alone for the first style of that name.
     * <p>
     * The caller renders each element from the parts of its entry in {@link ProcessedHtml#links()} or
     * {@link ProcessedHtml#images()}, which the {@value ProcessedHtml#LINK_REF_ATTRIBUTE} or
     * {@value ProcessedHtml#IMAGE_REF_ATTRIBUTE} attribute of the element names, and each macro from its entry in
     * {@link ProcessedHtml#macros()}, which the {@value ProcessedHtml#MACRO_REF_ATTRIBUTE} attribute names.
     *
     * @param params the HTML, the scope it belongs to, and how to process it
     * @return the processed HTML and the parts of each link, image and macro in it
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
