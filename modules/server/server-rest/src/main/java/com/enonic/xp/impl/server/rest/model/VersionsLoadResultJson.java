package com.enonic.xp.impl.server.rest.model;

import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.dump.VersionsLoadResult;

public class VersionsLoadResultJson
{
    private final Long successful;

    private final String duration;

    private final List<LoadErrorJson> errors;

    private VersionsLoadResultJson( final Builder builder )
    {
        successful = builder.successful;
        duration = builder.duration;
        errors = builder.errorList;
    }

    public static VersionsLoadResultJson from( final VersionsLoadResult result )
    {
        return VersionsLoadResultJson.create().
            duration( result.getDuration().toString() ).
            successful( result.getSuccessful() ).
            errors( result.getErrors().stream().map( LoadErrorJson::from ).collect( Collectors.toList() ) ).
            build();
    }

    @SuppressWarnings("unused")
    public Long getSuccessful()
    {
        return successful;
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
        private Long successful;

        private String duration;

        private List<LoadErrorJson> errorList;

        private Builder()
        {
        }

        public Builder successful( final Long val )
        {
            successful = val;
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

        public VersionsLoadResultJson build()
        {
            return new VersionsLoadResultJson( this );
        }
    }


}
