package com.enonic.xp.app.system;

import com.enonic.xp.app.system.json.NodeImportResultJson;
import com.enonic.xp.app.system.listener.ImportListenerImpl;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.export.ExportService;
import com.enonic.xp.export.ImportNodesParams;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskProgressReporterContext;

/**
 * The {@code com.enonic.xp.app.system:import} task: imports {@code $XP_HOME/data/export/<name>} into a node path. No XSLT
 * transformation: a stylesheet read from the exports directory has no provenance, so only {@code lib-export} with an
 * application resource can transform on import.
 */
public class ImportTaskHandler
    implements ScriptBean
{
    private ExportService exportService;

    private String exportName;

    private String repository;

    private String branch;

    private String nodePath;

    private boolean importWithIds = true;

    private boolean importWithPermissions = true;

    public void setExportName( final String exportName )
    {
        this.exportName = exportName;
    }

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

    public void setImportWithIds( final boolean importWithIds )
    {
        this.importWithIds = importWithIds;
    }

    public void setImportWithPermissions( final boolean importWithPermissions )
    {
        this.importWithPermissions = importWithPermissions;
    }

    public void execute()
    {
        TaskUtils.requireAdmin();

        final ProgressReporter progressReporter = TaskProgressReporterContext.current();

        final NodeImportResult result = ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( RepositoryId.from( repository ) )
            .branch( Branch.from( branch ) )
            .build()
            .callWith( () -> exportService.importNodes( ImportNodesParams.create()
                                                            .exportName( exportName )
                                                            .targetNodePath( new NodePath( nodePath ) )
                                                            .includeNodeIds( importWithIds )
                                                            .includePermissions( importWithPermissions )
                                                            .nodeImportListener( new ImportListenerImpl( progressReporter ) )
                                                            .build() ) );

        progressReporter.progress( ProgressReportParams.create( NodeImportResultJson.from( result ).toString() ).build() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.exportService = context.getService( ExportService.class ).get();
    }
}
