package com.enonic.xp.repository;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.BranchId;
import com.enonic.xp.branch.BranchIds;

@Beta
public final class Repository
{
    private final RepositoryId id;

    private final BranchIds branchIds;

    private Repository( Builder builder )
    {
        this.branchIds = builder.branchIds;
        this.id = builder.id;
    }

    public RepositoryId getId()
    {
        return id;
    }

    public BranchIds getBranchIds()
    {
        return branchIds;
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

        private BranchIds branchIds;

        private Builder()
        {
        }

        public Builder branches( final BranchIds branchIds )
        {
            this.branchIds = branchIds;
            return this;
        }

        public Builder branches( final BranchId... branchIds )
        {
            this.branchIds = BranchIds.from( branchIds );
            return this;
        }

        public Builder id( final RepositoryId id )
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
