package com.enonic.xp.core.dump;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.repo.impl.dump.model.BranchDumpEntry;
import com.enonic.xp.repo.impl.dump.model.CommitDumpEntry;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.model.VersionsDumpEntry;
import com.enonic.xp.repo.impl.dump.writer.DumpWriter;
import com.enonic.xp.repository.RepositoryId;

class TestDumpWriter
    implements DumpWriter
{
    private final ListMultimap<RepoBranchEntry, BranchDumpEntry> entries;

    private RepoBranchEntry current;

    private final Set<BlobKey> binaries = new HashSet<>();

    private final Set<NodeVersionKey> nodeVersionKeys = new HashSet<>();

    private final AtomicLong commitCount = new AtomicLong();

    private final AtomicLong versionCount = new AtomicLong();

    private DumpMeta dumpMeta;

    public TestDumpWriter()
    {
        this.entries = ArrayListMultimap.create();
    }

    @Override
    public void writeDumpMetaData( final DumpMeta dumpMeta )
    {
        this.dumpMeta = dumpMeta;
    }

    @Override
    public void openBranchMeta( final RepositoryId repositoryId, final Branch branch )
    {
        current = new RepoBranchEntry( repositoryId, branch );
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
        current = null;
    }

    @Override
    public void close()
    {
        current = null;
    }

    @Override
    public void writeBranchEntry( final BranchDumpEntry branchDumpEntry )
    {
        entries.put( current, branchDumpEntry );
    }

    @Override
    public void writeVersionsEntry( final VersionsDumpEntry versionsDumpEntry )
    {
        versionCount.incrementAndGet();
    }

    @Override
    public void writeCommitEntry( final CommitDumpEntry commitDumpEntry )
    {
        commitCount.incrementAndGet();
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

    public List<BranchDumpEntry> get( final RepositoryId repoId, final Branch branch )
    {
        return this.entries.get( new RepoBranchEntry( repoId, branch ) );
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

    static final class RepoBranchEntry
    {
        private final RepositoryId repositoryId;

        private final Branch branch;

        public RepoBranchEntry( final RepositoryId repositoryId, final Branch branch )
        {
            this.repositoryId = repositoryId;
            this.branch = branch;
        }

        @Override
        public boolean equals( final Object o )
        {
            if ( this == o )
            {
                return true;
            }
            if ( o == null || getClass() != o.getClass() )
            {
                return false;
            }

            final RepoBranchEntry that = (RepoBranchEntry) o;

            if ( repositoryId != null ? !repositoryId.equals( that.repositoryId ) : that.repositoryId != null )
            {
                return false;
            }
            return branch != null ? branch.equals( that.branch ) : that.branch == null;

        }

        @Override
        public int hashCode()
        {
            int result = repositoryId != null ? repositoryId.hashCode() : 0;
            result = 31 * result + ( branch != null ? branch.hashCode() : 0 );
            return result;
        }
    }
}

