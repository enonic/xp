package com.enonic.xp.repo.impl.dump;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.dump.DumpResult;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repo.impl.dump.reader.DumpLineProcessor;
import com.enonic.xp.repo.impl.dump.reader.DumpReader;
import com.enonic.xp.repository.CreateBranchParams;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;

class RepoLoader
{
    private final static Logger LOG = LoggerFactory.getLogger( RepoLoader.class );

    private final RepositoryId repositoryId;

    private final RepositoryService repositoryService;

    private final NodeService nodeService;

    private final DumpReader reader;

    private final DumpLineProcessor processor;

    private RepoLoader( final Builder builder )
    {
        repositoryId = builder.repositoryId;
        repositoryService = builder.repositoryService;
        nodeService = builder.nodeService;
        reader = builder.reader;
        this.processor = DumpLineProcessor.create().
            dumpReader( this.reader ).
            nodeService( this.nodeService ).
            includeVersions( builder.includeVersions ).
            build();
    }

    public DumpResult execute()
    {
        getBranches().forEach( ( branch ) -> {
            setContext( branch ).runWith( this::doExecute );
        } );

        return null;
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

    private void doExecute()
    {
        final Branch branch = ContextAccessor.current().getBranch();

        getOrCreateRepositoryAndBranch( branch );

        this.reader.load( repositoryId, branch, this.processor );
    }

    private void getOrCreateRepositoryAndBranch( final Branch branch )
    {
        Repository currentRepo = this.repositoryService.get( this.repositoryId );

        if ( currentRepo == null )
        {
            currentRepo = createRepository( this.repositoryId );
        }

        if ( !currentRepo.getBranches().contains( branch ) )
        {
            LOG.info( String.format( "Creating branch [%s] from dump", branch ) );
            this.repositoryService.createBranch( CreateBranchParams.from( branch.toString() ) );
        }
    }

    private Repository createRepository( final RepositoryId repositoryId )
    {
        // TODO: Root node properties (childOrder, indexConfig etc)
        return this.repositoryService.createRepository( CreateRepositoryParams.create().
            repositoryId( repositoryId ).
            build() );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private RepositoryId repositoryId;

        private RepositoryService repositoryService;

        private NodeService nodeService;

        private boolean includeVersions = false;

        private DumpReader reader;

        private Builder()
        {
        }

        public Builder repositoryId( final RepositoryId val )
        {
            repositoryId = val;
            return this;
        }

        public Builder repositoryService( final RepositoryService val )
        {
            repositoryService = val;
            return this;
        }

        public Builder nodeService( final NodeService val )
        {
            nodeService = val;
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
