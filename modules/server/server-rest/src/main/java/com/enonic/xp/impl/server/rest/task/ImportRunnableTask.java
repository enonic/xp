package com.enonic.xp.impl.server.rest.task;

import java.nio.file.Paths;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.home.HomeDir;
import com.enonic.xp.impl.server.rest.model.ImportNodesRequestJson;
import com.enonic.xp.impl.server.rest.model.NodeImportResultJson;
import com.enonic.xp.impl.server.rest.model.RepoPath;
import com.enonic.xp.impl.server.rest.task.listener.ImportListenerImpl;
import com.enonic.xp.repository.CreateRepositoryParams;
import com.enonic.xp.repository.NodeRepositoryService;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.task.AbstractRunnableTask;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.vfs.VirtualFile;
import com.enonic.xp.vfs.VirtualFiles;

public class ImportRunnableTask
    extends AbstractRunnableTask
{
    private final ImportNodesRequestJson params;

    private final ExportService exportService;

    private final RepositoryService repositoryService;

    private final NodeRepositoryService nodeRepositoryService;

    private ImportRunnableTask( Builder builder )
    {
        super( builder );
        this.params = builder.params;
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
        final String xsl = params.getXslSource();
        final VirtualFile xsltFile = xsl != null ? VirtualFiles.from( getExportDirectory( xsl ) ) : null;
        final RepoPath targetRepoPath = params.getTargetRepoPath();

        final NodeImportResult result =
            getContext( params.getTargetRepoPath() ).callWith( () -> this.exportService.importNodes( ImportNodesParams.create().
                source( VirtualFiles.from( getExportDirectory( params.getExportName() ) ) ).
                targetNodePath( targetRepoPath.getNodePath() ).
                dryRun( params.isDryRun() ).
                includeNodeIds( params.isImportWithIds() ).
                includePermissions( params.isImportWithPermissions() ).
                xslt( xsltFile ).
                xsltParams( params.getXslParams() ).
                nodeImportListener( new ImportListenerImpl( progressReporter ) ).
                build() ) );

        if ( targetIsSystemRepo( targetRepoPath ) )
        {
            initializeStoredRepositories();
        }

        progressReporter.info( NodeImportResultJson.from( result ).toString() );
    }

    private Context getContext( final RepoPath repoPath )
    {
        return ContextBuilder.from( ContextAccessor.current() ).
            branch( repoPath.getBranch() ).
            repositoryId( repoPath.getRepositoryId() ).
            build();
    }

    private boolean targetIsSystemRepo( final RepoPath targetRepoPath )
    {
        return SystemConstants.SYSTEM_REPO.getId().equals( targetRepoPath.getRepositoryId() ) &&
            SystemConstants.BRANCH_SYSTEM.equals( targetRepoPath.getBranch() );
    }

    private void initializeStoredRepositories()
    {
        this.repositoryService.invalidateAll();
        for ( Repository repository : repositoryService.list() )
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
    }

    private java.nio.file.Path getExportDirectory( final String exportName )
    {
        return Paths.get( HomeDir.get().toString(), "data", "export", exportName ).toAbsolutePath();
    }

    public static class Builder
        extends AbstractRunnableTask.Builder<Builder>
    {
        private ImportNodesRequestJson params;

        private ExportService exportService;

        private RepositoryService repositoryService;

        private NodeRepositoryService nodeRepositoryService;

        public Builder params( ImportNodesRequestJson params )
        {
            this.params = params;
            return this;
        }

        public Builder exportService( final ExportService exportService )
        {
            this.exportService = exportService;
            return this;
        }

        public Builder repositoryService( final RepositoryService repositoryService )
        {
            this.repositoryService = repositoryService;
            return this;
        }

        public Builder nodeRepositoryService( final NodeRepositoryService nodeRepositoryService )
        {
            this.nodeRepositoryService = nodeRepositoryService;
            return this;
        }

        public ImportRunnableTask build()
        {
            return new ImportRunnableTask( this );
        }
    }
}
