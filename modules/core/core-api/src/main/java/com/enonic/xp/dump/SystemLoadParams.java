package com.enonic.xp.dump;

import com.enonic.xp.repository.RepositoryIds;

public final class SystemLoadParams
{
    private final String dumpName;

    private final boolean includeVersions;

    private final boolean upgrade;

    private final boolean archive;

    private final SystemLoadListener listener;

    private final RepositoryIds repositories;

    private SystemLoadParams( final Builder builder )
    {
        this.dumpName = builder.dumpName;
        this.includeVersions = builder.includeVersions;
        this.listener = builder.listener;
        this.upgrade = builder.upgrade;
        this.archive = builder.archive;
        this.repositories = builder.repositories;
    }

    public String getDumpName()
    {
        return dumpName;
    }

    public boolean isIncludeVersions()
    {
        return includeVersions;
    }

    public SystemLoadListener getListener()
    {
        return listener;
    }

    public boolean isUpgrade()
    {
        return upgrade;
    }

    public boolean isArchive()
    {
        return archive;
    }

    public RepositoryIds getRepositories()
    {
        return repositories;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String dumpName;

        private boolean includeVersions = false;

        private boolean upgrade = false;

        private boolean archive = true;

        private SystemLoadListener listener;

        private RepositoryIds repositories;

        private Builder()
        {
        }

        public Builder listener( final SystemLoadListener listener )
        {
            this.listener = listener;
            return this;
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

        public Builder upgrade( final boolean upgrade )
        {
            this.upgrade = upgrade;
            return this;
        }

        public Builder archive( final boolean archive )
        {
            this.archive = archive;
            return this;
        }

        public Builder repositories( final RepositoryIds repositories )
        {
            this.repositories = repositories;
            return this;
        }

        public SystemLoadParams build()
        {
            return new SystemLoadParams( this );
        }
    }
}
