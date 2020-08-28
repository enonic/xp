package com.enonic.xp.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.repository.RepositoryId;

import static com.google.common.base.Strings.isNullOrEmpty;

@PublicApi
public class RestoreParams
{
    private final String snapshotName;

    private final RepositoryId repositoryId;

    private final boolean includeIndexedData;

    private final boolean latest;

    private RestoreParams( Builder builder )
    {
        this.snapshotName = builder.snapshotName;
        this.repositoryId = builder.repositoryId;
        this.includeIndexedData = builder.includeIndexedData;
        this.latest = builder.latest;
    }

    public boolean isIncludeIndexedData()
    {
        return includeIndexedData;
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

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String snapshotName;

        private RepositoryId repositoryId;

        private boolean includeIndexedData = true;

        private boolean latest = false;

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

        public Builder setIncludeIndexedData( final boolean includeIndexedData )
        {
            this.includeIndexedData = includeIndexedData;
            return this;
        }

        public Builder latest( final boolean latest )
        {
            this.latest = latest;
            return this;
        }

        private void validate()
        {
            if ( !latest )
            {
                Preconditions.checkArgument( !isNullOrEmpty( snapshotName ), "Snapshot name has to be given" );
            }
        }

        public RestoreParams build()
        {
            return new RestoreParams( this );
        }
    }
}
