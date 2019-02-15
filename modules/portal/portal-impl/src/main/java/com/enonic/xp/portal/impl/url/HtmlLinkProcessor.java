package com.enonic.xp.portal.impl.url;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.ExtraData;
import com.enonic.xp.content.Media;
import com.enonic.xp.data.Property;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.site.Site;
import com.enonic.xp.style.ImageStyle;
import com.enonic.xp.style.StyleDescriptorService;
import com.enonic.xp.style.StyleDescriptors;

import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang.StringUtils.substringBefore;

public class HtmlLinkProcessor
{
    private static final ApplicationKey SYSTEM_APPLICATION_KEY = ApplicationKey.from( "com.enonic.xp.app.system" );

    /*
     * Pattern Constants
     */

    private static final int MATCH_INDEX = 1;

    private static final int LINK_INDEX = MATCH_INDEX + 1;

    private static final int TYPE_INDEX = LINK_INDEX + 1;

    private static final int MODE_INDEX = TYPE_INDEX + 1;

    public static final int ID_INDEX = MODE_INDEX + 1;

    private static final int PARAMS_INDEX = ID_INDEX + 1;

    public static final int NB_GROUPS = ID_INDEX;

    private static final String CONTENT_TYPE = "content";

    private static final String MEDIA_TYPE = "media";

    private static final String IMAGE_TYPE = "image";

    private static final String DOWNLOAD_MODE = "download";

    private static final String INLINE_MODE = "inline";

    /*
     * Parameters Keys
     */

    private static final String KEEP_SIZE_PARAM = "keepSize";

    private static final String SCALE_PARAM = "scale";

    private static final String STYLE_PARAM = "style";

    /*
     * Default Values
     */

    private static final String IMAGE_SCALE = "width(768)";

    private static final int DEFAULT_WIDTH = 768;

    private static final String IMAGE_NO_SCALING = "full";


    private ContentService contentService;

    private StyleDescriptorService styleDescriptorService;

    private PortalUrlService portalUrlService;

    public HtmlLinkProcessor( final ContentService contentService, final StyleDescriptorService styleDescriptorService,
                              final PortalUrlService portalUrlService )
    {
        this.contentService = contentService;
        this.styleDescriptorService = styleDescriptorService;
        this.portalUrlService = portalUrlService;
    }

    public static final Pattern CONTENT_PATTERN = Pattern.compile(
        "(?:href|src)=(\"((" + CONTENT_TYPE + "|" + MEDIA_TYPE + "|" + IMAGE_TYPE + ")://(?:(" + DOWNLOAD_MODE + "|" + INLINE_MODE +
            ")/)?([0-9a-z-/]+)(\\?[^\"]+)?)\")", Pattern.MULTILINE | Pattern.UNIX_LINES );

    public String process( final String text, final String urlType, final PortalRequest portalRequest )
    {
        String processedHtml = text;
        final ImmutableMap<String, ImageStyle> imageStyleMap = getImageStyleMap( portalRequest );
        final Matcher contentMatcher = CONTENT_PATTERN.matcher( text );

        while ( contentMatcher.find() )
        {
            if ( contentMatcher.groupCount() >= NB_GROUPS )
            {
                final String match = contentMatcher.group( MATCH_INDEX );
                final String link = contentMatcher.group( LINK_INDEX );
                final String type = contentMatcher.group( TYPE_INDEX );
                final String mode = contentMatcher.group( MODE_INDEX );
                final String id = contentMatcher.group( ID_INDEX );
                final String urlParamsString = contentMatcher.groupCount() == PARAMS_INDEX ? contentMatcher.group( PARAMS_INDEX ) : null;

                switch ( type )
                {
                    case CONTENT_TYPE:
                        PageUrlParams pageUrlParams = new PageUrlParams().
                            type( urlType ).
                            id( id ).
                            portalRequest( portalRequest );
                        final String pageUrl = portalUrlService.pageUrl( pageUrlParams );
                        processedHtml = processedHtml.replaceFirst( Pattern.quote( match ), "\"" + pageUrl + "\"" );
                        break;
                    case IMAGE_TYPE:
                        final Map<String, String> urlParams = extractUrlParams( urlParamsString );

                        ImageStyle imageStyle = getImageStyle( imageStyleMap, urlParams );

                        ImageUrlParams imageUrlParams = new ImageUrlParams().
                            type( urlType ).
                            id( id ).
                            scale( getScale( id, imageStyle, urlParams ) ).
                            filter( getFilter( imageStyle ) ).
                            portalRequest( portalRequest );

                        final String imageUrl = portalUrlService.imageUrl( imageUrlParams );

                        processedHtml = processedHtml.replaceFirst( Pattern.quote( match ), "\"" + imageUrl + "\"" );
                        break;
                    default:
                        AttachmentUrlParams attachmentUrlParams = new AttachmentUrlParams().
                            type( urlType ).
                            id( id ).
                            download( DOWNLOAD_MODE.equals( mode ) ).
                            portalRequest( portalRequest );

                        final String attachmentUrl = portalUrlService.attachmentUrl( attachmentUrlParams );

                        processedHtml = processedHtml.replaceFirst( Pattern.quote( match ), "\"" + attachmentUrl + "\"" );
                }
            }
        }
        return processedHtml;
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

    private String getScale( final String id, final ImageStyle imageStyle, final Map<String, String> urlParams )
    {
        final String aspectRatio = getAspectRation( imageStyle, urlParams );
        final boolean keepSize = urlParams.containsKey( KEEP_SIZE_PARAM );

        if ( aspectRatio != null )
        {
            if ( !aspectRatio.contains( ":" ) )
            {
                throw new IllegalArgumentException( "Invalid aspect ratio: " + aspectRatio );
            }
            final String horizontalProportion = substringBefore( aspectRatio, ":" );
            final String verticalProportion = substringAfter( aspectRatio, ":" );

            final int width = keepSize ? getImageOriginalWidth( id ) : DEFAULT_WIDTH;
            final int height = width / Integer.parseInt( horizontalProportion ) * Integer.parseInt( verticalProportion );

            return "block(" + width + "," + height + ")";
        }

        if ( keepSize)
        {
            return IMAGE_NO_SCALING;
        }
        return IMAGE_SCALE;
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


    private int getImageOriginalWidth( final String id )
    {
        final Content content = this.getContent( ContentId.from( id ) );

        if ( content instanceof Media )
        {
            ExtraData imageData = content.getAllExtraData().getMetadata( MediaInfo.IMAGE_INFO_METADATA_NAME );

            if ( imageData != null )
            {
                final Property widthProperty = imageData.getData().getProperty( MediaInfo.IMAGE_INFO_IMAGE_WIDTH );
                if ( widthProperty != null )
                {
                    return widthProperty.getValue().asLong().intValue();
                }
            }
        }

        return 0;
    }

    private Content getContent( final ContentId contentId )
    {
        try
        {
            return this.contentService.getById( contentId );
        }
        catch ( ContentNotFoundException e )
        {
            return null;
        }
    }

    private Map<String, String> extractUrlParams( final String urlQuery )
    {
        final String query = substringAfter( urlQuery, "?" );
        if ( query == null )
        {
            return Collections.emptyMap();
        }
        return Splitter.on( '&' ).
            trimResults().
            withKeyValueSeparator( "=" ).
            split( query.replace( "&amp;", "&" ) );
    }

}
