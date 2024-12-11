package com.enonic.xp.node;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class DeleteSnapshotsResult
{
    private final Set<String> snapshotNames;

    private final Set<String> failedSnapshotNames;

    private DeleteSnapshotsResult( final Builder builder )
    {
        this.snapshotNames = builder.snapshotNames.build();
        this.failedSnapshotNames = builder.failedSnapshotNames.build();
    }

    public Set<String> getSnapshotNames()
    {
        return snapshotNames;
    }

    public Set<String> getFailedSnapshotNames()
    {
        return failedSnapshotNames;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final ImmutableSet.Builder<String> snapshotNames = ImmutableSet.builder();

        private final ImmutableSet.Builder<String> failedSnapshotNames = ImmutableSet.builder();

        public Builder add( final String snapshotName )
        {
            this.snapshotNames.add( snapshotName );
            return this;
        }

        public Builder addFailed( final String snapshotName )
        {
            this.failedSnapshotNames.add( snapshotName );
            return this;
        }

        public DeleteSnapshotsResult build()
        {
            return new DeleteSnapshotsResult( this );
        }
    }
}
