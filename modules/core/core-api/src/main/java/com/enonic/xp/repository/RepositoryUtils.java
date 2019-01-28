package com.enonic.xp.repository;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.content.ContentConstants;

public final class RepositoryUtils
{
    public static final String VALID_CONTENT_NAME_REGEX = "([a-z0-9\\-])*";

    public static RepositoryId fromContentRepoName( final String name )
    {
        if ( StringUtils.isBlank( name ) )
        {
            return null;
        }

        return RepositoryId.from( ContentConstants.CONTENT_REPO_ID_PREFIX + name );
    }

    public static String getContentRepoName( final RepositoryId repositoryId )
    {
        if ( !isContentRepo( repositoryId ) )
        {
            throw new IllegalArgumentException( String.format( "'%s' is not a content repository", repositoryId.toString() ) );
        }
        return extractContentRepoName( repositoryId.toString() );
    }

    private static boolean isContentRepo( final RepositoryId repositoryId )
    {
        final String name = extractContentRepoName( repositoryId.toString() );
        return StringUtils.isNotBlank( name ) && name.matches( "^" + VALID_CONTENT_NAME_REGEX + "$" );
    }

    private static String extractContentRepoName( final String value )
    {
        return StringUtils.substringAfter( value, ContentConstants.CONTENT_REPO_ID_PREFIX );
    }
}
