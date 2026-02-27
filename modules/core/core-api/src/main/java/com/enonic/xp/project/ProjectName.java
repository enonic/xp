package com.enonic.xp.project;

import java.util.Objects;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.core.internal.NameValidator;
import com.enonic.xp.repository.RepositoryId;

@PublicApi
public final class ProjectName
{
    private final String value;

    private ProjectName( final String value )
    {
        this.value = Objects.requireNonNull( value );
    }

    public static ProjectName from( final String projectName )
    {
        return new ProjectName( NameValidator.requireValidProjectName( projectName ) );
    }

    public static ProjectName from( final RepositoryId repositoryId )
    {
        return replacePrefix( repositoryId.toString() );
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
        return this == o || o instanceof ProjectName && this.value.equals( ( (ProjectName) o ).value );
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
