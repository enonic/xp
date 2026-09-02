package com.enonic.xp.app.system;

import java.util.ArrayDeque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import com.enonic.xp.content.ProjectSyncParams;
import com.enonic.xp.content.SyncContentService;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.project.Projects;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskService;

/**
 * The {@code com.enonic.xp.app.system:project-sync} task: syncs inherited content from parent projects into child
 * projects, parents first. Without {@code projects} every project with a parent is synced. Runs as a cluster task.
 */
public class ProjectSyncTaskHandler
    implements ScriptBean
{
    private ProjectService projectService;

    private SyncContentService syncContentService;

    private TaskService taskService;

    private List<String> projects;

    private TaskId taskId;

    public void setProjects( final List<String> projects )
    {
        this.projects = projects;
    }

    public void setTaskId( final String taskId )
    {
        this.taskId = TaskId.from( taskId );
    }

    public void execute()
    {
        TaskUtils.checkAlreadySubmitted( taskService.getTaskInfo( taskId ), taskService.getAllTasks() );

        final Set<ProjectName> selected =
            projects == null ? Set.of() : projects.stream().map( ProjectName::from ).collect( Collectors.toUnmodifiableSet() );

        sortAndFilterProjectsForSync( projectService.list() ).stream()
            .filter( projectName -> selected.isEmpty() || selected.contains( projectName ) )
            .forEach( projectName -> syncContentService.syncProject( ProjectSyncParams.create().targetProject( projectName ).build() ) );
    }

    /**
     * Orders projects so that a project follows every parent it has, and leaves out projects without a (known) parent -
     * there is nothing to inherit into them.
     */
    static List<ProjectName> sortAndFilterProjectsForSync( final Projects projects )
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

    @Override
    public void initialize( final BeanContext context )
    {
        this.projectService = context.getService( ProjectService.class ).get();
        this.syncContentService = context.getService( SyncContentService.class ).get();
        this.taskService = context.getService( TaskService.class ).get();
    }
}
