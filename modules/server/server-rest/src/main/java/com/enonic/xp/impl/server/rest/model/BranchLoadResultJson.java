package com.enonic.xp.impl.server.rest.model;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.dump.BranchLoadResult;

public class BranchLoadResultJson
{
    private final String branch;

    private final Long successful;

    private final List<LoadErrorJson> errors;

    private BranchLoadResultJson( final Builder builder )
    {
        branch = builder.branch;
        successful = builder.successful;
        errors = builder.errorList;
    }

    public static BranchLoadResultJson from( final BranchLoadResult result )
    {
        return BranchLoadResultJson.create().
            branch( result.getBranch().toString() ).
            successful( result.getSuccessful() ).
            errors( result.getErrors().stream().map( LoadErrorJson::from ).collect( Collectors.toList() ) ).
            build();
    }

    @SuppressWarnings("unused")
    public String getBranch()
    {
        return branch;
    }

    @SuppressWarnings("unused")
    public Long getSuccessful()
    {
        return successful;
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

        private Long successful;

        private List<LoadErrorJson> errorList;

        private Builder()
        {
        }

        public Builder branch( final String val )
        {
            branch = val;
            return this;
        }

        public Builder successful( final Long val )
        {
            successful = val;
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
