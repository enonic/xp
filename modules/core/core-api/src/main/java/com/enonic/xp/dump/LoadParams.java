package com.enonic.xp.dump;

import com.enonic.xp.repository.RepositoryId;

public class LoadParams
{
    private final String dumpName;

    private final RepositoryId repositoryId;

    private LoadParams( final Builder builder )
    {
        dumpName = builder.dumpName;
        repositoryId = builder.repositoryId;
    }

    public String getDumpName()
    {
        return dumpName;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String dumpName;

        private RepositoryId repositoryId;

        private Builder()
        {
        }

        public Builder dumpName( final String val )
        {
            dumpName = val;
            return this;
        }

        public Builder repositoryId( final RepositoryId val )
        {
            repositoryId = val;
            return this;
        }

        public LoadParams build()
        {
            return new LoadParams( this );
        }
    }
}
