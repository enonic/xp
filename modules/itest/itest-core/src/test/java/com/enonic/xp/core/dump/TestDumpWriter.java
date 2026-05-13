package com.enonic.xp.core.dump;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.repo.impl.dump.model.CommitDumpEntry;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.model.VersionMeta;
import com.enonic.xp.repo.impl.dump.writer.DumpWriter;
import com.enonic.xp.repository.RepositoryId;

class TestDumpWriter
    implements DumpWriter
{
    private final Set<BlobKey> binaries = new HashSet<>();

    private final Set<NodeVersionKey> nodeVersionKeys = new HashSet<>();

    private final AtomicLong commitCount = new AtomicLong();

    private final AtomicLong versionCount = new AtomicLong();

    private DumpMeta dumpMeta;

    @Override
    public void writeDumpMetaData( final DumpMeta dumpMeta )
    {
        this.dumpMeta = dumpMeta;
    }

    @Override
    public void openVersionsMeta( final RepositoryId repositoryId )
    {
        // Do nothing yet
    }

    @Override
    public void openCommitsMeta( final RepositoryId repositoryId )
    {
        // Do nothing yet
    }

    @Override
    public void closeMeta()
    {
    }

    @Override
    public void close()
    {
    }

    @Override
    public VersionsStream openVersions( final NodeId nodeId )
    {
        return new VersionsStream()
        {
            @Override
            public void append( final VersionMeta version, final Collection<Branch> branches )
            {
                versionCount.incrementAndGet();
            }

            @Override
            public void close()
            {
            }
        };
    }

    @Override
    public void writeCommitEntry( final CommitDumpEntry commitDumpEntry )
    {
        commitCount.incrementAndGet();
    }

    @Override
    public void writeRawEntry( final String entryName, final byte[] data )
    {
        // Do nothing
    }

    @Override
    public BlobKey addBlobRecord( final Segment segment, final ByteSource data )
    {
        return BlobKey.sha256( data );
    }

    @Override
    public void writeNodeVersionBlobs( final RepositoryId repositoryId, final NodeVersionKey nodeVersionKey )
    {
        nodeVersionKeys.add( nodeVersionKey );
    }

    @Override
    public void writeBinaryBlob( final RepositoryId repositoryId, final BlobKey key )
    {
        binaries.add( key );
    }

    public Set<BlobKey> getBinaries()
    {
        return binaries;
    }

    public DumpMeta getDumpMeta()
    {
        return dumpMeta;
    }

    public boolean hasNodeVersions( final NodeVersionKey... nodeVersionKeys )
    {
        return this.nodeVersionKeys.containsAll( Arrays.asList( nodeVersionKeys ) );
    }

    public Set<NodeVersionKey> getNodeVersionKeys()
    {
        return nodeVersionKeys;
    }

    public long getCommitCount()
    {
        return commitCount.get();
    }

    public long getVersionCount()
    {
        return versionCount.get();
    }
}
