package com.enonic.xp.portal.impl.url;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.url.AbstractUrlParams;
import com.enonic.xp.portal.url.AssetUrlParams;
import com.enonic.xp.portal.url.AttachmentUrlParams;
import com.enonic.xp.portal.url.ComponentUrlParams;
import com.enonic.xp.portal.url.ImageUrlParams;
import com.enonic.xp.portal.url.PageUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ProcessHtmlParams;
import com.enonic.xp.portal.url.ServiceUrlParams;

@Component(immediate = true)
public final class PortalUrlServiceImpl
    implements PortalUrlService
{

    private static final int MATCH_INDEX = 1;

    private static final int LINK_INDEX = MATCH_INDEX + 1;

    private static final int TYPE_INDEX = LINK_INDEX + 1;

    private static final int MODE_INDEX = TYPE_INDEX + 1;

    private static final int ID_INDEX = MODE_INDEX + 1;

    private static final int NB_GROUPS = ID_INDEX;

    private static final String CONTENT_TYPE = "content";

    private static final String MEDIA_TYPE = "media";

    private static final String IMAGE_TYPE = "image";

    private static final String DOWNLOAD_MODE = "download";

    private static final String INLINE_MODE = "inline";

    private final static Pattern CONTENT_PATTERN =
        Pattern.compile( "(?:href|src)=(\"((" + CONTENT_TYPE + "|" + MEDIA_TYPE + "|" + IMAGE_TYPE +
                             ")://(?:(" + DOWNLOAD_MODE + "|" + INLINE_MODE + ")/)?([0-9a-z-/]+))\")",
                         Pattern.MULTILINE | Pattern.UNIX_LINES );

    private static final String IMAGE_FILTER = "scalewidth(768)";

    private static final String IMAGE_FORMAT = "jpeg";


    private ContentService contentService;

    @Override
    public String assetUrl( final AssetUrlParams params )
    {
        return build( new AssetUrlBuilder(), params );
    }

    @Override
    public String serviceUrl( final ServiceUrlParams params )
    {
        return build( new ServiceUrlBuilder(), params );
    }

    @Override
    public String pageUrl( final PageUrlParams params )
    {
        return build( new PageUrlBuilder(), params );
    }

    @Override
    public String componentUrl( final ComponentUrlParams params )
    {
        return build( new ComponentUrlBuilder(), params );
    }

    @Override
    public String imageUrl( final ImageUrlParams params )
    {
        return build( new ImageUrlBuilder(), params );
    }

    @Override
    public String attachmentUrl( final AttachmentUrlParams params )
    {
        return build( new AttachmentUrlBuilder(), params );
    }

    @Override
    public String processHtml( final ProcessHtmlParams params )
    {
        if ( params.getValue() == null || params.getValue().isEmpty() )
        {
            return "";
        }

        String processedHtml = params.getValue();

        final Matcher contentMatcher = CONTENT_PATTERN.matcher( params.getValue() );

        while ( contentMatcher.find() )
        {
            if ( contentMatcher.groupCount() == NB_GROUPS )
            {
                final String match = contentMatcher.group( MATCH_INDEX );
                final String link = contentMatcher.group( LINK_INDEX );
                final String type = contentMatcher.group( TYPE_INDEX );
                final String mode = contentMatcher.group( MODE_INDEX );
                final String id = contentMatcher.group( ID_INDEX );

                if ( CONTENT_TYPE.equals( type ) )
                {
                    PageUrlParams pageUrlParams = new PageUrlParams().
                        id( id ).
                        portalRequest( params.getPortalRequest() );

                    final String pageUrl = pageUrl( pageUrlParams );

                    processedHtml = processedHtml.replaceFirst( match, "\"" + pageUrl + "\"" );
                }
                else if ( IMAGE_TYPE.equals( type ) )
                {
                    ImageUrlParams imageUrlParams = new ImageUrlParams().
                        id( id ).
                        filter( IMAGE_FILTER ).
                        format( IMAGE_FORMAT ).
                        portalRequest( params.getPortalRequest() );

                    final String imageUrl = imageUrl( imageUrlParams );

                    processedHtml = processedHtml.replaceFirst( match, "\"" + imageUrl + "\"" );
                }
                else
                {
                    AttachmentUrlParams attachmentUrlParams = new AttachmentUrlParams().
                        id( id ).
                        download( DOWNLOAD_MODE.equals( mode ) ).
                        portalRequest( params.getPortalRequest() );

                    final String attachmentUrl = attachmentUrl( attachmentUrlParams );

                    processedHtml = processedHtml.replaceFirst( match, "\"" + attachmentUrl + "\"" );
                }
            }
        }
        return processedHtml;

    }

    private <B extends PortalUrlBuilder<P>, P extends AbstractUrlParams> String build( final B builder, final P params )
    {
        builder.setParams( params );
        builder.contentService = this.contentService;
        return builder.build();
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }
}
