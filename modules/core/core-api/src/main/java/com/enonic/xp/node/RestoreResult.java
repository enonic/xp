package com.enonic.xp.node;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.repository.RepositoryId;

@PublicApi
public class RestoreResult
{
    final Set<String> indices;

    final String name;

    final boolean failed;

    final String message;

    final RepositoryId repositoryId;

    private RestoreResult( Builder builder )
    {
        this.indices = builder.indices;
        this.name = builder.name;
        this.failed = builder.failed;
        this.message = builder.message;
        this.repositoryId = builder.repositoryId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Set<String> getIndices()
    {
        return indices;
    }

    public String getName()
    {
        return name;
    }

    public boolean isFailed()
    {
        return failed;
    }

    public String getMessage()
    {
        return message;
    }

    public static final class Builder
    {
        private Set<String> indices = new HashSet<>();

        private String name;

        private String message;

        private boolean failed = false;

        private RepositoryId repositoryId;

        private Builder()
        {
        }

        public Builder indices( final Collection<String> indices )
        {
            this.indices.addAll( indices );
            return this;
        }

        public Builder name( String name )
        {
            this.name = name;
            return this;
        }

        public Builder message( final String message )
        {
            this.message = message;
            return this;
        }

        public Builder failed( final boolean failed )
        {
            this.failed = failed;
            return this;
        }

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public RestoreResult build()
        {
            return new RestoreResult( this );
        }
    }
}
