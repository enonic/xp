package com.enonic.wem.api.snapshot;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.wem.api.support.AbstractImmutableEntitySet;

public class SnapshotResults
    extends AbstractImmutableEntitySet<SnapshotResult>
{
    private SnapshotResults( final Builder builder )
    {
        super( ImmutableSet.copyOf( builder.snapshotResults ) );
    }

    public static Builder create()
    {

        return new Builder();
    }

    public static class Builder
    {
        private final Set<SnapshotResult> snapshotResults = Sets.newHashSet();


        public Builder add( final SnapshotResult snapshotResult )
        {
            this.snapshotResults.add( snapshotResult );
            return this;
        }

        public SnapshotResults build()
        {
            return new SnapshotResults( this );
        }

    }

}
