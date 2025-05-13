package com.enonic.xp.dump;


import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.branch.Branch;

public final class BranchDumpResult
{
    private final Branch branch;

    private final Long successful;

    private final List<DumpError> errors;

    private BranchDumpResult( final Builder builder )
    {
        this.branch = builder.branch;
        this.successful = builder.successful;
        this.errors = builder.errors;
    }

    public Branch getBranch()
    {
        return branch;
    }

    public Long getSuccessful()
    {
        return successful;
    }

    public List<DumpError> getErrors()
    {
        return errors;
    }

    public static Builder create( final Branch branch )
    {
        return new Builder( branch );
    }

    public static final class Builder
    {

        private final Branch branch;

        private Long successful = 0L;

        private final List<DumpError> errors = new ArrayList<>();

        private Builder( final Branch branch )
        {
            this.branch = branch;
        }

        public Builder addedNode()
        {
            successful++;
            return this;
        }

        public Builder addedNodes( final long val )
        {
            successful = val;
            return this;
        }


        public Builder error( final DumpError error )
        {
            this.errors.add( error );
            return this;
        }

        public BranchDumpResult build()
        {
            return new BranchDumpResult( this );
        }
    }
}
