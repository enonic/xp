package com.enonic.xp.repo.impl.dump.reader;

import java.io.Closeable;
import java.util.List;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.dump.CommitsLoadResult;
import com.enonic.xp.dump.VersionsLoadResult;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.repository.RepositoryEntry;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;

public interface DumpReader
    extends Closeable
{
    RepositoryIds getRepositories();

    VersionsLoadResult loadVersions( RepositoryId repositoryId, EntryProcessor processor );

    CommitsLoadResult loadCommits( RepositoryId repositoryId, EntryProcessor processor );

    NodeStoreVersion get( RepositoryId repositoryId, NodeVersionKey nodeVersionKey );

    ByteSource getBinary( RepositoryId repositoryId, BlobKey blobKey );

    DumpMeta getDumpMeta();

    List<RepositoryEntry> getRepositoryEntries( RepositoryIds repositoryIds );
}
