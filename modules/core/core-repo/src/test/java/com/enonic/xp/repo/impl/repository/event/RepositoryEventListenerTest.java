package com.enonic.xp.repo.impl.repository.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.Event;
import com.enonic.xp.repo.impl.RepositoryEvents;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.internal.InternalRepositoryService;

class RepositoryEventListenerTest
{
    private RepositoryEventListener repositoryEventListener;

    private InternalRepositoryService repositoryService;

    @BeforeEach
    void setUp()
    {
        this.repositoryService = Mockito.mock( InternalRepositoryService.class );

        repositoryEventListener = new RepositoryEventListener( repositoryService );
    }

    @Test
    void node_restored_event()
    {
        executeInContext( () -> {
            final Event localEvent = RepositoryEvents.restored();
            repositoryEventListener.onEvent( Event.create( localEvent ).localOrigin( false ).build() );
        } );

        Mockito.verify( repositoryService, Mockito.times( 1 ) ).invalidateAll();
    }


    @Test
    void node_restore_initialized()
    {
        executeInContext( () -> {
            final Event localEvent = RepositoryEvents.restoreInitialized();
            repositoryEventListener.onEvent( Event.create( localEvent ).localOrigin( false ).build() );
        } );

        Mockito.verify( repositoryService, Mockito.times( 1 ) ).invalidateAll();
    }

    private void executeInContext( final Runnable runnable )
    {
        ContextBuilder.from( ContextAccessor.current() )
            .repositoryId( RepositoryId.from( "com.enonic.cms.myproject" ) )
            .build()
            .runWith( runnable );
    }
}
