package com.enonic.xp.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.repository.RepositoryId;

import static com.google.common.base.Strings.isNullOrEmpty;

@PublicApi
public class SnapshotParams
{
    final String snapshotName;

    final boolean overwrite;

    final RepositoryId repositoryId;

    final boolean includeIndexedData;

    private SnapshotParams( Builder builder )
    {
        this.snapshotName = builder.snapshotName;
        this.overwrite = builder.overwrite;
        this.repositoryId = builder.repositoryId;
        this.includeIndexedData = builder.includeIndexedData;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public String getSnapshotName()
    {
        return snapshotName;
    }

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    public boolean isIncludeIndexedData()
    {
        return includeIndexedData;
    }

    public boolean isOverwrite()
    {
        return overwrite;
    }

    public static final class Builder
    {
        private String snapshotName;

        private boolean overwrite = true;

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

        public Builder overwrite( final boolean overwrite )
        {
            this.overwrite = overwrite;
            return this;
        }

        public Builder setIncludeIndexedData( final boolean includeIndexedData )
        {
            this.includeIndexedData = includeIndexedData;
            return this;
        }

        private void validate()
        {
            Preconditions.checkArgument( !isNullOrEmpty( snapshotName ), "Snapshot name has to be given" );
        }

        public SnapshotParams build()
        {
            validate();
            return new SnapshotParams( this );
        }
    }
}
