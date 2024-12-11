package com.enonic.xp.impl.server.rest.model;

import java.util.Set;

import com.enonic.xp.node.DeleteSnapshotsResult;

public class DeleteSnapshotsResultJson
{
    private final Set<String> deletedSnapshots;

    private final Set<String> failedSnapshots;

    private DeleteSnapshotsResultJson( final Set<String> deletedSnapshots, final Set<String> failedSnapshots )
    {
        this.deletedSnapshots = deletedSnapshots;
        this.failedSnapshots = failedSnapshots;
    }

    public static DeleteSnapshotsResultJson from( final DeleteSnapshotsResult result )
    {
        return new DeleteSnapshotsResultJson( result.getSet(), result.getFailedSnapshotNames() );
    }

    public Set<String> getDeletedSnapshots()
    {
        return deletedSnapshots;
    }

    public Set<String> getFailedSnapshots()
    {
        return failedSnapshots;
    }
}
