package com.enonic.xp.repo.impl.dump.reader;

import java.io.IOException;
import java.util.Optional;

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.repo.impl.NodeStoreVersion;
import com.enonic.xp.repo.impl.dump.PathRef;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;

public interface DumpReaderV7
    extends AutoCloseable, BlobStoreAccess
{
    RepositoryIds getRepositories();

    Branches getBranches( RepositoryId repositoryId );

    NodeStoreVersion get( RepositoryId repositoryId, NodeVersionKey nodeVersionKey );

    DumpMeta getDumpMeta();

    BlobRecord getRecord( Segment segment, BlobKey key );

    Optional<PathRef> getBranchEntries( RepositoryId repositoryId, Branch branch );

    Optional<PathRef> getVersions( RepositoryId repositoryId );

    Optional<PathRef> getCommits( RepositoryId repositoryId );

    TarArchiveInputStream openTarStream( PathRef metaFile )
        throws IOException;

    @Override
    void close()
        throws IOException;
}
