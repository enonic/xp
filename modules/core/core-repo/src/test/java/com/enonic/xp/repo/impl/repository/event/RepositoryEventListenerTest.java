package com.enonic.xp.repo.impl.repository.event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.event.Event;
import com.enonic.xp.repo.impl.RepositoryEvents;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.RepositoryService;

public class RepositoryEventListenerTest
{
    private RepositoryEventListener repositoryEventListener;

    private NodeStorageService storageService;

    private RepositoryService repositoryService;

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.storageService = Mockito.mock( NodeStorageService.class );
        this.repositoryService = Mockito.mock( RepositoryService.class );

        repositoryEventListener = new RepositoryEventListener( repositoryService, storageService );
    }

    @Test
    public void node_restored_event()
        throws Exception
    {
        final Event localEvent = RepositoryEvents.restored();
        repositoryEventListener.onEvent( Event.create( localEvent ).
            localOrigin( false ).
            build() );
        Mockito.verify( storageService, Mockito.times( 1 ) ).invalidate();
        Mockito.verify( repositoryService, Mockito.times( 1 ) ).invalidateAll();
    }


    @Test
    public void node_restore_initialized()
        throws Exception
    {
        final Event localEvent = RepositoryEvents.restoreInitialized();
        repositoryEventListener.onEvent( Event.create( localEvent ).
            localOrigin( false ).
            build() );
        Mockito.verify( storageService, Mockito.times( 1 ) ).invalidate();
        Mockito.verify( repositoryService, Mockito.times( 1 ) ).invalidateAll();
    }


}
