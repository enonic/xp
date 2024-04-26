package com.enonic.xp.portal.impl.handler;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.image.ImageService;
import com.enonic.xp.image.ScaleParamsParser;
import com.enonic.xp.media.MediaInfoService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.portal.impl.handler.attachment.AttachmentHandlerWorker;
import com.enonic.xp.portal.impl.handler.image.ImageHandlerWorker;
import com.enonic.xp.project.ProjectConstants;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static com.google.common.base.Strings.nullToEmpty;

@Component(service = MediaHandler.class, configurationPid = "com.enonic.xp.portal")
public class MediaHandler
{

    private static final Pattern PATTERN =
        Pattern.compile( "^/api/media/(?<mediaType>image|attachment)/(?<repo>[^/]+)/(?<branch>[^/]+)/(?<restPath>.*)$" );

    private static final Pattern ATTACHMENT_REST_PATH_PATTERN =
        Pattern.compile( "^(?<id>[^/:]+)(?::(?<fingerprint>[^/]+))?/(?<name>[^/?]+)(?:\\?download)?$" );

    private static final Pattern IMAGE_REST_PATH_PATTERN =
        Pattern.compile( "^(?<id>[^/:]+)(?::(?<fingerprint>[^/]+))?/(?<scaleParams>[^/]+)/(?<name>[^/]+)$" );

    private static final EnumSet<HttpMethod> ALLOWED_METHODS = EnumSet.of( HttpMethod.GET, HttpMethod.HEAD, HttpMethod.OPTIONS );

    private static final Predicate<WebRequest> IS_GET_HEAD_OPTIONS_METHOD = req -> ALLOWED_METHODS.contains( req.getMethod() );

    private static final Predicate<WebRequest> IS_SITE_BASE = req -> req instanceof PortalRequest && ( (PortalRequest) req ).isSiteBase();

    private final ContentService contentService;

    private final ImageService imageService;

    private final MediaInfoService mediaInfoService;

    private volatile String privateCacheControlHeaderConfig;

    private volatile String publicCacheControlHeaderConfig;

    private volatile String contentSecurityPolicy;

    private volatile String contentSecurityPolicySvg;

    private volatile List<PrincipalKey> draftBranchAllowedFor;

    @Activate
    public MediaHandler( @Reference final ContentService contentService, @Reference final ImageService imageService,
                         @Reference final MediaInfoService mediaInfoService )
    {
        this.contentService = contentService;
        this.imageService = imageService;
        this.mediaInfoService = mediaInfoService;
    }

    @Activate
    @Modified
    public void activate( final PortalConfig config )
    {
        this.privateCacheControlHeaderConfig = config.media_private_cacheControl();
        this.publicCacheControlHeaderConfig = config.media_public_cacheControl();
        this.contentSecurityPolicy = config.media_contentSecurityPolicy();
        this.contentSecurityPolicySvg = config.media_contentSecurityPolicy_svg();
        this.draftBranchAllowedFor = Arrays.stream( nullToEmpty( config.draftBranchAllowedFor() ).split( ",", -1 ) )
            .map( String::trim )
            .map( PrincipalKey::from )
            .collect( Collectors.toList() );
    }

    public WebResponse handle( final WebRequest webRequest, final WebResponse webResponse )
        throws Exception
    {
        Matcher matcher = PATTERN.matcher( webRequest.getRawPath() );
        if ( !matcher.matches() )
        {
            return PortalResponse.create( webResponse ).status( HttpStatus.NOT_FOUND ).build();
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

        String repo = matcher.group( "repo" );
        String branch = matcher.group( "branch" );
        String type = matcher.group( "mediaType" );
        String restPath = matcher.group( "restPath" );

        RepositoryId repositoryId = RepositoryId.from( ProjectConstants.PROJECT_REPO_ID_PREFIX + repo );

        PortalRequest portalRequest = createPortalRequest( webRequest, repositoryId, Branch.from( branch ) );

        return executeInContext( repositoryId, branch, () -> type.equals( "attachment" )
            ? doHandleAttachment( portalRequest, restPath )
            : doHandleImage( portalRequest, restPath ) );
    }

    private PortalRequest createPortalRequest( final WebRequest webRequest, final RepositoryId repositoryId, final Branch branch )
    {
        if ( ContentConstants.BRANCH_DRAFT.equals( branch ) )
        {
            final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
            if ( !authInfo.hasRole( RoleKeys.ADMIN ) && authInfo.getPrincipals().stream().noneMatch( draftBranchAllowedFor::contains ) )
            {
                throw WebException.forbidden( "You don't have permission to access this resource" );
            }
        }

        PortalRequest portalRequest = webRequest instanceof PortalRequest ? (PortalRequest) webRequest : new PortalRequest( webRequest );
        portalRequest.setRepositoryId( repositoryId );
        portalRequest.setBranch( branch );
        return portalRequest;
    }

    private PortalResponse executeInContext( final RepositoryId repositoryId, final String branch, final Callable<PortalResponse> callable )
    {
        return ContextBuilder.copyOf( ContextAccessor.current() )
            .repositoryId( repositoryId )
            .branch( branch )
            .build()
            .callWith( callable );
    }

    private PortalResponse doHandleImage( final PortalRequest portalRequest, final String restPath )
        throws Exception
    {
        Matcher matcher = IMAGE_REST_PATH_PATTERN.matcher( restPath );
        if ( !matcher.matches() )
        {
            throw WebException.notFound( "Not a valid image url pattern" );
        }

        final ImageHandlerWorker worker =
            new ImageHandlerWorker( portalRequest, this.contentService, this.imageService, this.mediaInfoService );
        worker.id = ContentId.from( matcher.group( "id" ) );
        worker.fingerprint = matcher.group( "fingerprint" );
        worker.scaleParams = new ScaleParamsParser().parse( matcher.group( "scaleParams" ) );
        worker.name = matcher.group( "name" );
        worker.filterParam = HandlerHelper.getParameter( portalRequest, "filter" );
        worker.qualityParam = HandlerHelper.getParameter( portalRequest, "quality" );
        worker.backgroundParam = HandlerHelper.getParameter( portalRequest, "background" );
        worker.privateCacheControlHeaderConfig = this.privateCacheControlHeaderConfig;
        worker.publicCacheControlHeaderConfig = this.publicCacheControlHeaderConfig;
        worker.contentSecurityPolicy = this.contentSecurityPolicy;
        worker.contentSecurityPolicySvg = this.contentSecurityPolicySvg;
        return worker.execute();
    }

    private PortalResponse doHandleAttachment( final PortalRequest portalRequest, final String restPath )
        throws Exception
    {
        Matcher matcher = ATTACHMENT_REST_PATH_PATTERN.matcher( restPath );
        if ( !matcher.matches() )
        {
            throw WebException.notFound( "Not a valid attachment url pattern" );
        }

        final AttachmentHandlerWorker worker = new AttachmentHandlerWorker( portalRequest, this.contentService );
        worker.download = HandlerHelper.getParameter( portalRequest, "download" ) != null;
        worker.id = ContentId.from( matcher.group( "id" ) );
        worker.fingerprint = matcher.group( "fingerprint" );
        worker.name = matcher.group( "name" );
        worker.privateCacheControlHeaderConfig = this.privateCacheControlHeaderConfig;
        worker.publicCacheControlHeaderConfig = this.publicCacheControlHeaderConfig;
        worker.contentSecurityPolicy = this.contentSecurityPolicy;
        worker.contentSecurityPolicySvg = this.contentSecurityPolicySvg;
        return worker.execute();
    }
}
