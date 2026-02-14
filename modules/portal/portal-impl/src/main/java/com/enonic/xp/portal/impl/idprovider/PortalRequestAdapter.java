package com.enonic.xp.portal.impl.idprovider;

import java.util.regex.MatchResult;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.handler.PathMatchers;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryUtils;
import com.enonic.xp.web.WebRequest;

class PortalRequestAdapter
{
    public PortalRequest adapt( final WebRequest req )
    {
        final PortalRequest result = new PortalRequest( req );

        baseUri( result );
        return result;
    }

    private static void baseUri( PortalRequest result )
    {
        final String basePath = result.getBasePath();
        if ( basePath.startsWith( PathMatchers.ADMIN_SITE_PREFIX ) )
        {
            final MatchResult matcher = PathMatchers.adminSite( result );
            if ( matcher.hasMatch() )
            {
                final RepositoryId repositoryId;
                final Branch branch;
                final RenderMode mode;
                try
                {
                    mode = RenderMode.from( matcher.group( "mode" ) );
                    repositoryId = RepositoryUtils.fromContentRepoName( matcher.group( "project" ) );
                    branch = Branch.from( matcher.group( "branch" ) );
                }
                catch ( IllegalArgumentException e )
                {
                    return;
                }

                result.setBaseUri( matcher.group( "base" ) );
                result.setMode( mode );
                result.setRepositoryId( repositoryId );
                result.setBranch( branch );
            }
        }
        else if ( basePath.equals( PathMatchers.ADMIN_TOOL_BASE ) )
        {
            result.setBaseUri( PathMatchers.ADMIN_TOOL_BASE );
        }
        else if ( basePath.startsWith( PathMatchers.ADMIN_TOOL_PREFIX ) )
        {
            final MatchResult matcher = PathMatchers.adminTool( result );
            if ( matcher.hasMatch() )
            {
                result.setBaseUri( matcher.group( "base" ) );
            }
            else
            {
                result.setBaseUri( PathMatchers.ADMIN_TOOL_BASE );
            }
        }
        else if ( basePath.startsWith( PathMatchers.SITE_PREFIX ) )
        {
            final MatchResult matcher = PathMatchers.site( result );
            if ( matcher.hasMatch() )
            {
                final RepositoryId repositoryId;
                final Branch branch;
                try
                {
                    repositoryId = RepositoryUtils.fromContentRepoName( matcher.group( "project" ) );
                    branch = Branch.from( matcher.group( "branch" ) );
                }
                catch ( IllegalArgumentException e )
                {
                    return;
                }

                result.setBaseUri( PathMatchers.SITE_BASE );
                result.setRepositoryId( repositoryId );
                result.setBranch( branch );
            }
        }
        else if ( basePath.startsWith( PathMatchers.WEBAPP_PREFIX ) )
        {
            final MatchResult matcher = PathMatchers.webapp( result );

            if ( matcher.hasMatch() )
            {
                result.setBaseUri( matcher.group( "base" ) );
            }
        }
        else if ( basePath.startsWith( PathMatchers.API_PREFIX ) )
        {
            final MatchResult matcher = PathMatchers.api( result );
            if ( matcher.hasMatch() )
            {
                result.setBaseUri( matcher.group( "base" ) );
            }
        }
    }
}
