package com.enonic.xp.repo.impl.dump.writer;

import java.io.Closeable;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;
import com.enonic.xp.repo.impl.dump.model.CommitDumpEntry;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.model.VersionsDumpEntry;
import com.enonic.xp.repository.RepositoryId;

public interface DumpWriter
    extends Closeable
{
    void writeDumpMetaData( DumpMeta dumpMeta );

    void openBranchMeta( RepositoryId repositoryId, Branch branch );

    void openVersionsMeta( RepositoryId repositoryId );

    void openCommitsMeta( RepositoryId repositoryId );

    void closeMeta();

    void writeBranchEntry( BranchDumpEntry branchDumpEntry );

    void writeVersionsEntry( VersionsDumpEntry versionsDumpEntry );

    void writeCommitEntry( CommitDumpEntry commitDumpEntry );

    void writeNodeVersionBlobs( RepositoryId repositoryId, NodeVersionKey nodeVersionKey );

    void writeBinaryBlob( RepositoryId repositoryId, BlobKey key );
}
