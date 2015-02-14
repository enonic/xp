package com.enonic.wem.repo.internal.snapshot;

import com.enonic.xp.core.snapshot.DeleteSnapshotParams;
import com.enonic.xp.core.snapshot.DeleteSnapshotsResult;
import com.enonic.xp.core.snapshot.RestoreParams;
import com.enonic.xp.core.snapshot.RestoreResult;
import com.enonic.xp.core.snapshot.SnapshotResults;
import com.enonic.xp.core.snapshot.SnapshotParams;
import com.enonic.xp.core.snapshot.SnapshotResult;

public interface SnapshotService
{
    public SnapshotResult snapshot( final SnapshotParams snapshotParams );

    public RestoreResult restore( final RestoreParams restoreParams );

    public DeleteSnapshotsResult delete( final DeleteSnapshotParams params );

    public void deleteSnapshotRepository();

    public SnapshotResults list();
}
