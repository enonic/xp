package com.enonic.xp.repo.impl.repository.event;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.event.Event;
import com.enonic.xp.repo.impl.RepositoryEvents;
import com.enonic.xp.repo.impl.storage.StorageService;

public class RepositoryEventListenerTest
{
    private RepositoryEventListener repositoryEventListener;

    private StorageService storageService;

    @Before
    public void setUp()
        throws Exception
    {

        repositoryEventListener = new RepositoryEventListener();
        storageService = Mockito.mock( StorageService.class );
        repositoryEventListener.setStorageService( storageService );

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
    }


}