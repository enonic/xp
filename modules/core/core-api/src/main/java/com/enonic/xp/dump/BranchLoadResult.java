package com.enonic.xp.dump;

import com.enonic.xp.branch.Branch;

public class BranchLoadResult
    extends LoadResult<BranchLoadResult>
{
    private final Branch branch;

    private BranchLoadResult( final Builder builder )
    {
        super( builder );
        this.branch = builder.branch;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public static Builder create( final Branch branch )
    {
        return new Builder( branch );
    }

    public static final class Builder
        extends LoadResult.Builder<BranchLoadResult, Builder>
    {
        private final Branch branch;

        private Builder( final Branch branch )
        {
            super();
            this.branch = branch;
        }

        public BranchLoadResult build()
        {
            super.build();
            return new BranchLoadResult( this );
        }
    }
}
