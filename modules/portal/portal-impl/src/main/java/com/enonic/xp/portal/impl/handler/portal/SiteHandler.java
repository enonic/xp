package com.enonic.xp.portal.impl.handler.portal;

import java.util.concurrent.Callable;
import java.util.regex.MatchResult;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.handler.BasePortalHandler;
import com.enonic.xp.portal.impl.handler.PathMatchers;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.Site;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.WebHandler;


@Component(immediate = true, service = WebHandler.class, configurationPid = "com.enonic.xp.portal")
public class SiteHandler
    extends BasePortalHandler
{
    private final ContentService contentService;

    private final ProjectService projectService;

    @Activate
    public SiteHandler( @Reference final ContentService contentService, @Reference final ProjectService projectService,
                        @Reference final ExceptionMapper exceptionMapper, @Reference final ExceptionRenderer exceptionRenderer )
    {
        this.contentService = contentService;
        this.projectService = projectService;
        this.exceptionMapper = exceptionMapper;
        this.exceptionRenderer = exceptionRenderer;
    }

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        return webRequest.getBasePath().startsWith( PathMatchers.SITE_PREFIX );
    }

    @Override
    protected PortalRequest createPortalRequest( final WebRequest webRequest, final WebResponse webResponse )
    {
        final MatchResult matcher = PathMatchers.site( webRequest );
        if ( !matcher.hasMatch() )
        {
            throw WebException.notFound( "Invalid site URL" );
        }
        final PortalRequest portalRequest = new PortalRequest( webRequest );

        final ProjectName projectName;
        final Branch branch;
        final ContentPath contentPath;
        try
        {
            projectName = ProjectName.from( matcher.group( "project" ) );
            branch = Branch.from( matcher.group( "branch" ) );
            contentPath = ContentPath.from( matcher.group( "path" ) );
        }
        catch ( IllegalArgumentException e )
        {
            throw new WebException( HttpStatus.NOT_FOUND, "Invalid site URL", e );
        }
        final RepositoryId repositoryId = projectName.getRepoId();

        portalRequest.setBaseUri( PathMatchers.SITE_BASE );
        portalRequest.setRepositoryId( repositoryId );
        portalRequest.setBranch( branch );
        portalRequest.setMode( RenderMode.LIVE );
        portalRequest.setContentPath( contentPath );

        final Project project = callAsContentAdmin( repositoryId, branch, () -> projectService.get( projectName ) );
        portalRequest.setProject( project );

        if ( contentPath.isRoot() )
        {
            return portalRequest;
        }

        final Content content = callAsContentAdmin( repositoryId, branch, () -> getContentByPath( contentPath ) );

        if ( content != null )
        {
            portalRequest.setContent( content );
            portalRequest.setContentPath( content.getPath() );
            portalRequest.setSite( content.isSite()
                                       ? (Site) content
                                       : callAsContentAdmin( repositoryId, branch,
                                                             () -> this.contentService.findNearestSiteByPath( contentPath ) ) );
        }

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
        final Context context = ContextAccessor.current();
        return ContextBuilder.from( context )
            .repositoryId( repositoryId )
            .branch( branch )
            .authInfo( AuthenticationInfo.copyOf( context.getAuthInfo() ).principals( RoleKeys.CONTENT_MANAGER_ADMIN ).build() )
            .build()
            .callWith( callable );
    }
}
