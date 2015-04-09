package com.enonic.xp.snapshot;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import com.enonic.xp.repository.RepositoryId;

@Beta
public class RestoreParams
{
    private final String snapshotName;

    private final RepositoryId repositoryId;

    private final boolean includeIndexedData;

    private RestoreParams( Builder builder )
    {
        this.snapshotName = builder.snapshotName;
        this.repositoryId = builder.repositoryId;
        this.includeIndexedData = builder.includeIndexedData;
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

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String snapshotName;

        private RepositoryId repositoryId;

        private boolean includeIndexedData = true;

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

        private void validate()
        {
            Preconditions.checkArgument( !Strings.isNullOrEmpty( snapshotName ), "Snapshot name has to be given" );
            Preconditions.checkArgument( this.repositoryId != null, "Repository must be given" );
        }

        public RestoreParams build()
        {
            return new RestoreParams( this );
        }
    }
}
