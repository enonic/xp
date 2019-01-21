package com.enonic.xp.repo.impl.dump.reader;

import com.google.common.io.ByteSource;
import com.google.common.io.LineProcessor;

import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.dump.BranchLoadResult;
import com.enonic.xp.dump.VersionsLoadResult;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;

public interface DumpReader
{
    RepositoryIds getRepositories();

    Branches getBranches( final RepositoryId repositoryId );

    BranchLoadResult loadBranch( final RepositoryId repositoryId, final Branch branch, final LineProcessor<EntryLoadResult> processor );

    VersionsLoadResult loadVersions( final RepositoryId repositoryId, final LineProcessor<EntryLoadResult> processor );

    NodeVersion get( final RepositoryId repositoryId, final NodeVersionKey blobKey );

    ByteSource getBinary( final RepositoryId repositoryId, final String blobKey );

    DumpMeta getDumpMeta();

}
