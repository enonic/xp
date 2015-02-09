package com.enonic.wem.admin.rest.resource.repo;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.snapshot.SnapshotResult;
import com.enonic.wem.api.snapshot.SnapshotResults;

public class SnapshotResultsJson
{
    private final List<SnapshotResultJson> results;

    private SnapshotResultsJson( final List<SnapshotResultJson> results )
    {
        this.results = results;
    }

    public static SnapshotResultsJson from( final SnapshotResults snapshotResults )
    {
        final List<SnapshotResultJson> results = Lists.newLinkedList();

        for ( final SnapshotResult result : snapshotResults )
        {
            results.add( SnapshotResultJson.from( result ) );
        }

        return new SnapshotResultsJson( results );
    }

    public List<SnapshotResultJson> getResults()
    {
        return results;
    }
}
