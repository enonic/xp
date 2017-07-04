package com.enonic.xp.repo.impl.dump.serializer;

import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;
import com.enonic.xp.repo.impl.dump.model.VersionsDumpEntry;

public interface DumpSerializer
{
    String serialize( final BranchDumpEntry branchDumpEntry );

    String serialize( final VersionsDumpEntry versionsDumpEntry );

    BranchDumpEntry toBranchMetaEntry( final String value );

    VersionsDumpEntry toNodeVersionsEntry( final String value );
}
