package com.enonic.xp.impl.server.rest.model;

import com.enonic.xp.dump.BranchDumpResult;

public class BranchDumpResultJson
{
    private final String branch;

    private final Long numberOfNodes;

    private final Long numberOfVersions;

    private final String timeUsed;

    private BranchDumpResultJson( final Builder builder )
    {
        branch = builder.branch;
        numberOfNodes = builder.numberOfNodes;
        numberOfVersions = builder.numberOfVersions;
        timeUsed = builder.timeUsed;
    }

    public static BranchDumpResultJson from( final BranchDumpResult result )
    {
        return BranchDumpResultJson.create().
            branch( result.getBranch().toString() ).
            numberOfNodes( result.getNumberOfNodes() ).
            numberOfVersions( result.getNumberOfVersions() ).
            timeUsed( result.getTimeUsed().toString() ).
            build();
    }

    @SuppressWarnings( "unused" )
    public String getBranch()
    {
        return branch;
    }

    @SuppressWarnings( "unused" )
    public Long getNumberOfNodes()
    {
        return numberOfNodes;
    }

    @SuppressWarnings( "unused" )
    public Long getNumberOfVersions()
    {
        return numberOfVersions;
    }

    @SuppressWarnings( "unused" )
    public String getTimeUsed()
    {
        return timeUsed;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String branch;

        private Long numberOfNodes;

        private Long numberOfVersions;

        private String timeUsed;

        private Builder()
        {
        }

        public Builder branch( final String val )
        {
            branch = val;
            return this;
        }

        public Builder numberOfNodes( final Long val )
        {
            numberOfNodes = val;
            return this;
        }

        public Builder numberOfVersions( final Long val )
        {
            numberOfVersions = val;
            return this;
        }

        public Builder timeUsed( final String val )
        {
            timeUsed = val;
            return this;
        }

        public BranchDumpResultJson build()
        {
            return new BranchDumpResultJson( this );
        }
    }
}
