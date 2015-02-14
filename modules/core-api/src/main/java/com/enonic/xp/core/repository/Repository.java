package com.enonic.xp.core.repository;

import com.enonic.xp.core.branch.Branch;
import com.enonic.xp.core.branch.Branches;

public class Repository
{
    private final RepositoryId id;

    private final Branches branches;

    private Repository( Builder builder )
    {
        this.branches = builder.branches;
        this.id = builder.id;
    }

    public RepositoryId getId()
    {
        return id;
    }

    public Branches getBranches()
    {
        return branches;
    }

    public static Builder create()
    {
        return new Builder();
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

        if ( id != null ? !id.equals( that.id ) : that.id != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return id != null ? id.hashCode() : 0;
    }

    public static final class Builder
    {
        private RepositoryId id;

        private Branches branches;

        private Builder()
        {
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

        public Builder id( RepositoryId id )
        {
            this.id = id;
            return this;
        }

        public Repository build()
        {
            return new Repository( this );
        }
    }
}
