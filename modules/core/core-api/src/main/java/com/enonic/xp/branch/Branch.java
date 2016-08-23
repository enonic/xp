package com.enonic.xp.branch;

import com.google.common.annotations.Beta;

@Beta
public class Branch
{
    private final BranchId branchId;

    private final boolean master;

    private final BranchId masterBranchId;

    private Branch( final Builder builder )
    {
        masterBranchId = builder.masterBranchId;
        master = builder.master;
        branchId = builder.branchId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private BranchId masterBranchId;

        private boolean master;

        private BranchId branchId;

        private Builder()
        {
        }

        public Builder masterBranchId( final BranchId val )
        {
            masterBranchId = val;
            return this;
        }

        public Builder master( final boolean val )
        {
            master = val;
            return this;
        }

        public Builder branchId( final BranchId val )
        {
            branchId = val;
            return this;
        }

        public Branch build()
        {
            return new Branch( this );
        }
    }
}
