package com.enonic.xp.impl.server.rest.model;

import java.time.Duration;
import java.util.List;

import org.codehaus.jparsec.util.Lists;

import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.SystemDumpResult;

public class SystemDumpResultJson
{
    private final List<RepoDumpResultJson> repositories;

    private final String duration;

    private SystemDumpResultJson( final Builder builder )
    {
        repositories = builder.repositories;
        this.duration = builder.duration.toString();
    }

    public static SystemDumpResultJson from( final SystemDumpResult systemDumpResult )
    {
        final SystemDumpResultJson.Builder builder = SystemDumpResultJson.create();

        for ( final RepoDumpResult result : systemDumpResult )
        {
            builder.add( RepoDumpResultJson.from( result ) );
            builder.addDuration( Duration.parse( result.getDuration() ) );
        }

        return builder.build();
    }

    @SuppressWarnings("unused")
    public List<RepoDumpResultJson> getRepositories()
    {
        return repositories;
    }

    @SuppressWarnings("unused")
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
        private final List<RepoDumpResultJson> repositories = Lists.arrayList();

        private Duration duration = Duration.ZERO;

        private Builder()
        {
        }

        public Builder add( final RepoDumpResultJson val )
        {
            repositories.add( val );
            return this;
        }

        public Builder addDuration( final Duration duration )
        {
            this.duration = this.duration.plus( duration );
            return this;
        }

        public SystemDumpResultJson build()
        {
            return new SystemDumpResultJson( this );
        }
    }
}
