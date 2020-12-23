package com.enonic.xp.admin.impl.rest.resource.content.task;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.content.ProjectSyncParams;
import com.enonic.xp.content.SyncContentService;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.project.Projects;
import com.enonic.xp.task.ProgressReporter;
import com.enonic.xp.task.TaskId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

@ExtendWith(MockitoExtension.class)
class ProjectsSyncTaskTest
{
    @Mock(stubOnly = true)
    private ProjectService projectService;

    @Mock
    private SyncContentService syncContentService;

    @Captor
    private ArgumentCaptor<ProjectSyncParams> paramsCaptor;

    @Test
    void syncAll()
    {
        final Project parent = createProject( "parent", null );
        final Project child1 = createProject( "child1", "parent" );
        final Project child2 = createProject( "child2", "child1" );

        when( projectService.list() ).thenReturn( Projects.create().addAll( Set.of( parent, child1, child2 ) ).build() );

        ProjectsSyncTask.create().
            projectService( projectService ).
            syncContentService( syncContentService ).
            build().
            run( TaskId.from( "taskId" ), mock( ProgressReporter.class, withSettings().stubOnly() ) );

        verify( syncContentService, times( 2 ) ).syncProject( paramsCaptor.capture() );

        final List<ProjectName> syncProjects = paramsCaptor.getAllValues().
            stream().
            map( ProjectSyncParams::getTargetProject ).
            collect( Collectors.toList() );

        assertThat( syncProjects ).containsExactly( child1.getName(), child2.getName() );
    }

    @Test
    void order()
    {
        final Projects projects = Projects.create().addAll(
            Set.of( createProject( "turkey-tr-tr", "turkey-tr" ), createProject( "enonic-common", null ),
                    createProject( "corporate", "enonic-common" ), createProject( "corporate-no", "corporate" ),
                    createProject( "countries", "enonic-common" ), createProject( "denmark", "countries" ),
                    createProject( "without-actual-parent1", "unknown-parent1" ), createProject( "denmark-de", "denmark" ),
                    createProject( "sweden", "countries" ), createProject( "sweden-sw", "sweden" ),
                    createProject( "sweden-sw-sw", "sweden-sw" ), createProject( "root1", null ), createProject( "child1", "root1" ),
                    createProject( "without-actual-parent2", "unknown-parent2" ), createProject( "turkey", "countries" ),
                    createProject( "turkey-tr", "turkey" ) ) ).
            build();

        when( projectService.list() ).thenReturn( projects );

        ProjectsSyncTask.create().
            projectService( projectService ).
            syncContentService( syncContentService ).
            build().
            run( TaskId.from( "taskId" ), mock( ProgressReporter.class, withSettings().stubOnly() ) );

        verify( syncContentService, times( 14 ) ).syncProject( paramsCaptor.capture() );

        final List<ProjectName> syncProjects = paramsCaptor.getAllValues().
            stream().
            map( ProjectSyncParams::getTargetProject ).
            collect( Collectors.toList() );

        assertAll( () -> assertThat( syncProjects.indexOf( ProjectName.from( "turkey-tr-tr" ) ) ).
                       isGreaterThan( syncProjects.indexOf( ProjectName.from( "turkey-tr" ) ) ),

                   () -> assertThat( syncProjects.indexOf( ProjectName.from( "turkey-tr" ) ) ).
                       isGreaterThan( syncProjects.indexOf( ProjectName.from( "turkey" ) ) ),

                   () -> assertThat( syncProjects.indexOf( ProjectName.from( "turkey" ) ) ).
                       isGreaterThan( syncProjects.indexOf( ProjectName.from( "countries" ) ) ),

                   () -> assertThat( syncProjects.indexOf( ProjectName.from( "corporate-no" ) ) ).
                       isGreaterThan( syncProjects.indexOf( ProjectName.from( "corporate" ) ) ),

                   () -> assertThat( syncProjects.indexOf( ProjectName.from( "denmark-de" ) ) ).
                       isGreaterThan( syncProjects.indexOf( ProjectName.from( "denmark" ) ) ),

                   () -> assertThat( syncProjects.indexOf( ProjectName.from( "denmark" ) ) ).
                       isGreaterThan( syncProjects.indexOf( ProjectName.from( "countries" ) ) ),

                   () -> assertThat( syncProjects.indexOf( ProjectName.from( "sweden-sw-sw" ) ) ).
                       isGreaterThan( syncProjects.indexOf( ProjectName.from( "sweden-sw" ) ) ),

                   () -> assertThat( syncProjects.indexOf( ProjectName.from( "sweden-sw" ) ) ).
                       isGreaterThan( syncProjects.indexOf( ProjectName.from( "sweden" ) ) ),

                   () -> assertThat( syncProjects.indexOf( ProjectName.from( "sweden" ) ) ).
                       isGreaterThan( syncProjects.indexOf( ProjectName.from( "countries" ) ) ),

                   () -> assertThat( syncProjects.indexOf( ProjectName.from( "child1" ) ) ).
                       isGreaterThan( syncProjects.indexOf( ProjectName.from( "root1" ) ) ),

                   () -> assertThat( syncProjects.indexOf( ProjectName.from( "without-actual-parent1" ) ) ).isGreaterThanOrEqualTo( 12 ),
                   () -> assertThat( syncProjects.indexOf( ProjectName.from( "without-actual-parent2" ) ) ).isGreaterThanOrEqualTo( 12 ),

                   () -> assertThat( syncProjects ).doesNotContain( ProjectName.from( "enoic-common" ), ProjectName.from( "root1" ) ) );
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
