package com.enonic.xp.core.impl.dump;

import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.io.ByteSource;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.core.impl.dump.model.DumpEntry;
import com.enonic.xp.core.impl.dump.writer.DumpWriter;
import com.enonic.xp.repository.RepositoryId;

class TestDumpWriter
    implements DumpWriter
{
    private final ListMultimap<RepoBranchEntry, DumpEntry> entries;

    private RepoBranchEntry current;


    public TestDumpWriter()
    {
        this.entries = ArrayListMultimap.create();
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
    public void write( final DumpEntry dumpEntry )
    {
        entries.put( current, dumpEntry );
    }

    @Override
    public void writeVersion( final BlobKey blobKey, final ByteSource source )
    {

    }

    @Override
    public void writeBinary( final BlobKey blobKey, final ByteSource source )
    {

    }

    public List<DumpEntry> get( final RepositoryId repoId, final Branch branch )
    {
        return this.entries.get( new RepoBranchEntry( repoId, branch ) );
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
