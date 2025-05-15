package com.enonic.xp.dump;

import com.enonic.xp.branch.Branch;

public final class BranchLoadResult
    extends AbstractLoadResult
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
        extends AbstractLoadResult.Builder<BranchLoadResult, Builder>
    {
        private final Branch branch;

        private Builder( final Branch branch )
        {
            super();
            this.branch = branch;
        }

        @Override
        public BranchLoadResult build()
        {
            return new BranchLoadResult( this );
        }
    }
}
