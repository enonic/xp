package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.content.ProjectSyncParams;
import com.enonic.xp.content.SyncContentService;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.project.Projects;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProjectsSyncTaskTest
{
    private ProjectService projectService;

    private SyncContentService syncContentService;

    private ArgumentCaptor<ProjectSyncParams> paramsCaptor;


    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.projectService = Mockito.mock( ProjectService.class );
        this.syncContentService = Mockito.mock( SyncContentService.class );

        this.paramsCaptor = ArgumentCaptor.forClass( ProjectSyncParams.class );
    }

    private ProjectsSyncTask createAndRunTask()
    {
        final ProjectsSyncTask task = ProjectsSyncTask.create().
            projectService( projectService ).
            syncContentService( syncContentService ).
            build();

        final ProgressReporter progressReporter = Mockito.mock( ProgressReporter.class );

        task.run( TaskId.from( "taskId" ), progressReporter );

        return task;
    }

    @Test
    public void syncAll()
    {
        final Project parent = createProject( "parent", null );
        final Project child1 = createProject( "child1", "parent" );
        final Project child2 = createProject( "child2", "child1" );

        Mockito.when( projectService.list() ).thenReturn( Projects.create().addAll( Set.of( parent, child2, child1 ) ).build() );
        Mockito.when( projectService.get( parent.getName() ) ).thenReturn( parent );
        Mockito.when( projectService.get( child1.getName() ) ).thenReturn( child1 );
        Mockito.when( projectService.get( child2.getName() ) ).thenReturn( child2 );

        createAndRunTask();

        Mockito.verify( syncContentService, Mockito.times( 2 ) ).syncProject( paramsCaptor.capture() );

        assertEquals( child1.getName(), paramsCaptor.getAllValues().get( 0 ).getTargetProject() );

        assertEquals( child2.getName(), paramsCaptor.getAllValues().get( 1 ).getTargetProject() );
    }


    private Project createProject( final String name, final String parent )
    {
        return Project.create().
            name( ProjectName.from( name ) ).
            parent( parent != null ? ProjectName.from( parent ) : null ).
            displayName( name ).
            description( name ).
            build();
    }

}
