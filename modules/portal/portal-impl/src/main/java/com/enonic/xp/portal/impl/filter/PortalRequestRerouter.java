package com.enonic.xp.portal.impl.filter;

import java.util.concurrent.Callable;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryUtils;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.site.Site;

final class PortalRequestRerouter
{
    private final ContentService contentService;

    PortalRequestRerouter( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    void reroute( final PortalRequest request )
    {
        final ContentPath contentPath = request.getContentPath();
        final RepositoryId repositoryId = request.getRepositoryId();
        final Branch branch = request.getBranch();

        request.setContent( null );
        request.setSite( null );
        request.setComponent( null );
        request.setPageDescriptor( null );
        request.setControllerScript( null );

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
        request.setContextPath( request.getBaseUri() + "/" + RepositoryUtils.getContentRepoName( repositoryId ) + "/" + branch +
                                    ( site != null ? site.getPath() : ContentPath.ROOT ) );
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
