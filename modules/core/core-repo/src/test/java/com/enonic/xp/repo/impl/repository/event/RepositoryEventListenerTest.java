package com.enonic.xp.repo.impl.repository.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.event.Event;
import com.enonic.xp.repo.impl.RepositoryEvents;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.internal.InternalRepositoryService;

public class RepositoryEventListenerTest
{
    private RepositoryEventListener repositoryEventListener;

    private NodeStorageService storageService;

    private InternalRepositoryService repositoryService;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.storageService = Mockito.mock( NodeStorageService.class );
        this.repositoryService = Mockito.mock( InternalRepositoryService.class );

        repositoryEventListener = new RepositoryEventListener( repositoryService, storageService );
    }

    @Test
    public void node_restored_event()
        throws Exception
    {
        executeInContext( () -> {
            final Event localEvent = RepositoryEvents.restored();
            repositoryEventListener.onEvent( Event.create( localEvent ).localOrigin( false ).build() );
        } );

        Mockito.verify( storageService, Mockito.times( 1 ) ).invalidate();
        Mockito.verify( repositoryService, Mockito.times( 1 ) ).invalidateAll();
    }


    @Test
    public void node_restore_initialized()
        throws Exception
    {
        executeInContext( () -> {
            final Event localEvent = RepositoryEvents.restoreInitialized();
            repositoryEventListener.onEvent( Event.create( localEvent ).localOrigin( false ).build() );
        } );

        Mockito.verify( storageService, Mockito.times( 1 ) ).invalidate();
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
