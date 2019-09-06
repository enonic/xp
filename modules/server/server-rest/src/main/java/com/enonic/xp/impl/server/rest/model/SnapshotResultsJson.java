package com.enonic.xp.impl.server.rest.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;

public class SnapshotResultsJson
{
    private final List<SnapshotResultJson> results;

    private SnapshotResultsJson( final List<SnapshotResultJson> results )
    {
        this.results = results;
    }

    public static SnapshotResultsJson from( final SnapshotResults snapshotResults )
    {
        final ImmutableList.Builder<SnapshotResultJson> results = ImmutableList.builder();

        for ( final SnapshotResult result : snapshotResults )
        {
            results.add( SnapshotResultJson.from( result ) );
        }

        return new SnapshotResultsJson( results.build() );
    }

    public List<SnapshotResultJson> getResults()
    {
        return results;
    }
}
