package com.enonic.xp.admin.impl.rest.resource.content.task;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ProjectSyncParams;
import com.enonic.xp.content.SyncContentService;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.RunnableTask;
import com.enonic.xp.task.TaskId;

public final class ProjectsSyncTask
    implements RunnableTask
{
    private final ProjectService projectService;

    private final SyncContentService syncContentService;

    public ProjectsSyncTask( final Builder builder )
    {
        this.projectService = builder.projectService;
        this.syncContentService = builder.syncContentService;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public void run( final TaskId taskId, final ProgressReporter progressReporter )
    {
        this.projectService.list().
            stream().
            filter( project -> project.getParent() != null ).
            sorted( ( o1, o2 ) -> {

                if ( o2.getName().equals( o1.getParent() ) )
                {
                    return 1;
                }

                if ( o1.getName().equals( o2.getParent() ) )
                {
                    return -1;
                }

                return 0;
            } ).
            forEach( this::doSync );
    }

    private void doSync( final Project targetProject )
    {
        syncContentService.syncProject( ProjectSyncParams.create().
            targetProject( targetProject.getName() ).
            build() );
    }

    public static class Builder
    {
        private ProjectService projectService;

        private SyncContentService syncContentService;

        private Builder()
        {
        }

        public Builder projectService( final ProjectService projectService )
        {
            this.projectService = projectService;
            return this;
        }

        public Builder syncContentService( final SyncContentService syncContentService )
        {
            this.syncContentService = syncContentService;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.projectService, "projectService must be set." );
            Preconditions.checkNotNull( this.syncContentService, "syncContentService must be set." );
        }

        public ProjectsSyncTask build()
        {
            validate();
            return new ProjectsSyncTask( this );
        }
    }
}
