package com.enonic.wem.repo.internal.snapshot;

import com.enonic.wem.api.snapshot.DeleteSnapshotParams;
import com.enonic.wem.api.snapshot.DeleteSnapshotsResult;
import com.enonic.wem.api.snapshot.RestoreParams;
import com.enonic.wem.api.snapshot.RestoreResult;
import com.enonic.wem.api.snapshot.SnapshotResults;
import com.enonic.wem.api.snapshot.SnapshotParams;
import com.enonic.wem.api.snapshot.SnapshotResult;

public interface SnapshotService
{
    public SnapshotResult snapshot( final SnapshotParams snapshotParams );

    public RestoreResult restore( final RestoreParams restoreParams );

    public DeleteSnapshotsResult delete( final DeleteSnapshotParams params );

    public void deleteSnapshotRepository();

    public SnapshotResults list();
}
