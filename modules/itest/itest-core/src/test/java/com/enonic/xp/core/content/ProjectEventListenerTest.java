package com.enonic.xp.core.content;

import java.util.Set;

import org.assertj.core.util.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.core.impl.content.ParentContentSynchronizer;
import com.enonic.xp.core.impl.content.ProjectEventListener;
import com.enonic.xp.core.impl.project.ProjectEvents;
import com.enonic.xp.event.Event;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProjectEventListenerTest
    extends AbstractContentSynchronizerTest
{
    private ProjectEventListener listener;

    private ArgumentCaptor<Event> eventCaptor;

    private Set<Event> handledEvents;

    @BeforeEach
    protected void setUpNode()
        throws Exception
    {
        super.setUpNode();

        final ParentContentSynchronizer synchronizer = new ParentContentSynchronizer( contentService );
        listener = new ProjectEventListener( this.projectService, this.taskService, synchronizer );

        eventCaptor = ArgumentCaptor.forClass( Event.class );
        handledEvents = Sets.newHashSet();
    }

    @Test
    public void testSingle()
        throws InterruptedException
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        handleEvents();

        final Content targetContent = layerContext.callWith( () -> contentService.getById( sourceContent.getId() ) );

        compareSynched( sourceContent, targetContent );
    }

    @Test
    public void testChildren()
        throws InterruptedException
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
    public void testNotLocalEvent()
        throws InterruptedException
    {
        final Content sourceContent = projectContext.callWith( () -> createContent( ContentPath.ROOT, "name" ) );

        adminContext().runWith( () -> listener.onEvent( Event.create( ProjectEvents.CREATED_EVENT_TYPE )
                                                            .value( ProjectEvents.PROJECT_NAME_KEY, layer.getName() )
                                                            .localOrigin( false )
                                                            .build() ) );

        Thread.sleep( 1000 );

        assertThrows( ContentNotFoundException.class, () -> layerContext.runWith( () -> contentService.getById( sourceContent.getId() ) ) );
    }

    private void handleEvents()
        throws InterruptedException
    {
        Mockito.verify( eventPublisher, Mockito.atLeastOnce() ).publish( eventCaptor.capture() );
        eventCaptor.getAllValues().stream().
            filter( event -> !handledEvents.contains( event ) ).
            forEach( listener::onEvent );
        handledEvents.addAll( eventCaptor.getAllValues() );
        Thread.sleep( 1000 );

    }

}
