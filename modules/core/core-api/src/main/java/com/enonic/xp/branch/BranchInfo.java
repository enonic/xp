package com.enonic.xp.branch;

import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;

@Beta
public final class BranchInfo
{
    private static final String VALID_REPOSITORY_ID_REGEX = "([a-zA-Z0-9\\-:])([a-zA-Z0-9_\\-\\.:])*";

    private final Branch branch;

    private final Branch parentBranch;

    private BranchInfo( final Builder builder )
    {
        Preconditions.checkArgument( builder.branch != null, "Branch cannot be null" );
        this.branch = builder.branch;
        this.parentBranch = builder.parentBranch;
    }

    public static BranchInfo from( final Branch branch )
    {
        return BranchInfo.create().
            branch( branch ).
            build();
    }

    public static BranchInfo from( final Branch branch, final Branch parentBranch )
    {
        return BranchInfo.create().
            branch( branch ).
            parentBranch( parentBranch ).
            build();
    }

    public static BranchInfo from( final String branch )
    {
        return BranchInfo.create().
            branch( branch ).
            build();
    }

    public Branch getBranch()
    {
        return branch;
    }

    public Branch getParentBranch()
    {
        return parentBranch;
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
        final BranchInfo that = (BranchInfo) o;
        return Objects.equals( branch, that.branch ) && Objects.equals( parentBranch, that.parentBranch );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( branch, parentBranch );
    }

    @Override
    public String toString()
    {
        return "BranchInfo{" + "branch=" + branch + ", parentBranch=" + parentBranch + '}';
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private Branch branch;

        private Branch parentBranch;

        private Builder()
        {
        }

        public Builder branch( Branch branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder branch( String branch )
        {
            this.branch = Branch.from( branch );
            return this;
        }

        public Builder parentBranch( Branch parentBranch )
        {
            this.parentBranch = parentBranch;
            return this;
        }

        public Builder parentBranch( String parentBranch )
        {
            this.parentBranch = parentBranch == null ? null : Branch.from( parentBranch );
            return this;
        }

        public BranchInfo build()
        {
            return new BranchInfo( this );
        }
    }
}


