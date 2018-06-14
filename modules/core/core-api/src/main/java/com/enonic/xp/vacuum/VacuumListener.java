package com.enonic.xp.vacuum;

import com.enonic.xp.blob.Segment;
import com.enonic.xp.repository.RepositoryId;

public interface VacuumListener
{
    void vacuumingBlobSegment( Segment segment );

    void vacuumingBlob( long count );

    void vacuumingVersionRepository( RepositoryId repository, long total );

    void vacuumingVersion( long count );
}
