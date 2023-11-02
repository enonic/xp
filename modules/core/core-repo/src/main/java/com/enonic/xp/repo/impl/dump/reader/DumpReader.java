package com.enonic.xp.repo.impl.dump.reader;

import java.io.Closeable;

import com.google.common.io.ByteSource;
import com.google.common.io.LineProcessor;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.dump.BranchLoadResult;
import com.enonic.xp.dump.CommitsLoadResult;
import com.enonic.xp.dump.VersionsLoadResult;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;

public interface DumpReader
    extends Closeable
{
    RepositoryIds getRepositories();

    Branches getBranches( RepositoryId repositoryId );

    BranchLoadResult loadBranch( RepositoryId repositoryId, Branch branch, LineProcessor<EntryLoadResult> processor );

    VersionsLoadResult loadVersions( RepositoryId repositoryId, LineProcessor<EntryLoadResult> processor );

    CommitsLoadResult loadCommits( RepositoryId repositoryId, LineProcessor<EntryLoadResult> processor );

    NodeVersion get( RepositoryId repositoryId, NodeVersionKey nodeVersionKey );

    ByteSource getBinary( RepositoryId repositoryId, String blobKey );

    DumpMeta getDumpMeta();
}
