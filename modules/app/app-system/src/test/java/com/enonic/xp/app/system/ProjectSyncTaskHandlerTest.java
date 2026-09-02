package com.enonic.xp.app.system;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.content.ProjectSyncParams;
import com.enonic.xp.content.SyncContentService;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.project.Projects;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;
import com.enonic.xp.task.TaskInfo;
import com.enonic.xp.task.TaskProgressReporterContext;
import com.enonic.xp.task.TaskService;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectSyncTaskHandlerTest
    extends ScriptTestSupport
{
    @Captor
    private ArgumentCaptor<ProjectSyncParams> paramsCaptor;

    @Mock
    private ProjectService projectService;

    @Mock
    private SyncContentService syncContentService;

    @Mock
    private TaskService taskService;

    @Mock
    private ProgressReporter progressReporter;

    @Override
    public void initialize()
        throws Exception
    {
        super.initialize();
        addService( ProjectService.class, this.projectService );
        addService( SyncContentService.class, this.syncContentService );
        addService( TaskService.class, this.taskService );
    }

    @Test
    void syncAll()
    {
        final Project parent = createProject( "parent", null );
        final Project child1 = createProject( "child1", "parent" );
        final Project child2 = createProject( "child2", "child1" );
        when( projectService.list() ).thenReturn( Projects.create().addAll( Set.of( parent, child1, child2 ) ).build() );

        TaskProgressReporterContext.withContext( ( id, reporter ) -> runFunction( "/test/ProjectSyncTaskHandlerTest.js", "syncAll" ) )
            .run( task(), progressReporter );

        verify( syncContentService, times( 2 ) ).syncProject( paramsCaptor.capture() );
        assertThat( paramsCaptor.getAllValues().stream().map( ProjectSyncParams::getTargetProject ) ).containsExactly( child1.getName(),
                                                                                                                     child2.getName() );
    }

    @Test
    void syncSelected()
    {
        final Project parent = createProject( "parent", null );
        final Project child1 = createProject( "child1", "parent" );
        final Project child2 = createProject( "child2", "child1" );
        when( projectService.list() ).thenReturn( Projects.create().addAll( Set.of( parent, child1, child2 ) ).build() );

        TaskProgressReporterContext.withContext( ( id, reporter ) -> runFunction( "/test/ProjectSyncTaskHandlerTest.js", "sync" ) )
            .run( task(), progressReporter );

        verify( syncContentService ).syncProject( paramsCaptor.capture() );
        assertThat( paramsCaptor.getValue().getTargetProject() ).isEqualTo( child2.getName() );
    }

    @Test
    void order()
    {
        final Projects projects = Projects.create()
            .addAll( Set.of( createProject( "turkey-tr-tr", "turkey-tr" ), createProject( "enonic-common", null ),
                             createProject( "corporate", "enonic-common" ), createProject( "corporate-no", "corporate" ),
                             createProject( "countries", "enonic-common" ), createProject( "denmark", "countries" ),
                             createProject( "without-actual-parent1", "unknown-parent1" ), createProject( "denmark-de", "denmark" ),
                             createProject( "sweden", "countries" ), createProject( "sweden-sw", "sweden" ),
                             createProject( "sweden-sw-sw", "sweden-sw" ), createProject( "root1", null ),
                             createProject( "child1", "root1" ), createProject( "without-actual-parent2", "unknown-parent2" ),
                             createProject( "turkey", "countries" ), createProject( "turkey-tr", "turkey" ) ) )
            .build();

        final List<ProjectName> syncProjects = ProjectSyncTaskHandler.sortAndFilterProjectsForSync( projects );

        assertAll( () -> assertThat( syncProjects ).hasSize( 12 ),
                   () -> assertThat( index( syncProjects, "turkey-tr-tr" ) ).isGreaterThan( index( syncProjects, "turkey-tr" ) ),
                   () -> assertThat( index( syncProjects, "turkey-tr" ) ).isGreaterThan( index( syncProjects, "turkey" ) ),
                   () -> assertThat( index( syncProjects, "turkey" ) ).isGreaterThan( index( syncProjects, "countries" ) ),
                   () -> assertThat( index( syncProjects, "corporate-no" ) ).isGreaterThan( index( syncProjects, "corporate" ) ),
                   () -> assertThat( index( syncProjects, "denmark-de" ) ).isGreaterThan( index( syncProjects, "denmark" ) ),
                   () -> assertThat( index( syncProjects, "denmark" ) ).isGreaterThan( index( syncProjects, "countries" ) ),
                   () -> assertThat( index( syncProjects, "sweden-sw-sw" ) ).isGreaterThan( index( syncProjects, "sweden-sw" ) ),
                   () -> assertThat( index( syncProjects, "sweden-sw" ) ).isGreaterThan( index( syncProjects, "sweden" ) ),
                   () -> assertThat( index( syncProjects, "sweden" ) ).isGreaterThan( index( syncProjects, "countries" ) ),
                   () -> assertThat( index( syncProjects, "child1" ) ).isGreaterThan( index( syncProjects, "root1" ) ),
                   () -> assertThat( index( syncProjects, "without-actual-parent1" ) ).isEqualTo( -1 ),
                   () -> assertThat( index( syncProjects, "without-actual-parent2" ) ).isEqualTo( -1 ),
                   () -> assertThat( syncProjects ).doesNotContain( ProjectName.from( "enonic-common" ), ProjectName.from( "root1" ) ) );
    }

    private static int index( final List<ProjectName> projects, final String name )
    {
        return projects.indexOf( ProjectName.from( name ) );
    }

    private TaskId task()
    {
        final TaskId taskId = TaskId.from( "task" );
        when( taskService.getTaskInfo( taskId ) ).thenReturn( TaskInfo.create()
                                                                  .id( taskId )
                                                                  .name( "com.enonic.xp.app.system:project-sync" )
                                                                  .application( ApplicationKey.SYSTEM )
                                                                  .startTime( Instant.now() )
                                                                  .build() );
        return taskId;
    }

    private static Project createProject( final String name, final String parent )
    {
        final Project.Builder project = Project.create().name( ProjectName.from( name ) ).displayName( name ).description( name );
        if ( parent != null )
        {
            project.addParent( ProjectName.from( parent ) );
        }
        return project.build();
    }
}
