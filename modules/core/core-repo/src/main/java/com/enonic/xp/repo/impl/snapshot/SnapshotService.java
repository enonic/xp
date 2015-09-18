package com.enonic.xp.repo.impl.snapshot;

import com.enonic.xp.snapshot.DeleteSnapshotParams;
import com.enonic.xp.snapshot.DeleteSnapshotsResult;
import com.enonic.xp.snapshot.RestoreParams;
import com.enonic.xp.snapshot.RestoreResult;
import com.enonic.xp.snapshot.SnapshotParams;
import com.enonic.xp.snapshot.SnapshotResult;
import com.enonic.xp.snapshot.SnapshotResults;

public interface SnapshotService
{
    public SnapshotResult snapshot( final SnapshotParams snapshotParams );

    public RestoreResult restore( final RestoreParams restoreParams );

    public DeleteSnapshotsResult delete( final DeleteSnapshotParams params );

    public void deleteSnapshotRepository();

    public SnapshotResults list();
}
