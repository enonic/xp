package com.enonic.xp.portal.impl.url;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.macro.MacroService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.html.HtmlDocument;
import com.enonic.xp.portal.html.HtmlElement;
import com.enonic.xp.portal.impl.html.HtmlParser;
import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.portal.url.HtmlElementPostProcessor;
import com.enonic.xp.portal.url.HtmlProcessorParams;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlParams;
import com.enonic.xp.style.ElementStyle;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.style.StyleDescriptors;

public class RichTextProcessor
{
    private static final ApplicationKey SYSTEM_APPLICATION_KEY = ApplicationKey.from( "com.enonic.xp.app.system" );

    private static final int[] QUERY_OR_FRAGMENT_ALLOWED_CHARACTERS =
        "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ?/:@-._~!$&'()*+,;=%".chars().sorted().toArray();

    /*
     * Pattern Constants
     */

    private static final int TYPE_INDEX = 1;

    private static final int MODE_INDEX = TYPE_INDEX + 1;

    private static final int ID_INDEX = MODE_INDEX + 1;

    private static final int PARAMS_INDEX = ID_INDEX + 1;

    private static final int NB_GROUPS = ID_INDEX;

    private static final String CONTENT_TYPE = "content";

    private static final String MEDIA_TYPE = "media";

    private static final String IMAGE_TYPE = "image";

    private static final String DOWNLOAD_MODE = "download";

    private static final String INLINE_MODE = "inline";

    /*
     * Parameters Keys
     */

    private static final String SCALE_PARAM = "scale";

    private static final String STYLE_PARAM = "style";

    /*
     * Default Values
     */

    private static final String IMAGE_SCALE = "width(768)";

    private static final int DEFAULT_WIDTH = 768;

    private static final Pattern PATTERN = Pattern.compile(
        "(" + CONTENT_TYPE + "|" + MEDIA_TYPE + "|" + IMAGE_TYPE + ")://(?:(" + DOWNLOAD_MODE + "|" + INLINE_MODE +
            ")/)?([0-9a-z-/]+)(\\?[^\"]+)?" );

    private static final Pattern ASPECT_RATIO_PATTEN = Pattern.compile( "^(?<horizontalProportion>\\d+):(?<verticalProportion>\\d+)$" );

    private final StyleDescriptorService styleDescriptorService;

    private final PortalUrlService portalUrlService;

    private final MacroService macroService;

    private Map<String, ImageStyle> imageStyleMap;

    public RichTextProcessor( final StyleDescriptorService styleDescriptorService, final PortalUrlService portalUrlService,
                              final MacroService macroService )
    {
        this.styleDescriptorService = styleDescriptorService;
        this.portalUrlService = portalUrlService;
        this.macroService = macroService;
    }

    private void defaultElementProcessing( HtmlElement element, ProcessHtmlParams params, HtmlElementPostProcessor postProcessor )
    {
        final Matcher contentMatcher = PATTERN.matcher( getLinkValue( element ) );

        if ( contentMatcher.find() && contentMatcher.groupCount() >= NB_GROUPS )
        {
            final String type = contentMatcher.group( TYPE_INDEX );
            final String mode = contentMatcher.group( MODE_INDEX );
            final String id = contentMatcher.group( ID_INDEX );
            final String urlParamsString = contentMatcher.groupCount() == PARAMS_INDEX ? contentMatcher.group( PARAMS_INDEX ) : null;

            switch ( type )
            {
                case CONTENT_TYPE:
                {
                    defaultLinkProcessingForContent( element, params, id, mode, urlParamsString, postProcessor );
                    break;
                }
                case IMAGE_TYPE:
                {
                    defaultImageProcessing( element, params, id, mode, urlParamsString, postProcessor );
                    break;
                }
                case MEDIA_TYPE:
                {
                    defaultAttachmentProcessing( element, params, id, mode, urlParamsString, postProcessor );
                    break;
                }
                default:
                {
                    throw new IllegalStateException( "Unknown type " + type );
                }
            }
        }
    }

    private void defaultProcessing( HtmlDocument document, ProcessHtmlParams params, HtmlElementPostProcessor postProcessor )
    {
        document.select( "[href],[src]" ).forEach( element -> defaultElementProcessing( element, params, postProcessor ) );
    }

    public String process( final ProcessHtmlParams params )
    {
        if ( params.getValue() == null || params.getValue().isEmpty() )
        {
            return "";
        }

        HtmlDocument document = HtmlParser.parse( params.getValue() );
        if ( params.getCustomHtmlProcessor() == null )
        {
            defaultProcessing( document, params, null );
        }
        else
        {
            final String html = params.getCustomHtmlProcessor()
                .apply( HtmlProcessorParams.create()
                            .htmlDocument( document )
                            .defaultProcessor( postProcessor -> defaultProcessing( document, params, postProcessor ) )
                            .defaultElementProcessor(
                                ( htmlElement, postProcessor ) -> defaultElementProcessing( htmlElement, params, postProcessor ) )
                            .build() );
            if ( !params.isProcessMacros() )
            {
                return html;
            }
        }
        return new HtmlMacroProcessor( macroService ).process( document.getInnerHtml() );
    }

    private void defaultLinkProcessingForContent( HtmlElement element, ProcessHtmlParams params, String id, String mode,
                                                  String urlParamsString, HtmlElementPostProcessor postProcessor )
    {
        final String originalUri = element.getAttribute( getLinkAttribute( element ) );

        final PageUrlParams pageUrlParams =
            new PageUrlParams().type( params.getType() ).id( id ).portalRequest( params.getPortalRequest() );

        final String pageUrl = addQueryParamsIfPresent( portalUrlService.pageUrl( pageUrlParams ), urlParamsString );

        element.setAttribute( getLinkAttribute( element ), pageUrl );

        if ( postProcessor != null )
        {
            Map<String, String> properties = new HashMap<>();

            properties.put( "type", params.getType() );
            properties.put( "contentId", id );
            properties.put( "uri", originalUri );
            properties.put( "mode", mode );
            properties.put( "queryParams", urlParamsString );

            postProcessor.process( element, properties );
        }
    }

    private void defaultImageProcessing( HtmlElement element, ProcessHtmlParams params, String id, String mode, String urlParamsString,
                                         HtmlElementPostProcessor callback )
    {
        final Map<String, String> urlParams = extractUrlParams( urlParamsString );

        if ( imageStyleMap == null )
        {
            StyleDescriptors styleDescriptors = params.getCustomStyleDescriptorsCallback() != null
                ? params.getCustomStyleDescriptorsCallback().get()
                : getStyleDescriptors( params.getPortalRequest() );
            imageStyleMap = getImageStyleMap( styleDescriptors );
        }

        ImageStyle imageStyle = getImageStyle( imageStyleMap, urlParams );
        ImageUrlParams imageUrlParams = new ImageUrlParams().type( params.getType() )
            .id( id )
            .scale( getScale( imageStyle, urlParams, null ) )
            .filter( getFilter( imageStyle ) )
            .portalRequest( params.getPortalRequest() );

        final String imageUrl = portalUrlService.imageUrl( imageUrlParams );

        element.setAttribute( getLinkAttribute( element ), imageUrl );

        if ( "img".equals( element.getTagName() ) )
        {
            if ( params.getImageWidths() != null )
            {
                final String srcsetValues = params.getImageWidths().stream().map( imageWidth -> {
                    final ImageUrlParams imageParams = new ImageUrlParams().type( params.getType() )
                        .id( id )
                        .scale( getScale( imageStyle, urlParams, imageWidth ) )
                        .filter( getFilter( imageStyle ) )
                        .portalRequest( params.getPortalRequest() );

                    return portalUrlService.imageUrl( imageParams ) + " " + imageWidth + "w";
                } ).collect( Collectors.joining( "," ) );

                element.setAttribute( "srcset", srcsetValues );
            }

            if ( params.getImageSizes() != null && !params.getImageSizes().trim().isEmpty() )
            {
                element.setAttribute( "sizes", params.getImageSizes() );
            }
        }

        if ( callback != null )
        {
            Map<String, String> properties = new HashMap<>();

            properties.put( "type", params.getType() );
            properties.put( "contentId", id );
            properties.put( "mode", mode );
            properties.put( "queryParams", urlParamsString );
            if ( imageStyle != null )
            {
                properties.put( "style:name", imageStyle.getName() );
                properties.put( "style:aspectRatio", imageStyle.getAspectRatio() );
                properties.put( "style:filter", imageStyle.getFilter() );
            }

            callback.process( element, properties );
        }
    }

    private void defaultAttachmentProcessing( HtmlElement element, ProcessHtmlParams params, String id, String mode, String urlParamsString,
                                              HtmlElementPostProcessor callback )
    {
        final String originalUri = element.getAttribute( getLinkAttribute( element ) );

        final AttachmentUrlParams attachmentUrlParams = new AttachmentUrlParams().type( params.getType() )
            .id( id )
            .download( DOWNLOAD_MODE.equals( mode ) )
            .portalRequest( params.getPortalRequest() );

        final String attachmentUrl = portalUrlService.attachmentUrl( attachmentUrlParams );

        element.setAttribute( getLinkAttribute( element ), attachmentUrl );

        if ( callback != null )
        {
            Map<String, String> properties = new HashMap<>();

            properties.put( "type", params.getType() );
            properties.put( "contentId", id );
            properties.put( "uri", originalUri );
            properties.put( "mode", mode );
            properties.put( "queryParams", urlParamsString );

            callback.process( element, properties );
        }
    }

    private String getLinkValue( final HtmlElement element )
    {
        return element.hasAttribute( "href" ) ? element.getAttribute( "href" ) : element.getAttribute( "src" );
    }

    private String getLinkAttribute( final HtmlElement element )
    {
        return element.hasAttribute( "href" ) ? "href" : "src";
    }

    private Map<String, ImageStyle> getImageStyleMap( final StyleDescriptors styleDescriptors )
    {
        return styleDescriptors.stream()
            .flatMap( styleDescriptor -> styleDescriptor.getElements().stream() )
            .filter( elementStyle -> ImageStyle.STYLE_ELEMENT_NAME.equals( elementStyle.getElement() ) )
            .collect( Collectors.toUnmodifiableMap( ElementStyle::getName, elementStyle -> (ImageStyle) elementStyle ) );
    }

    private StyleDescriptors getStyleDescriptors( final PortalRequest portalRequest )
    {
        final List<ApplicationKey> appKeys = new ArrayList<>();
        appKeys.add( SYSTEM_APPLICATION_KEY );
        if ( portalRequest != null && portalRequest.getSite() != null )
        {
            portalRequest.getSite().getSiteConfigs().forEach( siteConfig -> appKeys.add( siteConfig.getApplicationKey() ) );
        }
        return styleDescriptorService.getByApplications( ApplicationKeys.from( appKeys ) );
    }

    private ImageStyle getImageStyle( final Map<String, ImageStyle> imageStyleMap, final Map<String, String> urlParams )
    {
        final String styleString = urlParams.get( STYLE_PARAM );
        return styleString != null ? imageStyleMap.get( styleString ) : null;
    }

    private String getScale( final ImageStyle imageStyle, final Map<String, String> urlParams, final Integer expectedWidth )
    {
        final String aspectRatio = getAspectRation( imageStyle, urlParams );

        if ( aspectRatio != null )
        {
            final Matcher matcher = ASPECT_RATIO_PATTEN.matcher( aspectRatio );
            if ( !matcher.matches() )
            {
                throw new IllegalArgumentException( "Invalid aspect ratio: " + aspectRatio );
            }
            final String horizontalProportion = matcher.group( "horizontalProportion" );
            final String verticalProportion = matcher.group( "verticalProportion" );

            final int width = Objects.requireNonNullElse( expectedWidth, DEFAULT_WIDTH );
            final int height = width / Integer.parseInt( horizontalProportion ) * Integer.parseInt( verticalProportion );

            return "block(" + width + "," + height + ")";
        }

        return expectedWidth != null ? "width(" + expectedWidth + ")" : IMAGE_SCALE;
    }

    private String getFilter( final ImageStyle imageStyle )
    {
        return imageStyle == null ? null : imageStyle.getFilter();
    }

    private String getAspectRation( final ImageStyle imageStyle, final Map<String, String> urlParams )
    {
        if ( imageStyle != null )
        {
            final String aspectRatio = imageStyle.getAspectRatio();
            if ( aspectRatio != null )
            {
                return aspectRatio;
            }
        }
        return urlParams.get( SCALE_PARAM );
    }

    private Map<String, String> extractUrlParams( final String urlQuery )
    {
        if ( urlQuery == null )
        {
            return Collections.emptyMap();
        }
        final String query = urlQuery.startsWith( "?" ) ? urlQuery.substring( 1 ) : urlQuery;
        return Splitter.on( '&' ).trimResults().withKeyValueSeparator( "=" ).split( query.replace( "&amp;", "&" ) );
    }

    private String addQueryParamsIfPresent( final String url, final String urlQuery )
    {
        if ( urlQuery == null )
        {
            return url;
        }
        final String query = urlQuery.startsWith( "?" ) ? urlQuery.substring( 1 ) : urlQuery;

        final StringBuilder urlSuffix = new StringBuilder();
        final Map<String, String> queryParamsAsMap = extractUrlParams( query );

        addComponentToUrlIfValid( queryParamsAsMap.get( "query" ), "?", urlSuffix );
        addComponentToUrlIfValid( queryParamsAsMap.get( "fragment" ), "#", urlSuffix );

        return url + urlSuffix;
    }

    private void addComponentToUrlIfValid( final String value, final String mark, final StringBuilder builder )
    {
        if ( value == null )
        {
            return;
        }
        final String decodedValue = URLDecoder.decode( value, StandardCharsets.UTF_8 );
        if ( decodedValue.chars().allMatch( ch -> Arrays.binarySearch( QUERY_OR_FRAGMENT_ALLOWED_CHARACTERS, ch ) >= 0 ) )
        {
            builder.append( mark ).append( decodedValue );
        }
    }
}
