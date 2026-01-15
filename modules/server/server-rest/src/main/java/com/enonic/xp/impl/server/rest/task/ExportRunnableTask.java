package com.enonic.xp.impl.server.rest.task;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.export.ExportNodesParams;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.impl.server.rest.model.NodeExportResultJson;
import com.enonic.xp.impl.server.rest.task.listener.ExportListenerImpl;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;

public class ExportRunnableTask
    implements RunnableTask
{
    private final RepositoryId repositoryId;

    private final Branch branch;

    private final NodePath nodePath;

    private final String exportName;

    private final boolean exportWithIds;

    private final boolean dryRun;

    private final boolean includeVersions;

    private final Integer batchSize;

    private final ExportService exportService;


    private ExportRunnableTask( Builder builder )
    {
        this.repositoryId = builder.repositoryId;
        this.branch = builder.branch;
        this.nodePath = builder.nodePath;
        this.exportName = builder.exportName;
        this.includeVersions = builder.includeVersions;
        this.exportWithIds = builder.exportWithIds;
        this.dryRun = builder.dryRun;
        this.batchSize = builder.batchSize;

        this.exportService = builder.exportService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void run( final TaskId id, final ProgressReporter progressReporter )
    {
        final ExportNodesParams.Builder paramsBuilder = ExportNodesParams.create()
            .sourceNodePath( nodePath )
            .exportName( exportName )
            .dryRun( dryRun )
            .includeNodeIds( exportWithIds )
            .includeVersions( includeVersions )
            .nodeExportListener( new ExportListenerImpl( progressReporter ) );

        if ( batchSize != null )
        {
            paramsBuilder.batchSize( batchSize );
        }

        final NodeExportResult result =
            getContext( branch, repositoryId ).callWith( () -> this.exportService.exportNodes( paramsBuilder.build() ) );

        progressReporter.info( NodeExportResultJson.from( result ).toString() );
    }

    private Context getContext( final Branch branch, final RepositoryId repositoryId )
    {
        return ContextBuilder.from( ContextAccessor.current() ).branch( branch ).repositoryId( repositoryId ).build();
    }

    public static class Builder
    {
        private RepositoryId repositoryId;

        private Branch branch;

        private NodePath nodePath;

        private String exportName;

        private boolean exportWithIds;

        private boolean dryRun;

        private boolean includeVersions;

        private Integer batchSize;

        private ExportService exportService;

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder branch( final Branch branch )
        {
            this.branch = branch;
            return this;
        }

        public Builder nodePath( final NodePath nodePath )
        {
            this.nodePath = nodePath;
            return this;
        }

        public Builder exportName( final String exportName )
        {
            this.exportName = exportName;
            return this;
        }

        public Builder exportWithIds( final boolean exportWithIds )
        {
            this.exportWithIds = exportWithIds;
            return this;
        }

        public Builder dryRun( final boolean dryRun )
        {
            this.dryRun = dryRun;
            return this;
        }

        public Builder includeVersions( final boolean includeVersions )
        {
            this.includeVersions = includeVersions;
            return this;
        }

        public Builder batchSize( final Integer batchSize )
        {
            this.batchSize = batchSize;
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
