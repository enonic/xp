package com.enonic.xp.repo.impl.snapshot;

import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.DeleteSnapshotsResult;
import com.enonic.xp.node.RestoreParams;
import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;

public interface SnapshotService
{
    public SnapshotResult snapshot( final SnapshotParams snapshotParams );

    public RestoreResult restore( final RestoreParams restoreParams );

    public DeleteSnapshotsResult delete( final DeleteSnapshotParams params );

    public void deleteSnapshotRepository();

    public SnapshotResults list();
}
