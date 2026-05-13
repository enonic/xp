package com.enonic.xp.repo.impl.dump;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.dump.RepoLoadResult;
import com.enonic.xp.repo.impl.dump.reader.CommitEntryProcessor;
import com.enonic.xp.repo.impl.dump.reader.DumpReader;
import com.enonic.xp.repo.impl.dump.reader.NodeLoader;
import com.enonic.xp.repo.impl.dump.reader.VersionEntryProcessor;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;

class RepoLoader
{
    private final RepositoryId repositoryId;

    private final DumpReader reader;

    private final NodeLoader nodeLoader;

    private final BlobStore blobStore;

    private final boolean includeVersions;

    private RepoLoader( final Builder builder )
    {
        repositoryId = builder.repositoryId;
        reader = builder.reader;
        nodeLoader = builder.nodeLoader;
        blobStore = builder.blobStore;
        includeVersions = builder.includeVersions;
    }

    public RepoLoadResult execute()
    {
        final RepoLoadResult.Builder loadResult = RepoLoadResult.create( this.repositoryId );

        final VersionEntryProcessor versionEntryProcessor = VersionEntryProcessor.create()
            .dumpReader( this.reader )
            .nodeLoader( this.nodeLoader )
            .blobStore( this.blobStore )
            .repositoryId( repositoryId )
            .includeVersions( this.includeVersions )
            .build();
        final CommitEntryProcessor commitEntryProcessor = CommitEntryProcessor.create()
            .dumpReader( this.reader )
            .nodeLoader( this.nodeLoader )
            .blobStore( this.blobStore )
            .repositoryId( repositoryId )
            .build();

        ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( this.repositoryId )
            .branch( RepositoryConstants.MASTER_BRANCH )
            .build()
            .runWith( () -> {
                loadResult.versions( this.reader.loadVersions( repositoryId, versionEntryProcessor ) );
                versionEntryProcessor.getBranchLoadResults().forEach( ( _, result ) -> loadResult.add( result ) );
                loadResult.commits( this.reader.loadCommits( repositoryId, commitEntryProcessor ) );
            } );

        return loadResult.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private RepositoryId repositoryId;

        private NodeLoader nodeLoader;

        private boolean includeVersions = false;

        private DumpReader reader;

        private BlobStore blobStore;

        private Builder()
        {
        }

        public Builder repositoryId( final RepositoryId val )
        {
            repositoryId = val;
            return this;
        }

        public Builder nodeLoader( final NodeLoader val )
        {
            nodeLoader = val;
            return this;
        }

        public Builder blobStore( final BlobStore val )
        {
            blobStore = val;
            return this;
        }

        public Builder includeVersions( final boolean val )
        {
            includeVersions = val;
            return this;
        }

        public Builder reader( final DumpReader val )
        {
            reader = val;
            return this;
        }

        public RepoLoader build()
        {
            return new RepoLoader( this );
        }
    }
}
