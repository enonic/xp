package com.enonic.xp.impl.server.rest.model;

import java.time.Duration;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.dump.RepoLoadResult;
import com.enonic.xp.dump.SystemLoadResult;

public class SystemLoadResultJson
{
    private final List<RepoLoadResultJson> repositories;

    private final String duration;

    private SystemLoadResultJson( final Builder builder )
    {
        repositories = builder.repositories;
        duration = builder.duration.toString();
    }

    public static SystemLoadResultJson from( final SystemLoadResult results )
    {
        final Builder builder = SystemLoadResultJson.create();

        for ( final RepoLoadResult repoLoadResult : results )
        {
            builder.add( RepoLoadResultJson.from( repoLoadResult ) );
            builder.addDuration( repoLoadResult.getDuration() );
        }

        return builder.build();
    }

    @SuppressWarnings( "unused" )
    public List<RepoLoadResultJson> getRepositories()
    {
        return repositories;
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
        private final List<RepoLoadResultJson> repositories = Lists.newArrayList();

        private Duration duration = Duration.ZERO;

        private Builder()
        {
        }

        public Builder add( final RepoLoadResultJson val )
        {
            repositories.add( val );
            return this;
        }

        public Builder addDuration( final Duration val )
        {
            this.duration = duration.plus( val );
            return this;
        }

        public SystemLoadResultJson build()
        {
            return new SystemLoadResultJson( this );
        }
    }
}
