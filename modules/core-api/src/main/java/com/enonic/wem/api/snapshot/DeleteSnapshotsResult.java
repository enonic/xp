package com.enonic.wem.api.snapshot;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public class DeleteSnapshotsResult
    extends AbstractImmutableEntitySet<String>
{
    private DeleteSnapshotsResult( final Builder builder )
    {
        super( ImmutableSet.copyOf( builder.snapshotNames ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final Set<String> snapshotNames = Sets.newHashSet();


        public Builder add( final String snapshotName )
        {
            this.snapshotNames.add( snapshotName );
            return this;
        }

        public Builder addAll( final Collection<String> snapshotNames )
        {
            this.snapshotNames.addAll( snapshotNames );
            return this;
        }

        public DeleteSnapshotsResult build()
        {
            return new DeleteSnapshotsResult( this );
        }
    }
}
