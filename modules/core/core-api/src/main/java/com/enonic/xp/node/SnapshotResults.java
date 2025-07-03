package com.enonic.xp.node;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.support.AbstractImmutableEntityList;
import com.enonic.xp.support.AbstractImmutableEntitySet;

@PublicApi
public final class SnapshotResults
    extends AbstractImmutableEntityList<SnapshotResult>
{
    private SnapshotResults( final ImmutableList<SnapshotResult> list )
    {
        super( list );
    }

    public static Collector<SnapshotResult, ?, SnapshotResults> collecting()
    {
        return Collectors.collectingAndThen( ImmutableList.toImmutableList(), SnapshotResults::new );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private final ImmutableList.Builder<SnapshotResult> snapshotResults = ImmutableList.builder();

        public Builder add( final SnapshotResult snapshotResult )
        {
            this.snapshotResults.add( snapshotResult );
            return this;
        }

        public SnapshotResults build()
        {
            return new SnapshotResults( snapshotResults.build() );
        }
    }
}
