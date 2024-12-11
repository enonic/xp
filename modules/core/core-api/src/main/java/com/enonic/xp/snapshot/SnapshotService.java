package com.enonic.xp.snapshot;

import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.DeleteSnapshotsResult;
import com.enonic.xp.node.RestoreParams;
import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;

public interface SnapshotService
{
    SnapshotResult snapshot( SnapshotParams snapshotParams );

    RestoreResult restore( RestoreParams restoreParams );

    DeleteSnapshotsResult delete( DeleteSnapshotParams params );

    SnapshotResults list();
}
