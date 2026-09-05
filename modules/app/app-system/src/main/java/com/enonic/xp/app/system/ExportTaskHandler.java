package com.enonic.xp.app.system;

import com.enonic.xp.app.system.json.NodeExportResultJson;
import com.enonic.xp.app.system.listener.ExportListenerImpl;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.export.ExportNodesParams;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.NodeExportResult;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskProgressReporterContext;

/**
 * The {@code com.enonic.xp.app.system:export} task: exports a node subtree to {@code $XP_HOME/data/export/<name>}.
 */
public class ExportTaskHandler
    implements ScriptBean
{
    private ExportService exportService;

    private String repository;

    private String branch;

    private String nodePath;

    private String exportName;

    private Integer batchSize;

    public void setRepository( final String repository )
    {
        this.repository = repository;
    }

    public void setBranch( final String branch )
    {
        this.branch = branch;
    }

    public void setNodePath( final String nodePath )
    {
        this.nodePath = nodePath;
    }

    public void setExportName( final String exportName )
    {
        this.exportName = exportName;
    }

    public void setBatchSize( final Integer batchSize )
    {
        this.batchSize = batchSize;
    }

    public void execute()
    {
        TaskUtils.requireAdmin();

        final ProgressReporter progressReporter = TaskProgressReporterContext.current();

        final ExportNodesParams.Builder params = ExportNodesParams.create()
            .sourceNodePath( new NodePath( nodePath ) )
            .exportName( exportName )
            .nodeExportListener( new ExportListenerImpl( progressReporter ) );
        if ( batchSize != null )
        {
            params.batchSize( batchSize );
        }

        final NodeExportResult result = ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( RepositoryId.from( repository ) )
            .branch( Branch.from( branch ) )
            .build()
            .callWith( () -> exportService.exportNodes( params.build() ) );

        progressReporter.progress( ProgressReportParams.create( NodeExportResultJson.from( result ).toString() ).build() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.exportService = context.getService( ExportService.class ).get();
    }
}
