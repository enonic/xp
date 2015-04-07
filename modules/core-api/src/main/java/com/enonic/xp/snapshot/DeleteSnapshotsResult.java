package com.enonic.xp.snapshot;

import java.util.Collection;
import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
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
