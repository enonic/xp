package com.enonic.xp.portal.impl.filter;

import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryUtils;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.Site;
import com.enonic.xp.web.WebException;

final class PortalRequestRerouter
{
    private static final Pattern SITE_RELATIVE_PATH_PATTERN = Pattern.compile( "^/(?<project>[^/]+)/(?<branch>[^/]+)(?<path>.*)$" );

    private final ContentService contentService;

    private final ProjectService projectService;

    PortalRequestRerouter( final ContentService contentService, final ProjectService projectService )
    {
        this.contentService = contentService;
        this.projectService = projectService;
    }

    void reroute( final PortalRequest request )
    {
        final String baseUri = request.getBaseUri();
        final String basePath = request.getBasePath();

        if ( baseUri == null || baseUri.isEmpty() || !basePath.startsWith( baseUri + "/" ) )
        {
            throw WebException.notFound( "Invalid site URL" );
        }

        final Matcher matcher = SITE_RELATIVE_PATH_PATTERN.matcher( basePath.substring( baseUri.length() ) );
        if ( !matcher.matches() )
        {
            throw WebException.notFound( "Invalid site URL" );
        }

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
            throw WebException.badRequest( "Invalid site URL", e );
        }

        final RepositoryId repositoryId = projectName.getRepoId();

        request.setRepositoryId( repositoryId );
        request.setBranch( branch );
        request.setContentPath( contentPath );
        request.setContent( null );
        request.setSite( null );
        request.setComponent( null );
        request.setPageDescriptor( null );
        request.setControllerScript( null );

        request.setProject( callAsContentAdmin( repositoryId, branch, () -> projectService.get( projectName ) ) );

        if ( !contentPath.isRoot() )
        {
            final Content content = callAsContentAdmin( repositoryId, branch, () -> getContentByPath( contentPath ) );

            if ( content != null )
            {
                request.setContent( content );
                request.setContentPath( content.getPath() );
                request.setSite( content.isSite()
                                     ? (Site) content
                                     : callAsContentAdmin( repositoryId, branch,
                                                           () -> this.contentService.findNearestSiteByPath( content.getPath() ) ) );
            }
            else
            {
                request.setSite(
                    callAsContentAdmin( repositoryId, branch, () -> this.contentService.findNearestSiteByPath( contentPath ) ) );
            }
        }

        final Site site = request.getSite();
        request.setContextPath( baseUri + "/" + RepositoryUtils.getContentRepoName( repositoryId ) + "/" + branch +
                                    ( site != null ? site.getPath() : ContentPath.ROOT ) );

        final Context context = ContextAccessor.current();
        context.getLocalScope().setAttribute( repositoryId );
        context.getLocalScope().setAttribute( branch );
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
