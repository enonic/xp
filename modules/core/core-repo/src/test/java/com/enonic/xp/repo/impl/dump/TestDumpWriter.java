package com.enonic.xp.repo.impl.dump;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Sets;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.repo.impl.dump.model.DumpEntry;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.writer.DumpWriter;
import com.enonic.xp.repository.RepositoryId;

class TestDumpWriter
    implements DumpWriter
{
    private final ListMultimap<RepoBranchEntry, DumpEntry> entries;

    private RepoBranchEntry current;

    private final Set<String> binaries = Sets.newHashSet();

    private final Set<NodeVersionId> nodeVersions = Sets.newHashSet();

    private DumpMeta dumpMeta;

    public TestDumpWriter()
    {
        this.entries = ArrayListMultimap.create();
    }

    @Override
    public void writeDumpMeta( final DumpMeta dumpMeta )
    {
        this.dumpMeta = dumpMeta;
    }

    @Override
    public void open( final RepositoryId repositoryId, final Branch branch )
    {
        current = new RepoBranchEntry( repositoryId, branch );
    }

    @Override
    public void close()
    {
        current = null;
    }

    @Override
    public void writeMetaData( final DumpEntry dumpEntry )
    {
        entries.put( current, dumpEntry );
    }

    @Override
    public void writeVersion( final NodeVersionId nodeVersionId )
    {
        nodeVersions.add( nodeVersionId );
    }

    @Override
    public void writeBinary( final String key )
    {
        binaries.add( key );
    }

    public List<DumpEntry> get( final RepositoryId repoId, final Branch branch )
    {
        return this.entries.get( new RepoBranchEntry( repoId, branch ) );
    }

    public Set<String> getBinaries()
    {
        return binaries;
    }

    public DumpMeta getDumpMeta()
    {
        return dumpMeta;
    }

    public boolean hasVersions( final NodeVersionId... versions )
    {
        return this.nodeVersions.containsAll( Arrays.asList( versions ) );
    }

    public Set<NodeVersionId> getNodeVersions()
    {
        return nodeVersions;
    }

    class RepoBranchEntry
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

