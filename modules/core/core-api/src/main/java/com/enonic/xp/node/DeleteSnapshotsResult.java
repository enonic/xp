package com.enonic.xp.node;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
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
        private final Set<String> snapshotNames = new HashSet<>();


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
