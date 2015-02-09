package com.enonic.wem.admin.rest.resource.repo;

import java.util.Set;

import com.enonic.wem.api.snapshot.DeleteSnapshotsResult;

public class DeleteSnapshotsResultJson
{
    private final Set<String> snapshots;

    private DeleteSnapshotsResultJson( final Set<String> snapshots )
    {
        this.snapshots = snapshots;
    }

    public static DeleteSnapshotsResultJson from( final DeleteSnapshotsResult result )
    {
        return new DeleteSnapshotsResultJson( result.getSet() );
    }

    public Set<String> getSnapshots()
    {
        return snapshots;
    }
}
