package com.enonic.xp.portal.impl.handler;

import java.util.EnumSet;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.handler.WebHandlerHelper;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.portal.impl.handler.attachment.AttachmentHandlerWorker;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;

@Deprecated(since = "8.0", forRemoval = true)
@Component(service = AttachmentHandler.class, configurationPid = "com.enonic.xp.portal")
public class AttachmentHandler
{
    private static final Pattern PATTERN = Pattern.compile( "^([^/]+)/([^/:]+)(?::([^/]+))?/([^/]+)" );

    private static final EnumSet<HttpMethod> ALLOWED_METHODS = EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS );

    private static final Predicate<WebRequest> IS_GET_HEAD_OPTIONS_METHOD = req -> ALLOWED_METHODS.contains( req.getMethod() );

    private static final Predicate<WebRequest> IS_SITE_BASE = req -> req instanceof PortalRequest && ( (PortalRequest) req ).isSiteBase();

    private final ContentService contentService;

    private volatile String privateCacheControlHeaderConfig;

    private volatile String publicCacheControlHeaderConfig;

    private volatile String contentSecurityPolicy;

    private volatile String contentSecurityPolicySvg;

    @Activate
    public AttachmentHandler( @Reference final ContentService contentService )
    {
        this.contentService = contentService;
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

    public PortalResponse handle( final WebRequest webRequest )
        throws Exception
    {
        WebHandlerHelper.checkAdminAccess( webRequest );

        final String restPath = HandlerHelper.findRestPath( webRequest, "attachment" );
        final Matcher matcher = PATTERN.matcher( restPath );

        if ( !matcher.find() )
        {
            throw WebException.notFound( "Not a valid attachment url pattern" );
        }

        if ( !IS_SITE_BASE.test( webRequest ) )
        {
            throw WebException.notFound( "Not a valid request" );
        }

        if ( !IS_GET_HEAD_OPTIONS_METHOD.test( webRequest ) )
        {
            throw new WebException( HttpStatus.METHOD_NOT_ALLOWED, String.format( "Method %s not allowed", webRequest.getMethod() ) );
        }

        if ( webRequest.getMethod() == HttpMethod.OPTIONS )
        {
            return HandlerHelper.handleDefaultOptions( ALLOWED_METHODS );
        }

        final AttachmentHandlerWorker worker = new AttachmentHandlerWorker( (PortalRequest) webRequest, this.contentService );
        worker.download = "download".equals( matcher.group( 1 ) );
        worker.id = ContentId.from( matcher.group( 2 ) );
        worker.fingerprint = matcher.group( 3 );
        worker.name = matcher.group( 4 );
        worker.privateCacheControlHeaderConfig = this.privateCacheControlHeaderConfig;
        worker.publicCacheControlHeaderConfig = this.publicCacheControlHeaderConfig;
        worker.contentSecurityPolicy = this.contentSecurityPolicy;
        worker.contentSecurityPolicySvg = this.contentSecurityPolicySvg;
        return worker.execute();
    }
}
