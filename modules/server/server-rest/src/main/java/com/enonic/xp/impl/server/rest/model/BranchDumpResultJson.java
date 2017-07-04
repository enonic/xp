package com.enonic.xp.impl.server.rest.model;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.dump.BranchDumpResult;

public class BranchDumpResultJson
{
    private final String branch;

    private final Long successful;

    private final List<DumpErrorJson> errors;

    private BranchDumpResultJson( final Builder builder )
    {
        this.branch = builder.branch;
        this.successful = builder.successful;
        this.errors = builder.errors;
    }

    public static BranchDumpResultJson from( final BranchDumpResult result )
    {
        return BranchDumpResultJson.create().
            branch( result.getBranch().toString() ).
            successful( result.getSuccessful() ).
            errors( result.getErrors().stream().map( DumpErrorJson::from ).collect( Collectors.toList() ) ).
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

        private Long successful;

        private List<DumpErrorJson> errors;

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
