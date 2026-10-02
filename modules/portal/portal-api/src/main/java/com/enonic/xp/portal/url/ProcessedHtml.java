package com.enonic.xp.portal.url;

import java.util.List;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.style.ImageStyle;

/**
 * Result of {@link PortalUrlService#processHtmlParts(ProcessHtmlPartsParams)}: the processed HTML, and the parts of
 * every link and image in it.
 * <p>
 * Each internal link and image of the HTML holds a placeholder, a readable value useful for debugging: a content link
 * shows its path relative to the site or project the HTML belongs to, an image or attachment its media API path, such
 * as {@code /media:image/...}. The element carries a {@value #LINK_REF_ATTRIBUTE} or {@value #IMAGE_REF_ATTRIBUTE}
 * attribute holding the {@code ref} of its entry in {@link #links()} or {@link #images()}; the caller renders the
 * element from the parts of that entry.
 *
 * @param html    the processed HTML, with placeholders for its internal links and images
 * @param baseUrl the Base URL configured for the site or project the HTML belongs to, without a trailing slash;
 *                {@code null} when none is configured. It is the {@link PageUrlParts#baseUrl() baseUrl} of the page
 *                parts of every content link
 * @param links   internal links to contents and attachments, in document order
 * @param images  internal images, in document order
 */
@NullMarked
public record ProcessedHtml(String html, @Nullable String baseUrl, List<Link> links, List<Image> images)
{
    /**
     * Name of the attribute holding the {@link Link#ref() ref} of a link.
     */
    public static final String LINK_REF_ATTRIBUTE = "data-link-ref";

    /**
     * Name of the attribute holding the {@link Image#ref() ref} of an image.
     */
    public static final String IMAGE_REF_ATTRIBUTE = "data-image-ref";

    public ProcessedHtml
    {
        links = List.copyOf( links );
        images = List.copyOf( images );
    }

    /**
     * An internal link: a {@link ContentLink} to a content page, or an {@link AttachmentLink} to an attachment of a
     * media content.
     */
    public sealed interface Link
        permits ContentLink, AttachmentLink
    {
        /**
         * @return value of the {@value #LINK_REF_ATTRIBUTE} attribute of the element
         */
        String ref();

        /**
         * @return the link as written in the HTML, such as {@code content://<id>}
         */
        String uri();

        /**
         * @return id of the linked content
         */
        String contentId();
    }

    /**
     * A link to a content page, written as {@code content://<id>}.
     *
     * @param ref       value of the {@value #LINK_REF_ATTRIBUTE} attribute of the element
     * @param uri       the link as written in the HTML
     * @param contentId id of the linked content
     * @param page      parts of the page URL; its query string is the one the link carries
     * @param fragment  fragment of the link, without {@code #}; {@code null} when it has none
     */
    public record ContentLink(String ref, String uri, String contentId, PageUrlParts page, @Nullable String fragment)
        implements Link
    {
    }

    /**
     * A link to the attachment of a media content, written as {@code media://<mode>/<id>}.
     *
     * @param ref        value of the {@value #LINK_REF_ATTRIBUTE} attribute of the element
     * @param uri        the link as written in the HTML
     * @param contentId  id of the media content
     * @param attachment parts of the attachment URL
     * @param download   whether the link asks for the attachment to be downloaded
     */
    public record AttachmentLink(String ref, String uri, String contentId, AttachmentUrlParts attachment, boolean download)
        implements Link
    {
    }

    /**
     * An internal image.
     *
     * @param ref       value of the {@value #IMAGE_REF_ATTRIBUTE} attribute of the element
     * @param contentId id of the image content
     * @param style     the image style applied, or {@code null} for none
     * @param src       parts of the URL of the image as it appears in {@code src}
     * @param srcset    parts of the URLs in {@code srcset}, one for each of the image widths; empty for an image the
     *                  image API serves as stored
     */
    public record Image(String ref, String contentId, @Nullable ImageStyle style, ImageUrlParts src, List<Source> srcset)
    {
        public Image
        {
            srcset = List.copyOf( srcset );
        }
    }

    /**
     * A {@code srcset} candidate of an image.
     *
     * @param width width in pixels, as the {@code w} descriptor of the candidate
     * @param url   parts of the URL of the image scaled to the width
     */
    public record Source(int width, ImageUrlParts url)
    {
    }
}
