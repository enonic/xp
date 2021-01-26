package com.enonic.xp.repo.impl.dump.serializer;

import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;
import com.enonic.xp.repo.impl.dump.model.CommitDumpEntry;
import com.enonic.xp.repo.impl.dump.model.VersionsDumpEntry;

public interface DumpSerializer
{
    byte[] serialize( BranchDumpEntry branchDumpEntry );

    byte[] serialize( VersionsDumpEntry versionsDumpEntry );

    byte[] serialize( CommitDumpEntry commitDumpEntry );

    BranchDumpEntry toBranchMetaEntry( String value );

    VersionsDumpEntry toNodeVersionsEntry( String value );

    CommitDumpEntry toCommitDumpEntry( String value );
}
