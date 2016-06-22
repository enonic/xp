package com.enonic.xp.impl.server.rest.model;

import java.util.Set;

import com.enonic.xp.node.DeleteSnapshotsResult;

public class DeleteSnapshotsResultJson
{
    private final Set<String> deletedSnapshots;

    private DeleteSnapshotsResultJson( final Set<String> deletedSnapshots )
    {
        this.deletedSnapshots = deletedSnapshots;
    }

    public static DeleteSnapshotsResultJson from( final DeleteSnapshotsResult result )
    {
        return new DeleteSnapshotsResultJson( result.getSet() );
    }

    public Set<String> getDeletedSnapshots()
    {
        return deletedSnapshots;
    }
}
