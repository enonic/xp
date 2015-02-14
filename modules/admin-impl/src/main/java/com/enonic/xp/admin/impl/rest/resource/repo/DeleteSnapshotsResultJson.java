package com.enonic.xp.admin.impl.rest.resource.repo;

import java.util.Set;

import com.enonic.xp.core.snapshot.DeleteSnapshotsResult;

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
