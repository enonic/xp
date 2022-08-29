package com.enonic.xp.portal.impl.url;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.html.HtmlDocument;
import com.enonic.xp.portal.html.HtmlElement;
import com.enonic.xp.portal.impl.html.HtmlParser;
import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.portal.url.HtmlImageProcessorParams;
import com.enonic.xp.portal.url.HtmlLinkProcessorParams;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlParams;
import com.enonic.xp.site.Site;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.style.StyleDescriptors;

public class HtmlProcessor
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

    public HtmlProcessor( final StyleDescriptorService styleDescriptorService, final PortalUrlService portalUrlService )
    {
        this.styleDescriptorService = styleDescriptorService;
        this.portalUrlService = portalUrlService;
    }

    private void defaultAttachmentProcessing( HtmlElement element, String id, String mode, String type, PortalRequest portalRequest )
    {
        final AttachmentUrlParams attachmentUrlParams = new AttachmentUrlParams().
            type( type ).
            id( id ).
            download( DOWNLOAD_MODE.equals( mode ) ).
            portalRequest( portalRequest );

        final String attachmentUrl = portalUrlService.attachmentUrl( attachmentUrlParams );

        element.setAttribute( getLinkAttribute( element ), attachmentUrl );
    }

    public String process( final ProcessHtmlParams params )
    {
        final HtmlDocument document = HtmlParser.parse( params.getValue() );

        final List<HtmlElement> elements = document.select( "[href],[src]" );

        final ImmutableMap<String, ImageStyle> imageStyleMap = getImageStyleMap( params.getPortalRequest() );

        elements.forEach( element -> {
            final Matcher contentMatcher = PATTERN.matcher( getLinkValue( element ) );

            if ( contentMatcher.find() && contentMatcher.groupCount() >= NB_GROUPS )
            {
                final String type = contentMatcher.group( TYPE_INDEX );
                final String mode = contentMatcher.group( MODE_INDEX );
                final String id = contentMatcher.group( ID_INDEX );
                final String urlParamsString = contentMatcher.groupCount() == PARAMS_INDEX ? contentMatcher.group( PARAMS_INDEX ) : null;
                final Map<String, String> urlParams = extractUrlParams( urlParamsString );

                switch ( type )
                {
                    case CONTENT_TYPE:
                    {
                        if ( params.getLinkProcessor() == null && params.getImageProcessor() == null )
                        {
                            defaultLinkProcessingForContent( element, params, id, urlParamsString );
                        }
                        else
                        {
                            if ( "a".equals( element.getTagName() ) )
                            {
                                if ( params.getLinkProcessor() == null )
                                {
                                    defaultLinkProcessingForContent( element, params, id, urlParamsString );
                                }
                                else
                                {
                                    params.getLinkProcessor()
                                        .process( HtmlLinkProcessorParams.create()
                                                      .setElement( element )
                                                      .setContentId( id )
                                                      .setMode( mode )
                                                      .setQueryParams( urlParams )
                                                      .setPortalRequest( params.getPortalRequest() )
                                                      .setType( type )
                                                      .setDefaultProcessor(
                                                          () -> defaultLinkProcessingForContent( element, params, id, urlParamsString ) )
                                                      .build() );
                                }
                            }
                        }
                        break;
                    }
                    case IMAGE_TYPE:
                    {
                        if ( params.getLinkProcessor() == null && params.getImageProcessor() == null )
                        {
                            defaultImageProcessing( element, params, id, urlParamsString, imageStyleMap );
                        }
                        else
                        {
                            if ( "img".equals( element.getTagName() ) )
                            {
                                if ( params.getImageProcessor() == null )
                                {
                                    defaultImageProcessing( element, params, id, urlParamsString, imageStyleMap );
                                }
                                else
                                {
                                    params.getImageProcessor()
                                        .process( HtmlImageProcessorParams.create()
                                                      .setElement( element )
                                                      .setContentId( id )
                                                      .setType( type )
                                                      .setMode( mode )
                                                      .setQueryParams( urlParams )
                                                      .setImageWidths( params.getImageWidths() )
                                                      .setImageSizes( params.getImageSizes() )
                                                      .setPortalRequest( params.getPortalRequest() )
                                                      .setImageStyle( getImageStyle( imageStyleMap, urlParams ) )
                                                      .setDefaultProcessor(
                                                          () -> defaultImageProcessing( element, params, id, urlParamsString,
                                                                                        imageStyleMap ) )
                                                      .build() );
                                }
                            }
                        }
                        break;
                    }
                    default:
                    {
                        if ( params.getLinkProcessor() == null && params.getImageProcessor() == null )
                        {
                            defaultAttachmentProcessing( element, id, mode, type, params.getPortalRequest() );
                        }
                        else
                        {
                            if ( "a".equals( element.getTagName() ) )
                            {
                                if ( params.getLinkProcessor() == null )
                                {
                                    defaultAttachmentProcessing( element, id, mode, type, params.getPortalRequest() );
                                }
                                else
                                {
                                    params.getLinkProcessor()
                                        .process( HtmlLinkProcessorParams.create()
                                                      .setElement( element )
                                                      .setContentId( id )
                                                      .setMode( mode )
                                                      .setType( type )
                                                      .setPortalRequest( params.getPortalRequest() )
                                                      .setQueryParams( urlParams )
                                                      .setDefaultProcessor( () -> defaultAttachmentProcessing( element, id, mode, type,
                                                                                                               params.getPortalRequest() ) )
                                                      .build() );
                                }
                            }
                            if ( "img".equals( element.getTagName() ) )
                            {
                                if ( params.getImageProcessor() == null )
                                {
                                    defaultAttachmentProcessing( element, id, mode, type, params.getPortalRequest() );
                                }
                                else
                                {
                                    params.getImageProcessor()
                                        .process( HtmlImageProcessorParams.create()
                                                      .setElement( element )
                                                      .setContentId( id )
                                                      .setType( type )
                                                      .setMode( mode )
                                                      .setQueryParams( urlParams )
                                                      .setImageWidths( params.getImageWidths() )
                                                      .setImageSizes( params.getImageSizes() )
                                                      .setPortalRequest( params.getPortalRequest() )
                                                      .setImageStyle( imageStyleMap.get( "editor-style-original" ) )
                                                      .setDefaultProcessor( () -> defaultAttachmentProcessing( element, id, mode, type,
                                                                                                               params.getPortalRequest() ) )
                                                      .build() );
                                }
                            }
                        }
                        break;
                    }
                }
            }
        } );

        if ( params.getHtmlPostProcessor() != null )
        {
            params.getHtmlPostProcessor().process( document );
        }

        return document.getInnerHtml();
    }

    private void defaultLinkProcessingForContent( HtmlElement element, ProcessHtmlParams params, String id, String urlParamsString )
    {
        final PageUrlParams pageUrlParams = new PageUrlParams().
            type( params.getType() ).
            id( id ).
            portalRequest( params.getPortalRequest() );

        final String pageUrl = addQueryParamsIfPresent( portalUrlService.pageUrl( pageUrlParams ), urlParamsString );

        element.setAttribute( getLinkAttribute( element ), pageUrl );
    }

    private void defaultImageProcessing( HtmlElement element, ProcessHtmlParams params, String id, String urlParamsString,
                                         ImmutableMap<String, ImageStyle> imageStyleMap )
    {
        final Map<String, String> urlParams = extractUrlParams( urlParamsString );

        ImageStyle imageStyle = getImageStyle( imageStyleMap, urlParams );

        ImageUrlParams imageUrlParams = new ImageUrlParams().
            type( params.getType() ).
            id( id ).
            scale( getScale( imageStyle, urlParams, null ) ).
            filter( getFilter( imageStyle ) ).
            portalRequest( params.getPortalRequest() );

        final String imageUrl = portalUrlService.imageUrl( imageUrlParams );

        element.setAttribute( getLinkAttribute( element ), imageUrl );

        if ( "img".equals( element.getTagName() ) )
        {
            if ( params.getImageWidths() != null )
            {
                final String srcsetValues = params.getImageWidths().stream().map( imageWidth -> {
                    final ImageUrlParams imageParams = new ImageUrlParams().
                        type( params.getType() ).
                        id( id ).
                        scale( getScale( imageStyle, urlParams, imageWidth ) ).
                        filter( getFilter( imageStyle ) ).
                        portalRequest( params.getPortalRequest() );

                    return portalUrlService.imageUrl( imageParams ) + " " + imageWidth + "w";
                } ).collect( Collectors.joining( "," ) );

                element.setAttribute( "srcset", srcsetValues );
            }

            if ( params.getImageSizes() != null && !params.getImageSizes().trim().isEmpty() )
            {
                element.setAttribute( "sizes", params.getImageSizes() );
            }
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

    private ImmutableMap<String, ImageStyle> getImageStyleMap( final PortalRequest portalRequest )
    {
        final ImmutableMap.Builder<String, ImageStyle> imageStyleMap = ImmutableMap.builder();
        final StyleDescriptors styleDescriptors = getStyleDescriptors( portalRequest );
        styleDescriptors.stream().
            flatMap( styleDescriptor -> styleDescriptor.getElements().stream() ).
            filter( elementStyle -> ImageStyle.STYLE_ELEMENT_NAME.equals( elementStyle.getElement() ) ).
            forEach( elementStyle -> imageStyleMap.put( elementStyle.getName(), (ImageStyle) elementStyle ) );
        return imageStyleMap.build();
    }

    private StyleDescriptors getStyleDescriptors( final PortalRequest portalRequest )
    {
        final ImmutableList.Builder<ApplicationKey> applicationKeyList = new ImmutableList.Builder<ApplicationKey>().
            add( SYSTEM_APPLICATION_KEY );
        if ( portalRequest != null )
        {
            final Site site = portalRequest.getSite();
            if ( site != null )
            {
                final ImmutableSet<ApplicationKey> siteApplicationKeySet = site.getSiteConfigs().getApplicationKeys();
                applicationKeyList.addAll( siteApplicationKeySet );
            }
        }

        final ApplicationKeys applicationKeys = ApplicationKeys.from( applicationKeyList.build() );
        return styleDescriptorService.getByApplications( applicationKeys );
    }

    private ImageStyle getImageStyle( final Map<String, ImageStyle> imageStyleMap, final Map<String, String> urlParams )
    {
        final String styleString = urlParams.get( STYLE_PARAM );
        if ( styleString != null )
        {
            return imageStyleMap.get( styleString );
        }
        return null;
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
        return Splitter.on( '&' ).
            trimResults().
            withKeyValueSeparator( "=" ).
            split( query.replace( "&amp;", "&" ) );
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

        return url + urlSuffix.toString();
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
