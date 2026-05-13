package com.enonic.xp.repo.impl.dump.writer;

import java.io.Closeable;
import java.util.Collection;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.repo.impl.dump.model.CommitDumpEntry;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.model.VersionMeta;
import com.enonic.xp.repository.RepositoryId;

public interface DumpWriter
    extends Closeable
{
    void writeDumpMetaData( DumpMeta dumpMeta );

    void openVersionsMeta( RepositoryId repositoryId );

    void openCommitsMeta( RepositoryId repositoryId );

    void closeMeta();

    /**
     * Open a JSONL stream for one node's versions; close the returned stream to flush the file.
     * Each call to {@link VersionsStream#append} writes one line, optionally tagged with the
     * branches in which the version is the active head.
     */
    VersionsStream openVersions( NodeId nodeId );

    interface VersionsStream
        extends Closeable
    {
        void append( VersionMeta version, Collection<Branch> branches );

        @Override
        void close();
    }

    void writeCommitEntry( CommitDumpEntry commitDumpEntry );

    void writeRawEntry( String entryName, byte[] data );

    void writeNodeVersionBlobs( RepositoryId repositoryId, NodeVersionKey nodeVersionKey );

    void writeBinaryBlob( RepositoryId repositoryId, BlobKey key );

    BlobKey addBlobRecord( Segment segment, ByteSource data );
}
