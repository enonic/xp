package com.enonic.xp.repo.impl.dump;

import java.util.List;

import com.google.common.collect.Lists;

public class DumpResult
{
    private final List<BranchDumpResult> branchResults;

    private DumpResult( final Builder builder )
    {
        branchResults = builder.branchResults;
    }

    public List<BranchDumpResult> getBranchResults()
    {
        return branchResults;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final List<BranchDumpResult> branchResults = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder add( final BranchDumpResult val )
        {
            branchResults.add( val );
            return this;
        }

        public DumpResult build()
        {
            return new DumpResult( this );
        }
    }

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder();
        builder.append( "DumpResult{" );
        this.branchResults.forEach( ( entry ) -> builder.append( entry.toString() ).append( ", " ) );
        builder.append( "}" );
        return builder.toString();
    }
}
