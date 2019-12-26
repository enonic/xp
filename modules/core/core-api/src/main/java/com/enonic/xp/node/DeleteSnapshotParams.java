package com.enonic.xp.node;

import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.enonic.xp.annotation.PublicApi;

@PublicApi
public class DeleteSnapshotParams
{
    private final Set<String> snapshotNames;

    private final Instant before;

    private DeleteSnapshotParams( Builder builder )
    {
        snapshotNames = builder.snapshotNames;
        before = builder.before;
    }

    public Set<String> getSnapshotNames()
    {
        return snapshotNames;
    }

    public Instant getBefore()
    {
        return before;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private final Set<String> snapshotNames = new HashSet<>();

        private Instant before;

        private Builder()
        {
        }

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


        public Builder before( Instant before )
        {
            this.before = before;
            return this;
        }

        public DeleteSnapshotParams build()
        {
            return new DeleteSnapshotParams( this );
        }
    }
}
