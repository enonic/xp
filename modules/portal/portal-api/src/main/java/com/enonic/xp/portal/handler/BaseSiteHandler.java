package com.enonic.xp.portal.handler;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryUtils;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;

public abstract class BaseSiteHandler
    extends BasePortalHandler
{
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
}
