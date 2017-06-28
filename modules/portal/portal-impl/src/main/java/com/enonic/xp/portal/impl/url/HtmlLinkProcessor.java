package com.enonic.xp.portal.impl.url;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.primitives.Ints;

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

import static org.apache.commons.lang.StringUtils.substringAfter;
import static org.apache.commons.lang.StringUtils.substringBefore;

public class HtmlLinkProcessor
{
    private static final int MATCH_INDEX = 1;

    private static final int LINK_INDEX = MATCH_INDEX + 1;

    private static final int TYPE_INDEX = LINK_INDEX + 1;

    private static final int MODE_INDEX = TYPE_INDEX + 1;

    private static final int ID_INDEX = MODE_INDEX + 1;

    private static final int PARAMS_INDEX = ID_INDEX + 1;

    private static final int NB_GROUPS = ID_INDEX;

    private static final String CONTENT_TYPE = "content";

    private static final String MEDIA_TYPE = "media";

    private static final String IMAGE_TYPE = "image";

    private static final String DOWNLOAD_MODE = "download";

    private static final String INLINE_MODE = "inline";


    private static final String DEFAULT_IMAGE_SCALE_WIDTH = "width(768)";

    private static final String IMAGE_SCALE_WIDTH = "width(%d)";

    private static final int DEFAULT_WIDTH = 768;

    private static final String IMAGE_NO_SCALING = "full";

    private static final String KEEP_SIZE = "keepSize";

    private static final String SCALE = "scale";

    private static final String SIZE = "size";


    private ContentService contentService;

    private PortalUrlService portalUrlService;

    public HtmlLinkProcessor( final ContentService contentService, final PortalUrlService portalUrlService )
    {
        this.contentService = contentService;
        this.portalUrlService = portalUrlService;
    }

    private static final Pattern CONTENT_PATTERN =
        Pattern.compile( "(?:href|src)=(\"((" + CONTENT_TYPE + "|" + MEDIA_TYPE + "|" + IMAGE_TYPE +
                             ")://(?:(" + DOWNLOAD_MODE + "|" + INLINE_MODE + ")/)?([0-9a-z-/]+)(\\?[^\"]+)?)\")",
                         Pattern.MULTILINE | Pattern.UNIX_LINES );

    public String process( final String text, final String urlType, final PortalRequest portalRequest )
    {
        String processedHtml = text;
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

                if ( CONTENT_TYPE.equals( type ) )
                {
                    PageUrlParams pageUrlParams = new PageUrlParams().
                        type( urlType ).
                        id( id ).
                        portalRequest( portalRequest );

                    final String pageUrl = portalUrlService.pageUrl( pageUrlParams );

                    processedHtml = processedHtml.replaceFirst( Pattern.quote( match ), "\"" + pageUrl + "\"" );
                }
                else if ( IMAGE_TYPE.equals( type ) )
                {
                    ImageUrlParams imageUrlParams = new ImageUrlParams().
                        type( urlType ).
                        id( id ).
                        scale( getScale( id, urlParamsString ) ).
                        portalRequest( portalRequest );

                    final String imageUrl = portalUrlService.imageUrl( imageUrlParams );

                    processedHtml = processedHtml.replaceFirst( Pattern.quote( match ), "\"" + imageUrl + "\"" );
                }
                else
                {
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


    private String getScale( final String id, final String urlParamsString )
    {
        final Map<String, String> urlParams = extractUrlParams( urlParamsString );
        if ( urlParams.isEmpty() )
        {
            return IMAGE_NO_SCALING;
        }

        final boolean keepSize = urlParams.containsKey( KEEP_SIZE );
        final String sizeParam = urlParams.get( SIZE );
        final Integer size = sizeParam != null ? Ints.tryParse( sizeParam ) : null;

        if ( urlParams.containsKey( SCALE ) )
        {
            final String scaleParam = urlParams.get( SCALE );
            if ( !scaleParam.contains( ":" ) )
            {
                throw new IllegalArgumentException( "Invalid scale parameter: " + scaleParam );
            }
            final String horizontalProportion = substringBefore( scaleParam, ":" );
            final String verticalProportion = substringAfter( scaleParam, ":" );

            final int width;
            if ( keepSize )
            {
                width = getImageOriginalWidth( id );
            }
            else if ( size != null )
            {
                width = size;
            }
            else
            {
                width = DEFAULT_WIDTH;
            }
            final int height = width / Integer.parseInt( horizontalProportion ) * Integer.parseInt( verticalProportion );

            return "block(" + width + "," + height + ")";
        }
        else
        {
            if ( keepSize )
            {
                return IMAGE_NO_SCALING;
            }
            else if ( size != null )
            {
                return String.format( IMAGE_SCALE_WIDTH, size );
            }
            else
            {
                return DEFAULT_IMAGE_SCALE_WIDTH;
            }
        }
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
        return Splitter.on( '&' ).trimResults().withKeyValueSeparator( "=" ).split( query.replace( "&amp;", "&" ) );
    }

}
