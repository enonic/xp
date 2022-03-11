package com.enonic.xp.portal.impl.handler.image;

import java.util.Collection;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ScaleParamsParser;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.handler.EndpointHandler;
import com.enonic.xp.portal.handler.WebHandlerHelper;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class, configurationPid = "com.enonic.xp.portal")
public final class ImageHandler
    extends EndpointHandler
{
    private static final Pattern PATTERN = Pattern.compile( "([^/^:]+)(?::([^/]+))?/([^/]+)/([^/]+)" );

    private ContentService contentService;

    private ImageService imageService;

    private MediaInfoService mediaInfoService;

    private volatile String privateCacheControlHeaderConfig;

    private volatile String publicCacheControlHeaderConfig;

    private volatile String contentSecurityPolicy;

    private volatile String contentSecurityPolicySvg;

    public ImageHandler()
    {
        super( EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS ), "image" );
    }

    @Activate
    @Modified
    public void activate( final PortalConfig config )
    {
        privateCacheControlHeaderConfig = config.media_private_cacheControl();
        publicCacheControlHeaderConfig = config.media_public_cacheControl();
        contentSecurityPolicy = config.media_contentSecurityPolicy();
        contentSecurityPolicySvg = config.media_contentSecurityPolicy_svg();
    }

    @Override
    public boolean canHandle( final WebRequest webRequest )
    {
        return super.canHandle( webRequest ) && isSiteBase( webRequest );
    }

    @Override
    protected PortalResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        WebHandlerHelper.checkAdminAccess( webRequest );

        final String restPath = findRestPath( webRequest );
        final Matcher matcher = PATTERN.matcher( restPath );

        if ( !matcher.find() )
        {
            throw WebException.notFound( "Not a valid image url pattern" );
        }

        final ImageHandlerWorker worker =
            new ImageHandlerWorker( (PortalRequest) webRequest, this.contentService, this.imageService, this.mediaInfoService );
        worker.id = ContentId.from( matcher.group( 1 ) );
        worker.fingerprint = matcher.group( 2 );
        worker.scaleParams = new ScaleParamsParser().parse( matcher.group( 3 ) );
        worker.name = matcher.group( 4 );
        worker.filterParam = getParameter( webRequest, "filter" );
        worker.qualityParam = getParameter( webRequest, "quality" );
        worker.backgroundParam = getParameter( webRequest, "background" );
        worker.privateCacheControlHeaderConfig = this.privateCacheControlHeaderConfig;
        worker.publicCacheControlHeaderConfig = this.publicCacheControlHeaderConfig;
        worker.contentSecurityPolicy = this.contentSecurityPolicy;
        worker.contentSecurityPolicySvg = this.contentSecurityPolicySvg;
        return worker.execute();
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
