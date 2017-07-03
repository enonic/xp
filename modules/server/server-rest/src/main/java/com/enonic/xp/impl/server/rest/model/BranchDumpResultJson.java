package com.enonic.xp.impl.server.rest.model;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.dump.BranchDumpResult;

public class BranchDumpResultJson
{
    private final String branch;

    private final Long numberOfNodes;

    private final String timeUsed;

    private final List<DumpErrorJson> errors;

    private BranchDumpResultJson( final Builder builder )
    {
        this.branch = builder.branch;
        this.numberOfNodes = builder.numberOfNodes;
        this.timeUsed = builder.timeUsed;
        this.errors = builder.errors;
    }

    public static BranchDumpResultJson from( final BranchDumpResult result )
    {
        return BranchDumpResultJson.create().
            branch( result.getBranch().toString() ).
            numberOfNodes( result.getSuccessful() ).
            timeUsed( result.getDuration().toString() ).
            errors( result.getErrors().stream().map( DumpErrorJson::from ).collect( Collectors.toList() ) ).
            build();
    }

    @SuppressWarnings("unused")
    public String getBranch()
    {
        return branch;
    }

    @SuppressWarnings("unused")
    public Long getNumberOfNodes()
    {
        return numberOfNodes;
    }


    @SuppressWarnings("unused")
    public String getTimeUsed()
    {
        return timeUsed;
    }

    @SuppressWarnings("unused")
    public List<DumpErrorJson> getErrors()
    {
        return errors;
    }

    private static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String branch;

        private Long numberOfNodes;

        private String timeUsed;

        private List<DumpErrorJson> errors;

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

        public Builder timeUsed( final String val )
        {
            timeUsed = val;
            return this;
        }

        public Builder errors( final List<DumpErrorJson> errors )
        {
            this.errors = errors;
            return this;
        }

        public BranchDumpResultJson build()
        {
            return new BranchDumpResultJson( this );
        }
    }
}
