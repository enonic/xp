package com.enonic.xp.repository;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;

@PublicApi
public final class Repository
{
    private final RepositoryId id;

    private final Branches branches;

    private final RepositorySettings settings;

    private Repository( Builder builder )
    {
        this.id = builder.id;
        this.branches = builder.branches;
        this.settings = builder.settings == null ? RepositorySettings.create().build() : builder.settings;
    }

    public RepositoryId getId()
    {
        return id;
    }

    public RepositorySettings getSettings()
    {
        return settings;
    }

    public Branches getBranches()
    {
        return branches;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder create( final Repository source )
    {
        return new Builder( source );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Repository that = (Repository) o;

        return id != null ? id.equals( that.id ) : that.id == null;
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }

    public static final class Builder
    {
        private RepositoryId id;

        private RepositorySettings settings;

        private Branches branches;

        private Builder()
        {
        }

        public Builder( final Repository source )
        {
            id = source.id;
            branches = source.branches;
            settings = source.settings;
        }

        public Builder id( final RepositoryId id )
        {
            this.id = id;
            return this;
        }

        public Builder branches( final Branches branches )
        {
            this.branches = branches;
            return this;
        }


        public Builder branches( final Branch... branches )
        {
            this.branches = Branches.from( branches );
            return this;
        }

        public Builder settings( final RepositorySettings settings )
        {
            this.settings = settings;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( branches, "branches cannot be null" );
            Preconditions.checkArgument( branches.contains( RepositoryConstants.MASTER_BRANCH ), "branches must contain master branch." );
        }


        public Repository build()
        {
            validate();
            return new Repository( this );
        }
    }
}
