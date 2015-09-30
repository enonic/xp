package com.enonic.wem.repo.internal.snapshot;

import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.DeleteSnapshotsResult;
import com.enonic.xp.node.RestoreParams;
import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;

public interface SnapshotService
{
    SnapshotResult snapshot( final SnapshotParams snapshotParams );

    RestoreResult restore( final RestoreParams restoreParams );

    DeleteSnapshotsResult delete( final DeleteSnapshotParams params );

    void deleteSnapshotRepository();

    SnapshotResults list();
}
