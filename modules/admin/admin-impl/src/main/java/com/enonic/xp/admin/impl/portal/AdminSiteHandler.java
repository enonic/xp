package com.enonic.xp.admin.impl.portal;

import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.google.common.net.HttpHeaders;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.handler.BasePortalHandler;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryUtils;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.Site;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

import static com.google.common.base.Strings.nullToEmpty;

@Component(immediate = true, service = WebHandler.class, configurationPid = "com.enonic.xp.admin")
public class AdminSiteHandler
    extends BasePortalHandler
{
    private static final String ADMIN_SITE_PREFIX = "/admin/site/";

    private static final Pattern BASE_URI_PATTERN = Pattern.compile( "^/admin/site/(edit|preview|admin|inline)" );

    private final ContentService contentService;

    private final ProjectService projectService;

    private volatile String previewContentSecurityPolicy;

    @Activate
    public AdminSiteHandler( @Reference final ContentService contentService, @Reference final ProjectService projectService,
                             @Reference final ExceptionMapper exceptionMapper, @Reference final ExceptionRenderer exceptionRenderer )
    {
        this.contentService = contentService;
        this.projectService = projectService;
        this.exceptionMapper = exceptionMapper;
        this.exceptionRenderer = exceptionRenderer;
    }

    @Activate
    @Modified
    public void activate( final AdminConfig config )
    {
        previewContentSecurityPolicy = config.site_preview_contentSecurityPolicy();
    }

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        return webRequest.getRawPath().startsWith( ADMIN_SITE_PREFIX );
    }

    @Override
    protected PortalRequest createPortalRequest( final WebRequest webRequest, final WebResponse webResponse )
    {
        final Matcher matcher = BASE_URI_PATTERN.matcher( webRequest.getRawPath() );
        if ( !matcher.find() )
        {
            throw WebException.notFound( "Mode must be specified" );
        }
        final String baseUri = matcher.group( 0 );
        final RenderMode mode = RenderMode.from( matcher.group( 1 ) );
        final String baseSubPath = webRequest.getRawPath().substring( baseUri.length() + 1 );

        final PortalRequest portalRequest = doCreatePortalRequest( webRequest, baseUri, baseSubPath, mode );

        final Project project = callInContext( portalRequest.getRepositoryId(), portalRequest.getBranch(), RoleKeys.ADMIN,
                                               () -> projectService.get( ProjectName.from( portalRequest.getRepositoryId() ) ) );

        portalRequest.setProject( project );

        final ContentPath contentPath = portalRequest.getContentPath();
        if ( contentPath.isRoot() )
        {
            return portalRequest;
        }

        if ( mode == RenderMode.EDIT )
        {
            final ContentId contentId = tryConvertToContentId( contentPath.toString() );

            final Content contentById = contentId != null ? callAsContentAdmin( portalRequest.getRepositoryId(), portalRequest.getBranch(),
                                                                                () -> getContentById( contentId ) ) : null;

            final Content content = contentById != null
                ? contentById
                : callAsContentAdmin( portalRequest.getRepositoryId(), portalRequest.getBranch(),
                                      () -> this.getContentByPath( contentPath ) );

            if ( content != null && !content.getPath().isRoot() )
            {
                portalRequest.setContent( content );
                portalRequest.setContentPath( content.getPath() );
                portalRequest.setSite( content.isSite()
                                           ? (Site) content
                                           : callAsContentAdmin( portalRequest.getRepositoryId(), portalRequest.getBranch(),
                                                                 () -> this.contentService.getNearestSite( content.getId() ) ) );
            }
        }
        else
        {
            final Content content =
                callAsContentAdmin( portalRequest.getRepositoryId(), portalRequest.getBranch(), () -> getContentByPath( contentPath ) );

            if ( content != null )
            {
                portalRequest.setContent( content );
                portalRequest.setContentPath( content.getPath() );
                portalRequest.setSite( content.isSite()
                                           ? (Site) content
                                           : callAsContentAdmin( portalRequest.getRepositoryId(), portalRequest.getBranch(),
                                                                 () -> this.contentService.findNearestSiteByPath( contentPath ) ) );
            }
        }

        return portalRequest;
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
    {
        final WebResponse response = super.doHandle( webRequest, webResponse, webHandlerChain );
        final PortalRequest request = PortalRequestAccessor.get( webRequest.getRawRequest() );

        final RenderMode mode = request.getMode();

        if ( mode == RenderMode.LIVE || request.getEndpointPath() != null )
        {
            return response;
        }

        final PortalResponse.Builder builder = PortalResponse.create( response );

        if ( mode == RenderMode.INLINE || mode == RenderMode.EDIT )
        {
            builder.header( HttpHeaders.X_FRAME_OPTIONS, "SAMEORIGIN" );
        }

        if ( mode == RenderMode.EDIT )
        {
            builder.removeHeader( HttpHeaders.CONTENT_SECURITY_POLICY );
        }
        else if ( !nullToEmpty( previewContentSecurityPolicy ).isBlank() &&
            !response.getHeaders().containsKey( HttpHeaders.CONTENT_SECURITY_POLICY ) )
        {
            builder.header( HttpHeaders.CONTENT_SECURITY_POLICY, previewContentSecurityPolicy );
        }
        return builder.build();
    }

    private static ContentId tryConvertToContentId( final String contentPathString )
    {
        try
        {
            return ContentId.from( contentPathString.substring( 1 ) );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    private Content getContentById( final ContentId contentId )
    {
        try
        {
            return this.contentService.getById( contentId );
        }
        catch ( final ContentNotFoundException e )
        {
            return null;
        }
    }

    private Content getContentByPath( final ContentPath contentPath )
    {
        try
        {
            return this.contentService.getByPath( contentPath );
        }
        catch ( final ContentNotFoundException e )
        {
            return null;
        }
    }

    private static RepositoryId findRepository( final String baseSubPath )
    {
        final int index = baseSubPath.indexOf( '/' );
        final String result = baseSubPath.substring( 0, index > 0 ? index : baseSubPath.length() );
        if ( result.isEmpty() )
        {
            throw WebException.notFound( "Repository must be specified" );
        }

        try
        {
            return toRepositoryId( result );
        }
        catch ( IllegalArgumentException e )
        {
            throw WebException.notFound( String.format( "Repository name is invalid [%s]", result ) );
        }
    }

    private static RepositoryId toRepositoryId( String result )
    {
        final RepositoryId repositoryId = RepositoryUtils.fromContentRepoName( result );
        if ( repositoryId == null )
        {
            throw new IllegalArgumentException();
        }
        return repositoryId;
    }

    private static Branch findBranch( final String baseSubPath )
    {
        final String branchSubPath = findPathAfterRepository( baseSubPath );
        final int index = branchSubPath.indexOf( '/' );
        final String result = branchSubPath.substring( 0, index > 0 ? index : branchSubPath.length() );
        if ( result.isEmpty() )
        {
            throw WebException.notFound( "Branch must be specified" );
        }
        try
        {
            return Branch.from( result );
        }
        catch ( IllegalArgumentException e )
        {
            throw WebException.notFound( String.format( "Branch name is invalid [%s]", result ) );
        }
    }

    private static ContentPath findContentPath( final String baseSubPath )
    {
        final String branchSubPath = findPathAfterBranch( baseSubPath );
        final int underscore = branchSubPath.indexOf( "/_/" );
        final String result = branchSubPath.substring( 0, underscore > -1 ? underscore : branchSubPath.length() );
        return ContentPath.from( result );
    }

    private static String findPathAfterRepository( final String baseSubPath )
    {
        final int index = baseSubPath.indexOf( '/' );
        return baseSubPath.substring( index > 0 && index < baseSubPath.length() ? index + 1 : baseSubPath.length() );
    }

    private static String findPathAfterBranch( final String baseSubPath )
    {
        final String repoSubPath = findPathAfterRepository( baseSubPath );
        final int index = repoSubPath.indexOf( '/' );

        return index >= 0 ? repoSubPath.substring( index ) : "";
    }

    protected PortalRequest doCreatePortalRequest( final WebRequest webRequest, final String baseUri, final String baseSubPath,
                                                   final RenderMode renderMode )
    {
        final RepositoryId repositoryId = findRepository( baseSubPath );
        final Branch branch = findBranch( baseSubPath );
        final ContentPath contentPath = findContentPath( baseSubPath );

        final PortalRequest portalRequest = new PortalRequest( webRequest );
        portalRequest.setBaseUri( baseUri );
        portalRequest.setRepositoryId( repositoryId );
        portalRequest.setBranch( branch );
        portalRequest.setContentPath( contentPath );
        portalRequest.setMode( renderMode );

        return portalRequest;
    }

    private static <T> T callAsContentAdmin( final RepositoryId repositoryId, final Branch branch, final Callable<T> callable )
    {
        return callInContext( repositoryId, branch, RoleKeys.CONTENT_MANAGER_ADMIN, callable );
    }

    private static <T> T callInContext( final RepositoryId repositoryId, final Branch branch, final PrincipalKey principalKey,
                                        final Callable<T> callable )
    {
        final Context context = ContextAccessor.current();
        return ContextBuilder.from( context )
            .repositoryId( repositoryId )
            .branch( branch )
            .authInfo( AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( principalKey ).build() )
            .build()
            .callWith( callable );
    }
}
