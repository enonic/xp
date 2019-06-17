package com.enonic.xp.repository;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.BranchInfo;
import com.enonic.xp.branch.BranchInfos;
import com.enonic.xp.branch.Branches;

@Beta
public final class Repository
{
    private final RepositoryId id;

    private final BranchInfos branchInfos;

    private final RepositorySettings settings;

    private Repository( Builder builder )
    {
        this.id = builder.id;
        this.branchInfos = builder.branchInfos;
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

    public BranchInfos getBranchInfos()
    {
        return branchInfos;
    }

    public Branches getBranches()
    {
        return branchInfos.getBranches();
    }

    public BranchInfos getChildBranchInfos( Branch branch)
    {
        final List<BranchInfo> childBranchInfos = getBranchInfos().
            stream().
            filter( branchInfo -> branch.equals( branchInfo.getParentBranch() ) ).
            collect( Collectors.toList() );
        return BranchInfos.from( childBranchInfos );
    }

    public Branches getChildBranches( Branch branch)
    {
        final List<Branch> childBranches = getBranchInfos().
            stream().
            filter( branchInfo -> branch.equals( branchInfo.getParentBranch() ) ).
            map( BranchInfo::getBranch ).
            collect( Collectors.toList() );
        return Branches.from( childBranches );
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

        private RepositorySettings settings;

        private BranchInfos branchInfos;

        private Builder()
        {
        }

        public Builder( final Repository source )
        {
            id = source.id;
            branchInfos = source.branchInfos;
            settings = source.settings;
        }

        public Builder id( final RepositoryId id )
        {
            this.id = id;
            return this;
        }

        public Builder branchInfos( final BranchInfos branchInfos )
        {
            this.branchInfos = branchInfos;
            return this;
        }


        public Builder branchInfos( final BranchInfo... branchInfos )
        {
            this.branchInfos = BranchInfos.from( branchInfos );
            return this;
        }

        public Builder settings( final RepositorySettings settings )
        {
            this.settings = settings;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( branchInfos, "branchInfos cannot be null" );
            Preconditions.checkArgument( branchInfos.contains( RepositoryConstants.MASTER_BRANCH_INFO ), "branchInfos must contain master branch." );
        }


        public Repository build()
        {
            validate();
            return new Repository( this );
        }
    }
}
