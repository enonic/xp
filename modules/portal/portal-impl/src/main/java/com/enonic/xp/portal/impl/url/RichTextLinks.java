package com.enonic.xp.portal.impl.url;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.portal.html.HtmlDocument;
import com.enonic.xp.portal.html.HtmlElement;
import com.enonic.xp.site.SiteConfigs;
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.style.StyleDescriptors;

/**
 * The internal links of rich text, shared by {@link RichTextProcessor} and {@link RichTextPartsProcessor}: how they are
 * written, how their parameters are read, which image styles apply to them, and the clean-up of the default
 * processing.
 */
final class RichTextLinks
{
    static final String CONTENT_TYPE = "content";

    static final String MEDIA_TYPE = "media";

    static final String IMAGE_TYPE = "image";

    static final String DOWNLOAD_MODE = "download";

    static final String INLINE_MODE = "inline";

    private static final ApplicationKey SYSTEM_APPLICATION_KEY = ApplicationKey.from( "com.enonic.xp.app.system" );

    private static final int[] QUERY_OR_FRAGMENT_ALLOWED_CHARACTERS =
        "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ?/:@-._~!$&'()*+,;=%".chars().sorted().toArray();

    private static final Pattern PATTERN = Pattern.compile(
        "(" + CONTENT_TYPE + "|" + MEDIA_TYPE + "|" + IMAGE_TYPE + ")://(?:(" + DOWNLOAD_MODE + "|" + INLINE_MODE +
            ")/)?([0-9a-z-/]+)(\\?[^\"]+)?" );

    private RichTextLinks()
    {
    }

    /**
     * An internal link found in an element.
     *
     * @param attribute       the attribute holding it, {@code href} or {@code src}
     * @param uri             the attribute value
     * @param type            {@value #CONTENT_TYPE}, {@value #MEDIA_TYPE} or {@value #IMAGE_TYPE}
     * @param mode            {@value #DOWNLOAD_MODE} or {@value #INLINE_MODE} for a media link, {@code null} otherwise
     * @param id              id of the linked content
     * @param urlParamsString the parameters of the link, with the leading {@code ?}, or {@code null}
     */
    record Link(String attribute, String uri, String type, String mode, String id, String urlParamsString)
    {
        Map<String, String> urlParams()
        {
            return extractUrlParams( urlParamsString );
        }
    }

    /**
     * Removes the empty {@code figcaption} elements the editor leaves behind images without a caption.
     */
    static void removeEmptyCaptions( final HtmlDocument document )
    {
        document.select( "figcaption:empty" ).forEach( HtmlElement::remove );
    }

    /**
     * @return the internal link of the element, or {@code null} when it has none
     */
    static Link find( final HtmlElement element )
    {
        final String attribute = element.hasAttribute( "href" ) ? "href" : "src";
        final String uri = element.getAttribute( attribute );
        final Matcher matcher = PATTERN.matcher( uri );

        if ( !matcher.find() )
        {
            return null;
        }

        return new Link( attribute, uri, matcher.group( 1 ), matcher.group( 2 ), matcher.group( 3 ), matcher.group( 4 ) );
    }

    static Map<String, String> extractUrlParams( final String urlQuery )
    {
        if ( urlQuery == null )
        {
            return Collections.emptyMap();
        }
        final String query = urlQuery.startsWith( "?" ) ? urlQuery.substring( 1 ) : urlQuery;
        return Splitter.on( '&' ).trimResults().withKeyValueSeparator( "=" ).split( query.replace( "&amp;", "&" ) );
    }

    /**
     * @return the decoded query or fragment of a link when it consists of URL-safe characters, {@code null} otherwise
     */
    static String validQueryOrFragment( final String value )
    {
        if ( value == null )
        {
            return null;
        }
        final String decoded = URLDecoder.decode( value, StandardCharsets.UTF_8 );
        return decoded.chars().allMatch( ch -> Arrays.binarySearch( QUERY_OR_FRAGMENT_ALLOWED_CHARACTERS, ch ) >= 0 ) ? decoded : null;
    }

    /**
     * @return the style descriptors of the system application and of the applications the site configs name
     */
    static StyleDescriptors styleDescriptors( final StyleDescriptorService styleDescriptorService, final SiteConfigs siteConfigs )
    {
        final List<ApplicationKey> appKeys = new ArrayList<>();
        appKeys.add( SYSTEM_APPLICATION_KEY );
        siteConfigs.forEach( siteConfig -> appKeys.add( siteConfig.getApplicationKey() ) );
        return styleDescriptorService.getByApplications( ApplicationKeys.from( appKeys ) );
    }

    static ImageStyles imageStyles( final StyleDescriptors styleDescriptors )
    {
        return new ImageStyles( styleDescriptors );
    }
}
