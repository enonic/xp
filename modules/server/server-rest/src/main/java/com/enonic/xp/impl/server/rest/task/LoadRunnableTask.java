package com.enonic.xp.impl.server.rest.task;

import java.nio.file.Paths;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.dump.BranchLoadResult;
import com.enonic.xp.dump.DumpService;
import com.enonic.xp.dump.RepoLoadResult;
import com.enonic.xp.dump.SystemLoadListener;
import com.enonic.xp.dump.SystemLoadParams;
import com.enonic.xp.dump.SystemLoadResult;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.home.HomeDir;
import com.enonic.xp.impl.server.rest.NodeImportResultTranslator;
import com.enonic.xp.impl.server.rest.model.SystemLoadRequestJson;
import com.enonic.xp.impl.server.rest.model.SystemLoadResultJson;
import com.enonic.xp.impl.server.rest.task.listener.SystemLoadListenerImpl;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.Repositories;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.task.AbstractRunnableTask;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.vfs.VirtualFiles;

public class LoadRunnableTask
    extends AbstractRunnableTask
{
    private final SystemLoadRequestJson params;

    private final DumpService dumpService;

    private final NodeRepositoryService nodeRepositoryService;

    private final RepositoryService repositoryService;

    private final ExportService exportService;

    private SystemLoadListener loadDumpListener;

    private LoadRunnableTask( Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.dumpService = builder.dumpService;
        this.exportService = builder.exportService;
        this.repositoryService = builder.repositoryService;
        this.nodeRepositoryService = builder.nodeRepositoryService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        SystemLoadResultJson result;
        loadDumpListener = new SystemLoadListenerImpl( progressReporter );

        if ( isExport( params ) )
        {
            result = doLoadFromExport( params );
        }
        else
        {
            result = doLoadFromSystemDump( params );
        }

        progressReporter.info( result.toString() );
    }

    private boolean isExport( final SystemLoadRequestJson request )
    {
        final java.nio.file.Path rootDir = getDumpRoot( request.getName() );

        final java.nio.file.Path exportProperties = Paths.get( rootDir.toString(), "export.properties" );

        return exportProperties.toFile().exists();
    }

    private SystemLoadResultJson doLoadFromExport( final SystemLoadRequestJson request )
    {
        final SystemLoadResult.Builder builder = SystemLoadResult.create();

        final Repositories repositories = repositoryService.list();

        final long branchesCount = repositories.
            stream().
            flatMap( repository -> repository.getBranches().stream() ).
            count() + SystemConstants.SYSTEM_REPO.getBranches().getSize();

        loadDumpListener.totalBranches( branchesCount );

        builder.add( importSystemRepo( request ) );

        this.repositoryService.invalidateAll();

        for ( Repository repository : repositories )
        {
            initializeRepo( repository );
            builder.add( importRepoBranches( request.getName(), repository ) );
        }

        return SystemLoadResultJson.from( builder.build() );
    }

    private java.nio.file.Path getDumpRoot( final String dumpName )
    {
        final java.nio.file.Path rootDir = getDumpDirectory( dumpName );

        if ( !rootDir.toFile().exists() )
        {
            throw new IllegalArgumentException( "No dump with name '" + dumpName + "' found in " + getDataHome() );
        }
        return rootDir;
    }

    private RepoLoadResult importSystemRepo( final SystemLoadRequestJson request )
    {
        final RepoLoadResult.Builder builder = RepoLoadResult.create( SystemConstants.SYSTEM_REPO.getId() );

        final NodeImportResult systemRepoImport =
            importRepoBranch( SystemConstants.SYSTEM_REPO.getId().toString(), SystemConstants.BRANCH_SYSTEM.toString(), request.getName() );

        final BranchLoadResult branchLoadResult = NodeImportResultTranslator.translate( systemRepoImport, SystemConstants.BRANCH_SYSTEM );
        builder.add( branchLoadResult );
        return builder.build();
    }

    private void initializeRepo( final Repository repository )
    {
        if ( !this.nodeRepositoryService.isInitialized( repository.getId() ) )
        {
            final CreateRepositoryParams createRepositoryParams = CreateRepositoryParams.create().
                repositoryId( repository.getId() ).
                repositorySettings( repository.getSettings() ).
                build();
            this.nodeRepositoryService.create( createRepositoryParams );
        }
    }

    private SystemLoadResultJson doLoadFromSystemDump( final SystemLoadRequestJson request )
    {
        final SystemLoadResult systemLoadResult = this.dumpService.load( SystemLoadParams.create().
            dumpName( request.getName() ).
            upgrade( request.isUpgrade() ).
            includeVersions( true ).
            listener( loadDumpListener ).
            build() );

        return SystemLoadResultJson.from( systemLoadResult );
    }

    private NodeImportResult importRepoBranch( final String repoName, final String branch, final String dumpName )
    {
        final java.nio.file.Path rootDir = getDumpRoot( dumpName );

        final java.nio.file.Path importPath = rootDir.resolve( repoName ).resolve( branch );

        return getContext( branch, repoName ).callWith( () -> this.exportService.importNodes( ImportNodesParams.create().
            source( VirtualFiles.from( importPath ) ).
            targetNodePath( NodePath.ROOT ).
            includeNodeIds( true ).
            includePermissions( true ).
            build() ) );
    }

    private java.nio.file.Path getDumpDirectory( final String name )
    {
        return Paths.get( HomeDir.get().toString(), "data", "dump", name ).toAbsolutePath();
    }

    private java.nio.file.Path getDataHome()
    {
        return Paths.get( HomeDir.get().toString(), "data" );
    }

    private boolean isSystemRepoMaster( final Repository repository, final Branch branch )
    {
        return SystemConstants.SYSTEM_REPO.equals( repository ) && SystemConstants.BRANCH_SYSTEM.equals( branch );
    }


    private Context getContext( final String branchName, final String repositoryName )
    {
        return ContextBuilder.from( ContextAccessor.current() ).
            branch( Branch.from( branchName ) ).
            repositoryId( RepositoryId.from( repositoryName ) ).
            build();
    }

    private RepoLoadResult importRepoBranches( final String dumpName, final Repository repository )
    {
        final RepoLoadResult.Builder builder = RepoLoadResult.create( repository.getId() );

        for ( Branch branch : repository.getBranches() )
        {
            if ( isSystemRepoMaster( repository, branch ) )
            {
                continue;
            }

            final NodeImportResult nodeImportResult = importRepoBranch( repository.getId().toString(), branch.getValue(), dumpName );
            builder.add( NodeImportResultTranslator.translate( nodeImportResult, branch ) );
        }

        return builder.build();
    }


    public static class Builder
        extends AbstractRunnableTask.Builder<Builder>
    {
        private SystemLoadRequestJson params;

        private DumpService dumpService;

        private NodeRepositoryService nodeRepositoryService;

        private RepositoryService repositoryService;

        private ExportService exportService;

        public Builder params( SystemLoadRequestJson params )
        {
            this.params = params;
            return this;
        }

        public Builder dumpService( final DumpService dumpService )
        {
            this.dumpService = dumpService;
            return this;
        }

        public Builder nodeRepositoryService( final NodeRepositoryService nodeRepositoryService )
        {
            this.nodeRepositoryService = nodeRepositoryService;
            return this;
        }

        public Builder repositoryService( final RepositoryService repositoryService )
        {
            this.repositoryService = repositoryService;
            return this;
        }

        public Builder exportService( final ExportService exportService )
        {
            this.exportService = exportService;
            return this;
        }

        public LoadRunnableTask build()
        {
            return new LoadRunnableTask( this );
        }
    }
}
