package com.enonic.xp.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.repository.RepositoryId;

import static com.google.common.base.Strings.isNullOrEmpty;

@PublicApi
public final class SnapshotParams
{
    final String snapshotName;

    final RepositoryId repositoryId;

    private SnapshotParams( Builder builder )
    {
        this.snapshotName = builder.snapshotName;
        this.repositoryId = builder.repositoryId;
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

    public static final class Builder
    {
        private String snapshotName;

        private RepositoryId repositoryId;

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
