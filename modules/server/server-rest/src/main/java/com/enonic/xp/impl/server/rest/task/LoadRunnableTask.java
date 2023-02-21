package com.enonic.xp.impl.server.rest.task;

import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.base.Preconditions;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.core.internal.FileNames;
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
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.vfs.VirtualFiles;

public class LoadRunnableTask
    implements RunnableTask
{
    private final Path dumpsFolder = HomeDir.get().toPath().resolve( "data" ).resolve( "dump" );

    private final String name;

    private final boolean upgrade;

    private final boolean archive;

    private final TaskService taskService;

    private final DumpService dumpService;

    private final NodeRepositoryService nodeRepositoryService;

    private final RepositoryService repositoryService;

    private final ExportService exportService;

    private SystemLoadListener loadDumpListener;

    private LoadRunnableTask( Builder builder )
    {
        this.name = builder.name;
        this.upgrade = builder.upgrade;
        this.archive = builder.archive;
        this.taskService = builder.taskService;
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
        TaskUtils.checkAlreadySubmitted( taskService.getTaskInfo( id ), taskService.getAllTasks() );

        loadDumpListener = new SystemLoadListenerImpl( progressReporter );

        final Path dumpRoot = getDumpRoot( name );

        final SystemLoadResultJson result;
        if ( isExport( dumpRoot ) )
        {
            result = doLoadFromExport( dumpRoot );
        }
        else
        {
            result = doLoadFromSystemDump();
        }

        progressReporter.info( result.toString() );
    }

    private boolean isExport( final Path dumpRoot )
    {
        return Files.exists( dumpRoot.resolve( "export.properties" ) );
    }

    private SystemLoadResultJson doLoadFromExport( final Path rootDir )
    {
        final SystemLoadResult.Builder builder = SystemLoadResult.create();

        final Repositories repositories = repositoryService.list();

        final long branchesCount = repositories.stream().flatMap( repository -> repository.getBranches().stream() ).count();

        loadDumpListener.totalBranches( branchesCount );

        builder.add( importSystemRepo( rootDir ) );

        this.repositoryService.invalidateAll();

        for ( Repository repository : repositories )
        {
            initializeRepo( repository );
            builder.add( importRepoBranches( rootDir, repository ) );
        }

        return SystemLoadResultJson.from( builder.build() );
    }

    private Path getDumpRoot( final String dumpName )
    {
        Preconditions.checkArgument( FileNames.isSafeFileName( dumpName ) );

        return dumpsFolder.resolve( dumpName );
    }

    private RepoLoadResult importSystemRepo( final Path rootDir )
    {
        final RepoLoadResult.Builder builder = RepoLoadResult.create( SystemConstants.SYSTEM_REPO_ID );

        final NodeImportResult systemRepoImport =
            importRepoBranch( SystemConstants.SYSTEM_REPO_ID.toString(), SystemConstants.BRANCH_SYSTEM.toString(), rootDir );

        final BranchLoadResult branchLoadResult = NodeImportResultTranslator.translate( systemRepoImport, SystemConstants.BRANCH_SYSTEM );
        builder.add( branchLoadResult );
        return builder.build();
    }

    private void initializeRepo( final Repository repository )
    {
        if ( !this.nodeRepositoryService.isInitialized( repository.getId() ) )
        {
            final CreateRepositoryParams createRepositoryParams = CreateRepositoryParams.create()
                .repositoryId( repository.getId() )
                .repositorySettings( repository.getSettings() )
                .data( repository.getData() )
                .build();
            this.nodeRepositoryService.create( createRepositoryParams );
        }
    }

    private SystemLoadResultJson doLoadFromSystemDump()
    {
        final SystemLoadResult systemLoadResult = this.dumpService.load( SystemLoadParams.create()
                                                                             .dumpName( name )
                                                                             .upgrade( upgrade )
                                                                             .archive( archive )
                                                                             .includeVersions( true )
                                                                             .listener( loadDumpListener )
                                                                             .build() );

        return SystemLoadResultJson.from( systemLoadResult );
    }

    private NodeImportResult importRepoBranch( final String repoName, final String branch, final Path rootDir )
    {
        final Path importPath = rootDir.resolve( repoName ).resolve( branch );

        return getContext( branch, repoName ).callWith( () -> this.exportService.importNodes( ImportNodesParams.create()
                                                                                                  .source( VirtualFiles.from( importPath ) )
                                                                                                  .targetNodePath( NodePath.ROOT )
                                                                                                  .includeNodeIds( true )
                                                                                                  .includePermissions( true )
                                                                                                  .build() ) );
    }


    private boolean isSystemRepoMaster( final Repository repository, final Branch branch )
    {
        return SystemConstants.SYSTEM_REPO_ID.equals( repository.getId() ) && SystemConstants.BRANCH_SYSTEM.equals( branch );
    }


    private Context getContext( final String branchName, final String repositoryName )
    {
        return ContextBuilder.from( ContextAccessor.current() )
            .branch( Branch.from( branchName ) )
            .repositoryId( RepositoryId.from( repositoryName ) )
            .build();
    }

    private RepoLoadResult importRepoBranches( final Path rootDir, final Repository repository )
    {
        final RepoLoadResult.Builder builder = RepoLoadResult.create( repository.getId() );

        for ( Branch branch : repository.getBranches() )
        {
            if ( isSystemRepoMaster( repository, branch ) )
            {
                continue;
            }

            final NodeImportResult nodeImportResult = importRepoBranch( repository.getId().toString(), branch.getValue(), rootDir );
            builder.add( NodeImportResultTranslator.translate( nodeImportResult, branch ) );
        }

        return builder.build();
    }

    public static class Builder
    {
        private String name;

        private boolean upgrade;

        private boolean archive;

        private DumpService dumpService;

        private NodeRepositoryService nodeRepositoryService;

        private RepositoryService repositoryService;

        private ExportService exportService;

        private TaskService taskService;

        public Builder taskService( final TaskService taskService )
        {
            this.taskService = taskService;
            return this;
        }

        public Builder name( String name )
        {
            this.name = name;
            return this;
        }

        public Builder upgrade( boolean upgrade )
        {
            this.upgrade = upgrade;
            return this;
        }

        public Builder archive( boolean archive )
        {
            this.archive = archive;
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
