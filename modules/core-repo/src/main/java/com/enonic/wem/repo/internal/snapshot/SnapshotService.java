package com.enonic.wem.repo.internal.snapshot;

import com.enonic.wem.api.snapshot.RestoreParams;
import com.enonic.wem.api.snapshot.RestoreResult;
import com.enonic.wem.api.snapshot.SnapshotParams;
import com.enonic.wem.api.snapshot.SnapshotResult;

public interface SnapshotService
{
    public SnapshotResult snapshot( final SnapshotParams snapshotParams );

    public RestoreResult restore( final RestoreParams restoreParams );

}
