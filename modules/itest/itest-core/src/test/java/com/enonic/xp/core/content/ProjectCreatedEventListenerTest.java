package com.enonic.xp.core.content;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.core.impl.content.ParentContentSynchronizer;
import com.enonic.xp.core.impl.content.ProjectCreatedEventListener;
import com.enonic.xp.impl.task.MockTaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProjectCreatedEventListenerTest
    extends AbstractContentSynchronizerTest
{
    @BeforeEach
    void setUp()
    {
        final ParentContentSynchronizer synchronizer = new ParentContentSynchronizer( layersContentService );
        listener = new ProjectCreatedEventListener( this.projectService, new MockTaskService(), synchronizer );
    }

    @Test
    void testSingle()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        handleEvents();

        final Content targetContent = layerContext.callWith( () -> contentService.getById( sourceContent.getId() ) );

        compareSynched( sourceContent, targetContent );
    }

    @Test
    void testChildren()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );
        final Content sourceChild1 = projectContext.callWith( () -> createContent( sourceContent.getPath(), "name" ) );
        final Content sourceChild2 = projectContext.callWith( () -> createContent( sourceChild1.getPath(), "name" ) );

        handleEvents();

        final Content targetChild1 = layerContext.callWith( () -> contentService.getById( sourceChild1.getId() ) );
        final Content targetChild2 = layerContext.callWith( () -> contentService.getById( sourceChild2.getId() ) );

        compareSynched( sourceChild1, targetChild1 );
        compareSynched( sourceChild2, targetChild2 );
    }

    @Test
    void testFromMultipleParents()
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        handleEvents();

        final Content parentContent1 = childLayerContext.callWith( () -> contentService.getById( sourceContent.getId() ) );
//        final Content parentContent2 = secondChildLayerContext.callWith( () -> contentService.getById( sourceContent.getId() ) );

        final Content targetContent = mixedChildLayerContext.callWith( () -> contentService.getById( sourceContent.getId() ) );

        compareSynched( parentContent1, targetContent );
        assertEquals( childLayer.getName(), targetContent.getOriginProject() );
    }

}
