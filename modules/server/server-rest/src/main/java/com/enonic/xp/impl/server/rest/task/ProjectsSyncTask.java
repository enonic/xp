package com.enonic.xp.impl.server.rest.task;

import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

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
        sortAndFilterProjectsForSync( this.projectService.list() ).forEach( this::doSync );
    }

    private void doSync( final ProjectName targetProjectName )
    {
        syncContentService.syncProject( ProjectSyncParams.create().targetProject( targetProjectName ).build() );
    }

    private List<ProjectName> sortAndFilterProjectsForSync( final Projects projects )
    {
        final Set<ProjectName> projectNames = projects.stream().map( Project::getName ).collect( Collectors.toSet() );

        final Set<ProjectName> result = new LinkedHashSet<>();

        final Map<ProjectName, Project> normalizedProjects = projects.stream().map( project -> {

            final Project.Builder builder = Project.create().name( project.getName() );
            project.getParents().stream().filter( projectNames::contains ).forEach( builder::addParent );

            return builder.build();

        } ).collect( Collectors.toMap( Project::getName, project -> project ) );

        final Queue<Project> queue = new ArrayDeque<>( normalizedProjects.values() );

        while ( !queue.isEmpty() )
        {
            final Project current = queue.poll();

            if ( result.containsAll( current.getParents() ) )
            {
                result.add( current.getName() );
            }
            else
            {
                queue.offer( current );
            }
        }

        return result.stream()
            .map( normalizedProjects::get )
            .filter( project -> !project.getParents().isEmpty() )
            .map( Project::getName )
            .collect( Collectors.toList() );
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
            Objects.requireNonNull( this.projectService );
            Objects.requireNonNull( this.syncContentService );
        }

        public ProjectsSyncTask build()
        {
            validate();
            return new ProjectsSyncTask( this );
        }
    }
}
