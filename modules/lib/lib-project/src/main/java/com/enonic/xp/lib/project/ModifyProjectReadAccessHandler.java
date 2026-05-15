package com.enonic.xp.lib.project;

import com.enonic.xp.content.ApplyPermissionsListener;
import com.enonic.xp.lib.project.mapper.ProjectReadAccessMapper;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.SetProjectReadAccessParams;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.task.ProgressReportParams;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskProgressReporterContext;

public final class ModifyProjectReadAccessHandler
    extends BaseProjectHandler
{
    private ProjectName id;

    private boolean isPublic;

    @Override
    protected ProjectReadAccessMapper doExecute()
    {
        final boolean result = this.projectService.get()
            .setReadAccess(
                SetProjectReadAccessParams.create().name( this.id ).isPublic( this.isPublic ).listener( taskProgressListener() ).build() );

        return new ProjectReadAccessMapper( result );
    }

    private static ApplyPermissionsListener taskProgressListener()
    {
        final ProgressReporter reporter = TaskProgressReporterContext.current();
        if ( reporter == null )
        {
            return null;
        }
        return new ProgressReporterListener( reporter );
    }

    private static final class ProgressReporterListener
        implements ApplyPermissionsListener
    {
        private final ProgressReporter reporter;

        private int applied;

        private int total;

        ProgressReporterListener( final ProgressReporter reporter )
        {
            this.reporter = reporter;
        }

        @Override
        public void setTotal( final int count )
        {
            this.total = count;
            this.reporter.progress( ProgressReportParams.create().current( applied ).total( count ).build() );
        }

        @Override
        public void permissionsApplied( final int count )
        {
            this.applied += count;
            this.reporter.progress( ProgressReportParams.create().current( applied ).total( total ).build() );
        }

        @Override
        public void notEnoughRights( final int count )
        {
            // no-op: progress is reported as items are applied; rejected items are not counted
        }
    }

    public void setId( final String value )
    {
        this.id = ProjectName.from( value );
    }

    public void setReadAccess( final ScriptValue value )
    {
        this.isPublic = buildReadAccess( value );
    }
}
