package com.enonic.xp.portal.impl.handler.portal;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.handler.BasePortalHandler;
import com.enonic.xp.portal.impl.PortalConfig;
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

import static com.google.common.base.Strings.nullToEmpty;

@Component(immediate = true, service = WebHandler.class, configurationPid = "com.enonic.xp.portal")
public class SiteHandler
    extends BasePortalHandler
{
    private static final String SITE_BASE = "/site";

    private static final String SITE_PREFIX = SITE_BASE + "/";

    private final ContentService contentService;

    private final ProjectService projectService;

    private List<PrincipalKey> draftBranchAllowedFor;

    @Activate
    public SiteHandler( @Reference final ContentService contentService, @Reference final ProjectService projectService,
                        @Reference final ExceptionMapper exceptionMapper, @Reference final ExceptionRenderer exceptionRenderer )
    {
        this.contentService = contentService;
        this.projectService = projectService;
        this.exceptionMapper = exceptionMapper;
        this.exceptionRenderer = exceptionRenderer;
    }


    @Activate
    @Modified
    public void activate( final PortalConfig config )
    {
        this.draftBranchAllowedFor = Arrays.stream( nullToEmpty( config.draftBranchAllowedFor() ).split( ",", -1 ) )
            .map( String::trim )
            .map( PrincipalKey::from )
            .collect( Collectors.toList() );
    }

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        return webRequest.getRawPath().startsWith( SITE_PREFIX );
    }

    @Override
    protected PortalRequest createPortalRequest( final WebRequest webRequest, final WebResponse webResponse )
    {
        final String baseSubPath = webRequest.getRawPath().substring( ( SITE_PREFIX.length() ) );
        final PortalRequest portalRequest = doCreatePortalRequest( webRequest, baseSubPath );

        final Project project = callInContext( portalRequest.getRepositoryId(), portalRequest.getBranch(), RoleKeys.ADMIN,
                                               () -> projectService.get( ProjectName.from( portalRequest.getRepositoryId() ) ) );

        portalRequest.setProject( project );

        if ( ContentConstants.BRANCH_DRAFT.equals( portalRequest.getBranch() ) )
        {
            final String endpointPath = portalRequest.getEndpointPath();
            if ( endpointPath != null && ( endpointPath.startsWith( "/_/asset/" ) || endpointPath.startsWith( "/_/idprovider/" ) ) )
            {
                return portalRequest;
            }
            final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
            if ( !authInfo.hasRole( RoleKeys.ADMIN ) && authInfo.getPrincipals().stream().noneMatch( draftBranchAllowedFor::contains ) )
            {
                throw WebException.forbidden( "You don't have permission to access this resource" );
            }
        }

        final ContentPath contentPath = portalRequest.getContentPath();
        if ( contentPath.isRoot() )
        {
            return portalRequest;
        }

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

        return portalRequest;
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
        return ContentPath.from( result.startsWith( "/" ) ? result : ( "/" + result ) );
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

    protected PortalRequest doCreatePortalRequest( final WebRequest webRequest, final String baseSubPath )
    {
        final RepositoryId repositoryId = findRepository( baseSubPath );
        final Branch branch = findBranch( baseSubPath );
        final ContentPath contentPath = findContentPath( baseSubPath );

        final PortalRequest portalRequest = new PortalRequest( webRequest );
        portalRequest.setBaseUri( SITE_BASE );
        portalRequest.setRepositoryId( repositoryId );
        portalRequest.setBranch( branch );
        portalRequest.setContentPath( contentPath );
        portalRequest.setMode( RenderMode.LIVE );

        return portalRequest;
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
