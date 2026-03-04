package com.enonic.xp.project;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

import com.enonic.xp.core.internal.NameValidator;
import com.enonic.xp.repository.RepositoryId;


public final class ProjectName
    implements Serializable
{
    @Serial
    private static final long serialVersionUID = 0;

    /**
     * ProjectName validator.
     * The Project name is a suffix of RepositoryId identifier, hence the length is even more constrained.
     *
     */
    private static final NameValidator PROJECT_NAME_VALIDATOR = NameValidator.builder( ProjectName.class )
        .maxLength( ProjectConstants.PROJECT_NAME_MAX_LENGTH )
        .regex( Pattern.compile( "^[a-z0-9][a-z0-9_-]*$" ) )
        .build();

    private final String value;

    private ProjectName( final String value )
    {
        this.value = Objects.requireNonNull( value );
    }

    public static ProjectName from( final String projectName )
    {
        return new ProjectName( PROJECT_NAME_VALIDATOR.validate( projectName ) );
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
