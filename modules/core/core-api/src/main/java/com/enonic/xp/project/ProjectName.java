package com.enonic.xp.project;

import java.util.Objects;
import java.util.regex.Pattern;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.repository.RepositoryId;

@Beta
public final class ProjectName
{
    private static final Pattern VALID_PROJECT_NAME_PATTERN = Pattern.compile( "^([a-z0-9\\-])([a-z0-9_\\-])*$" );

    private final String value;

    private ProjectName( final String value )
    {
        Preconditions.checkNotNull( value, "Project name cannot be null" );
        Preconditions.checkArgument( !value.trim().isEmpty(), "Project name cannot be blank" );
        Preconditions.checkArgument( VALID_PROJECT_NAME_PATTERN.matcher( value ).matches(), "Project name format incorrect: " + value );
        this.value = value;
    }

    public static ProjectName from( final String projectName )
    {
        return new ProjectName( projectName );
    }

    public static ProjectName from( final RepositoryId repositoryId )
    {
        final String value = repositoryId.toString();
        return replacePrefix( value );
    }

    private static ProjectName replacePrefix( final String value )
    {
        if ( value.startsWith( ProjectConstants.PROJECT_REPO_ID_PREFIX ) )
        {
            return new ProjectName( value.replace( ProjectConstants.PROJECT_REPO_ID_PREFIX, "" ) );
        }
        return null;
    }

    public RepositoryId getRepoId()
    {
        return RepositoryId.from( ProjectConstants.PROJECT_REPO_ID_PREFIX + value );
    }

    @Override
    public boolean equals( final Object o )
    {
        return ( o instanceof ProjectName ) && Objects.equals( this.value, ( (ProjectName) o ).value );
    }

    @Override
    public int hashCode()
    {
        return this.value.hashCode();
    }

    @Override
    public String toString()
    {
        return this.value;
    }
}
