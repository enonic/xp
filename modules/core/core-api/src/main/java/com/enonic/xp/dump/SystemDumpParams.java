package com.enonic.xp.dump;

import com.enonic.xp.repository.RepositoryIds;

public final class SystemDumpParams
{
    private final String dumpName;

    private final boolean includeVersions;

    private final boolean includeBinaries;

    private final Integer maxAge;

    private final Integer maxVersions;

    private final SystemDumpListener listener;

    private final RepositoryIds repositories;

    private SystemDumpParams( final Builder builder )
    {
        dumpName = builder.dumpName;
        includeVersions = builder.includeVersions;
        includeBinaries = builder.includeBinaries;
        maxAge = builder.maxAge;
        maxVersions = builder.maxVersions;
        this.listener = builder.listener;
        this.repositories = builder.repositories != null ? builder.repositories : RepositoryIds.empty();
    }

    public String getDumpName()
    {
        return dumpName;
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

    public Integer getMaxAge()
    {
        return maxAge;
    }

    public Integer getMaxVersions()
    {
        return maxVersions;
    }

    public SystemDumpListener getListener()
    {
        return listener;
    }

    public RepositoryIds getRepositories()
    {
        return repositories;
    }


    public static final class Builder
    {
        private String dumpName;

        private boolean includeVersions = true;

        private boolean includeBinaries = true;

        private Integer maxAge;

        private Integer maxVersions;

        private SystemDumpListener listener;

        private RepositoryIds repositories;


        private Builder()
        {
        }

        public Builder dumpName( final String val )
        {
            dumpName = val;
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

        public Builder maxAge( final Integer val )
        {
            maxAge = val;
            return this;
        }

        public Builder maxVersions( final Integer val )
        {
            maxVersions = val;
            return this;
        }

        public Builder listener( final SystemDumpListener listener )
        {
            this.listener = listener;
            return this;
        }

        public Builder repositories( final RepositoryIds repositories )
        {
            this.repositories = repositories;
            return this;
        }


        public SystemDumpParams build()
        {
            return new SystemDumpParams( this );
        }
    }
}
