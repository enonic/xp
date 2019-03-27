package com.enonic.xp.impl.server.rest.task;

import java.nio.file.Paths;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.export.ExportNodesParams;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.home.HomeDir;
import com.enonic.xp.impl.server.rest.model.ExportNodesRequestJson;
import com.enonic.xp.impl.server.rest.model.NodeExportResultJson;
import com.enonic.xp.impl.server.rest.model.RepoPath;
import com.enonic.xp.impl.server.rest.task.listener.ExportListenerImpl;
import com.enonic.xp.task.AbstractRunnableTask;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;

public class ExportRunnableTask
    extends AbstractRunnableTask
{
    private final ExportNodesRequestJson params;

    private final ExportService exportService;

    private ExportRunnableTask( Builder builder )
    {
        super( builder );
        this.params = builder.params;
        this.exportService = builder.exportService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final NodeExportResult result =
            getContext( params.getSourceRepoPath() ).callWith( () -> this.exportService.exportNodes( ExportNodesParams.create().
                sourceNodePath( params.getSourceRepoPath().getNodePath() ).
                targetDirectory( getExportDirectory( params.getExportName() ).toString() ).
                dryRun( params.isDryRun() ).
                includeNodeIds( params.isExportWithIds() ).
                includeVersions( params.isIncludeVersions() ).
                nodeExportListener( new ExportListenerImpl( progressReporter ) ).
                build() ) );

        progressReporter.info( NodeExportResultJson.from( result ).toString() );
    }

    private Context getContext( final RepoPath repoPath )
    {
        return ContextBuilder.from( ContextAccessor.current() ).
            branch( repoPath.getBranch() ).
            repositoryId( repoPath.getRepositoryId() ).
            build();
    }

    private java.nio.file.Path getExportDirectory( final String exportName )
    {
        return Paths.get( HomeDir.get().toString(), "data", "export", exportName ).toAbsolutePath();
    }

    public static class Builder
        extends AbstractRunnableTask.Builder<Builder>
    {
        private ExportNodesRequestJson params;

        private ExportService exportService;

        public Builder params( ExportNodesRequestJson params )
        {
            this.params = params;
            return this;
        }

        public Builder exportService( final ExportService exportService )
        {
            this.exportService = exportService;
            return this;
        }

        public ExportRunnableTask build()
        {
            return new ExportRunnableTask( this );
        }
    }
}
