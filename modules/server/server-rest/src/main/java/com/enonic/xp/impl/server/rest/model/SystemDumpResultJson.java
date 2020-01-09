package com.enonic.xp.impl.server.rest.model;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.SystemDumpResult;
import com.enonic.xp.impl.server.rest.ModelToStringHelper;

public class SystemDumpResultJson
{
    private final List<RepoDumpResultJson> repositories;

    private SystemDumpResultJson( final Builder builder )
    {
        repositories = builder.repositories;
    }

    public static SystemDumpResultJson from( final SystemDumpResult systemDumpResult )
    {
        final SystemDumpResultJson.Builder builder = SystemDumpResultJson.create();

        for ( final RepoDumpResult result : systemDumpResult )
        {
            builder.add( RepoDumpResultJson.from( result ) );
        }

        return builder.build();
    }

    @SuppressWarnings("unused")
    public List<RepoDumpResultJson> getRepositories()
    {
        return repositories;
    }

    @Override
    public String toString()
    {
        return ModelToStringHelper.convertToString( this );
    }

    private static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final List<RepoDumpResultJson> repositories = new ArrayList<>();

        private Builder()
        {
        }

        public Builder add( final RepoDumpResultJson val )
        {
            repositories.add( val );
            return this;
        }

        public SystemDumpResultJson build()
        {
            return new SystemDumpResultJson( this );
        }
    }
}
