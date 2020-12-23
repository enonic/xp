package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

import com.google.common.base.Preconditions;

import com.enonic.xp.content.ProjectSyncParams;
import com.enonic.xp.content.SyncContentService;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.project.Projects;
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
        sortProjects( this.projectService.list() ).
            stream().
            filter( project -> project.getParent() != null ).
            forEach( this::doSync );
    }

    private void doSync( final Project targetProject )
    {
        syncContentService.syncProject( ProjectSyncParams.create().
            targetProject( targetProject.getName() ).
            build() );
    }

    private List<Project> sortProjects( final Projects projects )
    {
        final List<Project> result = new ArrayList<>();
        final Queue<Project> queue = new ArrayDeque<>( projects.getList() );

        ProjectName currentParent = null;
        int currentParentCounter = 0;
        int loopSize = queue.size();

        while ( !queue.isEmpty() )
        {
            if ( loopSize == 0 )
            {
                if ( currentParentCounter < result.size() )
                {
                    currentParent = result.get( currentParentCounter ).getName();
                    currentParentCounter++;
                }
                else
                {  // projects with invalid parent in queue
                    currentParent = queue.peek().getParent();
                }

                loopSize = queue.size();

            }

            loopSize--;

            final Project current = queue.poll();
            if ( Objects.equals( current.getParent(), currentParent ) )
            {
                result.add( current );
            }
            else
            {
                queue.offer( current );
            }
        }

        return result;
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
