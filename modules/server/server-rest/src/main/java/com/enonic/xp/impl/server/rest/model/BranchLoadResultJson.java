package com.enonic.xp.impl.server.rest.model;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.dump.BranchLoadResult;

public class BranchLoadResultJson
{
    private final String branch;

    private final Long numberOfNodes;

    private final Long numberOfVersions;

    private final String duration;

    private final List<LoadErrorJson> errors;

    private BranchLoadResultJson( final Builder builder )
    {
        branch = builder.branch;
        numberOfNodes = builder.numberOfNodes;
        numberOfVersions = builder.numberOfVersions;
        duration = builder.duration;
        errors = builder.errorList;
    }

    public static BranchLoadResultJson from( final BranchLoadResult result )
    {
        return BranchLoadResultJson.create().
            branch( result.getBranch().toString() ).
            duration( result.getDuration().toString() ).
            numberOfNodes( result.getNumberOfNodes() ).
            numberOfVersions( result.getNumberOfVersions() ).
            errors( result.getErrors().stream().map( LoadErrorJson::from ).collect( Collectors.toList() ) ).
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
    public Long getNumberOfVersions()
    {
        return numberOfVersions;
    }

    @SuppressWarnings("unused")
    public String getDuration()
    {
        return duration;
    }

    @SuppressWarnings("unused")
    public List<LoadErrorJson> getErrors()
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

        private Long numberOfVersions;

        private String duration;

        private List<LoadErrorJson> errorList;

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

        public Builder duration( final String val )
        {
            duration = val;
            return this;
        }

        public Builder errors( final List<LoadErrorJson> errors )
        {
            this.errorList = errors;
            return this;
        }

        public BranchLoadResultJson build()
        {
            return new BranchLoadResultJson( this );
        }
    }
}
