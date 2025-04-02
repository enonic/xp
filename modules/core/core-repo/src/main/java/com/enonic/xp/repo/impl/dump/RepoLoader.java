package com.enonic.xp.repo.impl.dump;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.dump.BranchLoadResult;
import com.enonic.xp.dump.RepoLoadResult;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repo.impl.dump.reader.BranchEntryProcessor;
import com.enonic.xp.repo.impl.dump.reader.CommitEntryProcessor;
import com.enonic.xp.repo.impl.dump.reader.DumpReader;
import com.enonic.xp.repo.impl.dump.reader.VersionEntryProcessor;
import com.enonic.xp.repo.impl.repository.RepositoryEntry;
import com.enonic.xp.repo.impl.repository.RepositoryEntryService;
import com.enonic.xp.repository.RepositoryConstants;
import com.enonic.xp.repository.RepositoryId;

class RepoLoader
{
    private final RepositoryId repositoryId;

    private final RepositoryEntryService repositoryEntryService;

    private final NodeService nodeService;

    private final DumpReader reader;

    private final boolean includeVersions;

    private final BranchEntryProcessor branchEntryProcessor;

    private final VersionEntryProcessor versionEntryProcessor;

    private final CommitEntryProcessor commitEntryProcessor;

    private RepoLoader( final Builder builder )
    {
        repositoryId = builder.repositoryId;
        repositoryEntryService = builder.repositoryEntryService;
        nodeService = builder.nodeService;
        reader = builder.reader;
        this.includeVersions = builder.includeVersions;
        this.branchEntryProcessor = BranchEntryProcessor.create().
            dumpReader( this.reader ).
            nodeService( this.nodeService ).
            blobStore( builder.blobStore ).
            repositoryId( repositoryId ).
            build();
        this.versionEntryProcessor = VersionEntryProcessor.create().
            dumpReader( this.reader ).
            nodeService( this.nodeService ).
            blobStore( builder.blobStore ).
            repositoryId( repositoryId ).
            build();
        this.commitEntryProcessor = CommitEntryProcessor.create().
            dumpReader( this.reader ).
            nodeService( this.nodeService ).
            blobStore( builder.blobStore ).
            repositoryId( repositoryId ).
            build();
    }

    public RepoLoadResult execute()
    {
        final RepoLoadResult.Builder loadResult = RepoLoadResult.create( this.repositoryId );

        getBranches().forEach( branch -> setContext( branch ).runWith( () -> doExecute( loadResult ) ) );

        if ( this.includeVersions )
        {
            ContextBuilder.from( ContextAccessor.current() ).
                repositoryId( this.repositoryId ).
                branch( RepositoryConstants.MASTER_BRANCH ).
                build().runWith( () -> loadVersions( loadResult ) );
        }

        ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( this.repositoryId ).
            branch( RepositoryConstants.MASTER_BRANCH ).
            build().runWith( () -> loadCommits( loadResult ) );

        return loadResult.build();
    }

    private Branches getBranches()
    {
        return this.reader.getBranches( this.repositoryId );
    }

    private Context setContext( final Branch branch )
    {
        return ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( this.repositoryId ).
            branch( branch ).
            build();
    }

    private void doExecute( final RepoLoadResult.Builder result )
    {
        final Branch currentBranch = ContextAccessor.current().getBranch();
        verifyOrCreateBranch( currentBranch );
        final BranchLoadResult branchLoadResult = this.reader.loadBranch( repositoryId, currentBranch, this.branchEntryProcessor );
        result.add( branchLoadResult );
    }

    private void loadVersions( final RepoLoadResult.Builder result )
    {
        result.versions( this.reader.loadVersions( repositoryId, this.versionEntryProcessor ) );
    }

    private void loadCommits( final RepoLoadResult.Builder result )
    {
        result.commits( this.reader.loadCommits( repositoryId, this.commitEntryProcessor ) );
    }

    private void verifyOrCreateBranch( final Branch branch )
    {
        final RepositoryEntry currentRepo = this.repositoryEntryService.getRepositoryEntry( this.repositoryId );

        if ( currentRepo.getBranches().contains( branch ) )
        {
            return;
        }

        this.repositoryEntryService.addBranchToRepositoryEntry( repositoryId, branch );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private RepositoryId repositoryId;

        private RepositoryEntryService repositoryEntryService;

        private NodeService nodeService;

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

        public Builder repositoryEntryService( final RepositoryEntryService val )
        {
            repositoryEntryService = val;
            return this;
        }

        public Builder nodeService( final NodeService val )
        {
            nodeService = val;
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
