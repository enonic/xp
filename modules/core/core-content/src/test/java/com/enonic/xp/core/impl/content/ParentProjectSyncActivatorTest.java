package com.enonic.xp.core.impl.content;

import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.enonic.xp.index.IndexService;
import com.enonic.xp.project.Project;
import com.enonic.xp.project.ProjectName;
import com.enonic.xp.project.ProjectService;
import com.enonic.xp.project.Projects;

@ExtendWith(MockitoExtension.class)
public class ParentProjectSyncActivatorTest
{

    @Mock
    private IndexService indexService;

    @Mock
    private ContentSynchronizer contentSynchronizer;

    @Mock
    private ProjectService projectService;

    @Mock
    private ContentConfig contentConfig;

    @BeforeEach
    public void init()
    {

        Mockito.when( contentConfig.content_sync_period() ).thenReturn( "PT1S" );
        Mockito.when( indexService.isMaster() ).thenReturn( true );


    }

    @Test
    public void testSync()
        throws Exception
    {
        final Project source = Project.create().
            name( ProjectName.from( "source" ) ).
            build();

        final Project target1 = Project.create().
            name( ProjectName.from( "target1" ) ).
            parent( ProjectName.from( "source" ) ).
            build();

        final Project target2 = Project.create().
            name( ProjectName.from( "target2" ) ).
            parent( ProjectName.from( "target1" ) ).
            build();

        Mockito.when( projectService.get( source.getName() ) ).thenReturn( source );
        Mockito.when( projectService.get( target1.getName() ) ).thenReturn( target1 );

        Mockito.when( projectService.list() ).thenReturn( Projects.create().
            addAll( Set.of( source, target1, target2 ) ).
            build() );

        ArgumentCaptor<ContentSyncParams> captor = ArgumentCaptor.forClass( ContentSyncParams.class );

        new ParentProjectSyncActivator( contentConfig, projectService, indexService, contentSynchronizer );

        Thread.sleep( 1500 );

        Mockito.verify( contentSynchronizer, Mockito.atLeast( 2 ) ).sync( captor.capture() );

        Assertions.assertEquals( "source", captor.getAllValues().get( 0 ).getSourceProject().toString() );
        Assertions.assertEquals( "target1", captor.getAllValues().get( 0 ).getTargetProject().toString() );

        Assertions.assertEquals( "target1", captor.getAllValues().get( 1 ).getSourceProject().toString() );
        Assertions.assertEquals( "target2", captor.getAllValues().get( 1 ).getTargetProject().toString() );
    }

    @Test
    public void testDeactivate()
        throws Exception
    {
        final ParentProjectSyncActivator activator =
            new ParentProjectSyncActivator( contentConfig, projectService, indexService, contentSynchronizer );

        activator.deactivate();

        Thread.sleep( 1500 );

        Mockito.verify( contentSynchronizer, Mockito.never() ).sync( Mockito.isA( ContentSyncParams.class ) );
    }
}
