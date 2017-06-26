package com.enonic.xp.impl.server.rest.model;

import java.time.Duration;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.RepoDumpResult;

public class RepoDumpResultJson
{
    private final List<BranchDumpResultJson> branches;

    private final String repository;

    private final String duration;

    private RepoDumpResultJson( final Builder builder )
    {
        this.branches = builder.branches;
        this.repository = builder.repository;
        this.duration = builder.duration.toString();
    }

    public static RepoDumpResultJson from( final RepoDumpResult repoDumpResult )
    {
        final Builder builder = RepoDumpResultJson.create();

        for ( final BranchDumpResult result : repoDumpResult )
        {
            builder.add( BranchDumpResultJson.from( result ) );
            builder.addDuration( result.getDuration() );
        }

        builder.repository = repoDumpResult.getRepositoryId().toString();

        return builder.build();
    }

    @SuppressWarnings( "unused" )
    public List<BranchDumpResultJson> getBranches()
    {
        return branches;
    }

    @SuppressWarnings( "unused" )
    public String getRepositoryId()
    {
        return repository;
    }

    @SuppressWarnings( "unused" )
    public String getDuration()
    {
        return duration;
    }

    private static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final List<BranchDumpResultJson> branches = Lists.newArrayList();

        private String repository;

        private Duration duration = Duration.ZERO;

        private Builder()
        {
        }

        public Builder add( final BranchDumpResultJson val )
        {
            branches.add( val );
            return this;
        }

        public Builder repository( final String val )
        {
            repository = val;
            return this;
        }

        public RepoDumpResultJson build()
        {
            return new RepoDumpResultJson( this );
        }

        public Builder addDuration( final Duration duration )
        {
            this.duration = this.duration.plus( duration );
            return this;
        }
    }
}
