package com.enonic.xp.node;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public final class DeleteSnapshotsResult
{
    private final Set<String> deletedSnapshots;

    private final Set<String> failedSnapshots;

    private DeleteSnapshotsResult( final Builder builder )
    {
        this.deletedSnapshots = builder.deletedSnapshots.build();
        this.failedSnapshots = builder.failedSnapshots.build();
    }

    public Set<String> getDeletedSnapshots()
    {
        return deletedSnapshots;
    }

    public Set<String> getFailedSnapshots()
    {
        return failedSnapshots;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final ImmutableSet.Builder<String> deletedSnapshots = ImmutableSet.builder();

        private final ImmutableSet.Builder<String> failedSnapshots = ImmutableSet.builder();

        public Builder add( final String snapshotName )
        {
            this.deletedSnapshots.add( snapshotName );
            return this;
        }

        public Builder addFailed( final String snapshotName )
        {
            this.failedSnapshots.add( snapshotName );
            return this;
        }

        public DeleteSnapshotsResult build()
        {
            return new DeleteSnapshotsResult( this );
        }
    }
}
