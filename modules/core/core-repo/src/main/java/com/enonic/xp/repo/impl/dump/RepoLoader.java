package com.enonic.xp.repo.impl.dump;

import java.nio.file.Path;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repo.impl.dump.reader.DumpLineProcessor;
import com.enonic.xp.repo.impl.dump.reader.DumpReader;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;

public class RepoLoader
{
    private final static String LINE_SEPARATOR = System.getProperty( "line.separator" );

    private final RepositoryId repositoryId;

    private final RepositoryService repositoryService;

    private final NodeService nodeService;

    private final Branches branches;

    private final boolean includeVersions;

    private final boolean includeBinaries;

    private final Path sourcePath;

    private final DumpReader reader;

    private final DumpLineProcessor processor;

    private RepoLoader( final Builder builder )
    {
        repositoryId = builder.repositoryId;
        repositoryService = builder.repositoryService;
        nodeService = builder.nodeService;
        branches = builder.branches;
        includeVersions = builder.includeVersions;
        includeBinaries = builder.includeBinaries;
        sourcePath = builder.sourcePath;
        reader = builder.reader;
        this.processor = new DumpLineProcessor( this.nodeService, this.reader );
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
        final Repository repository = this.repositoryService.get( this.repositoryId );

        if ( repository == null )
        {
            throw new RepoDumpException( String.format( "Repository [%s] not found", this.repositoryId ) );
        }

        return repository.getBranches();
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
        this.reader.load( repositoryId, ContextAccessor.current().getBranch(), this.processor );
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

        private Branches branches;

        private boolean includeVersions;

        private boolean includeBinaries;

        private Path sourcePath;

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

        public Builder branches( final Branches val )
        {
            branches = val;
            return this;
        }

        public Builder includeVersions( final boolean val )
        {
            includeVersions = val;
            return this;
        }

        public Builder includeBinaries( final boolean val )
        {
            includeBinaries = val;
            return this;
        }

        public Builder sourcePath( final Path val )
        {
            sourcePath = val;
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
