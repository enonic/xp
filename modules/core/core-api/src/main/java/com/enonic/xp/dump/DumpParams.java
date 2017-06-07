package com.enonic.xp.dump;

import com.enonic.xp.repository.RepositoryId;

public class DumpParams
{
    private final String dumpName;

    private final RepositoryId repositoryId;

    private final boolean includeVersions;

    private final boolean includeBinaries;

    private DumpParams( final Builder builder )
    {
        dumpName = builder.dumpName;
        repositoryId = builder.repositoryId;
        includeVersions = builder.includeVersions;
        includeBinaries = builder.includeBinaries;
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

    public boolean isIncludeVersions()
    {
        return includeVersions;
    }

    public boolean isIncludeBinaries()
    {
        return includeBinaries;
    }

    public static final class Builder
    {
        private String dumpName;

        private RepositoryId repositoryId;

        private boolean includeVersions = false;

        private boolean includeBinaries = true;

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

        public Builder includeVersions( final boolean val )
        {
            includeVersions = val;
            return this;
        }

        public Builder includeBinaries( final boolean val )
        {
            includeBinaries = val;
            return this;
        }

        public DumpParams build()
        {
            return new DumpParams( this );
        }
    }
}
