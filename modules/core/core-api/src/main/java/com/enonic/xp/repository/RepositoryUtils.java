package com.enonic.xp.repository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.content.ContentConstants;

public final class RepositoryUtils
{
    private static final String CONTENT_NAME_REGEX = "([a-z0-9\\-]+)";

    private static final Pattern CONTENT_REPOSITORY_ID_PATTERN =
        Pattern.compile( "^" + ContentConstants.CONTENT_REPO_ID_PREFIX.replace( ".", "\\." ) + CONTENT_NAME_REGEX + "$" );

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
        final String contentRepoName = extractContentRepoName( repositoryId );
        if ( contentRepoName == null )
        {
            throw new IllegalArgumentException( String.format( "'%s' is not a content repository", repositoryId.toString() ) );
        }
        return contentRepoName;
    }

    private static String extractContentRepoName( final RepositoryId repositoryId )
    {
        if ( repositoryId == null || StringUtils.isBlank( repositoryId.toString() ) )
        {
            return null;
        }
        final Matcher matcher = CONTENT_REPOSITORY_ID_PATTERN.matcher( repositoryId.toString() );

        if ( !matcher.find() )
        {
            return null;
        }

        return matcher.group( 1 );
    }
}
