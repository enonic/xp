package com.enonic.xp.portal.impl.handler.image;

import java.util.Collection;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ScaleParamsParser;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.portal.PortalWebRequest;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.handler.EndpointHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;
import com.enonic.xp.web.handler.WebRequest;
import com.enonic.xp.web.handler.WebResponse;

@Component(immediate = true, service = WebHandler.class)
public final class ImageHandler
    extends EndpointHandler
{
    private final static Pattern PATTERN = Pattern.compile( "([^/^:]+)(:[^/]+)?/([^/]+)/([^/]+)" );

    private ContentService contentService;

    private ImageService imageService;

    private MediaInfoService mediaInfoService;

    public ImageHandler()
    {
        super( EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS ), "image" );
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
    {
        final String endpointSubPath = getEndpointSubPath( webRequest );
        final Matcher matcher = PATTERN.matcher( endpointSubPath );
        if ( !matcher.find() )
        {
            throw notFound( "Not a valid image url pattern" );
        }

        final PortalWebRequest portalWebRequest =
            webRequest instanceof PortalWebRequest ? (PortalWebRequest) webRequest : PortalWebRequest.create( webRequest ).build();

        return ImageWebHandlerWorker.create().
            webRequest( portalWebRequest ).
            webResponse( webResponse ).
            contentId( ContentId.from( matcher.group( 1 ) ) ).
            cacheable( matcher.group( 2 ) != null ).
            scaleParams( new ScaleParamsParser().parse( matcher.group( 3 ) ) ).
            name( matcher.group( 4 ) ).
            imageService( imageService ).
            contentService( contentService ).
            mediaInfoService( mediaInfoService ).
            filterParam( getParameter( webRequest, "filter" ) ).
            qualityParam( getParameter( webRequest, "quality" ) ).
            backgroundParam( getParameter( webRequest, "background" ) ).
            build().
            execute();
    }

    private String getParameter( final WebRequest req, final String name )
    {
        final Collection<String> values = req.getParams().get( name );
        return values.isEmpty() ? null : values.iterator().next();
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setImageService( final ImageService imageService )
    {
        this.imageService = imageService;
    }

    @Reference
    public void setMediaInfoService( final MediaInfoService mediaInfoService )
    {
        this.mediaInfoService = mediaInfoService;
    }
}
