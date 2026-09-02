package com.enonic.xp.app.system.json;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.dump.RepoLoadResult;
import com.enonic.xp.dump.SystemLoadResult;
import com.enonic.xp.app.system.json.JsonHelper;

public class SystemLoadResultJson
{
    private final List<RepoLoadResultJson> repositories;

    private SystemLoadResultJson( final Builder builder )
    {
        repositories = builder.repositories;
    }

    public static SystemLoadResultJson from( final SystemLoadResult results )
    {
        final Builder builder = SystemLoadResultJson.create();

        for ( final RepoLoadResult repoLoadResult : results )
        {
            builder.add( RepoLoadResultJson.from( repoLoadResult ) );
        }

        return builder.build();
    }

    @SuppressWarnings("unused")
    public List<RepoLoadResultJson> getRepositories()
    {
        return repositories;
    }

    @Override
    public String toString()
    {
        return JsonHelper.toJson( this );
    }

    private static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final List<RepoLoadResultJson> repositories = new ArrayList<>();

        private Builder()
        {
        }

        public Builder add( final RepoLoadResultJson val )
        {
            repositories.add( val );
            return this;
        }

        public SystemLoadResultJson build()
        {
            return new SystemLoadResultJson( this );
        }
    }
}
