package com.enonic.xp.portal.impl.handler.image;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ScaleParamsParser;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.impl.PortalHandler;
import com.enonic.xp.portal.impl.handler.EndpointHandler;
import com.enonic.xp.portal.impl.handler.PortalHandlerWorker;
import com.enonic.xp.web.HttpMethod;

@Component(immediate = true, service = PortalHandler.class)
public final class ImageHandler
    extends EndpointHandler
{
    private final static Pattern PATTERN = Pattern.compile( "([^/^:]+)(:[^/]+)?/([^/]+)/([^/]+)" );

    private ContentService contentService;

    private ImageService imageService;

    public ImageHandler()
    {
        super( "image" );
        setMethodsAllowed( HttpMethod.GET, HttpMethod.HEAD );
    }

    @Override
    protected PortalHandlerWorker newWorker( final PortalRequest req )
        throws Exception
    {
        final String restPath = findRestPath( req );
        final Matcher matcher = PATTERN.matcher( restPath );

        if ( !matcher.find() )
        {
            throw notFound( "Not a valid image url pattern" );
        }

        final ImageHandlerWorker worker = new ImageHandlerWorker();
        worker.contentId = ContentId.from( matcher.group( 1 ) );
        worker.cache = matcher.group( 2 ) != null;
        worker.scaleParams = new ScaleParamsParser().parse( matcher.group( 3 ) );
        worker.name = matcher.group( 4 );
        worker.imageService = this.imageService;
        worker.contentService = this.contentService;
        worker.filterParam = getParameter( req, "filter" );
        worker.qualityParam = getParameter( req, "quality" );
        worker.backgroundParam = getParameter( req, "background" );

        return worker;
    }

    private String getParameter( final PortalRequest req, final String name )
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
}
