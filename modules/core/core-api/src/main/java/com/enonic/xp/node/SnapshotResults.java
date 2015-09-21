package com.enonic.xp.node;

import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;

@Beta
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
        private final Set<SnapshotResult> snapshotResults = Sets.newLinkedHashSet();


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
