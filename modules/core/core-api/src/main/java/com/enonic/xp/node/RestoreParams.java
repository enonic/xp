package com.enonic.xp.node;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.repository.RepositoryId;

@PublicApi
public final class RestoreParams
{
    private final String snapshotName;

    private final RepositoryId repositoryId;

    private final boolean latest;

    private final boolean force;

    private RestoreParams( Builder builder )
    {
        this.snapshotName = builder.snapshotName;
        this.repositoryId = builder.repositoryId;
        this.latest = builder.latest;
        this.force = builder.force;
    }

    public String getSnapshotName()
    {
        return snapshotName;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public boolean isLatest()
    {
        return latest;
    }

    public boolean isForce()
    {
        return force;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String snapshotName;

        private RepositoryId repositoryId;

        private boolean latest = false;

        private boolean force = false;

        private Builder()
        {
        }

        public Builder snapshotName( final String snapshotName )
        {
            this.snapshotName = snapshotName;
            return this;
        }

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder latest( final boolean latest )
        {
            this.latest = latest;
            return this;
        }

        public Builder force( final boolean force )
        {
            this.force = force;
            return this;
        }

        public RestoreParams build()
        {
            return new RestoreParams( this );
        }
    }
}
