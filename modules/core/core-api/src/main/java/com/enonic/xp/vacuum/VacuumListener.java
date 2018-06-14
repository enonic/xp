package com.enonic.xp.vacuum;

import com.enonic.xp.blob.Segment;
import com.enonic.xp.repository.RepositoryId;

public interface VacuumListener
{
    void vacuumTaskStarted( String taskName, int taskIndex, int taskTotal );

    void vacuumingBlob( Segment segment, long blobCount );

    void vacuumingVersion( RepositoryId repository, long versionIndex, long versionTotal );
}
