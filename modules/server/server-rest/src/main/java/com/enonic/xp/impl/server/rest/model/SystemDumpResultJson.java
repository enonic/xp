package com.enonic.xp.impl.server.rest.model;

import java.util.List;

import org.codehaus.jparsec.util.Lists;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.SystemDumpResult;

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
        final ObjectMapper mapper = new ObjectMapper();
        try
        {
            return mapper.writeValueAsString( this );
        }
        catch ( JsonProcessingException e )
        {
            throw new RuntimeException( e );
        }
    }

    private static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final List<RepoDumpResultJson> repositories = Lists.arrayList();

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
