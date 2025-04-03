package com.enonic.xp.repo.impl.repository.event;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.repo.impl.RepositoryEvents;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.internal.InternalRepositoryService;

@Component(immediate = true)
public class RepositoryEventListener
    implements EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( RepositoryEventListener.class );

    private final RepositoryRestoredHandler repositoryRestoredHandler;

    private final RepositoryInvalidateByIdHandler repositoryInvalidateByIdHandler;

    private final RepositoryRestoreInitializedHandler repositoryRestoreInitializedHandler;

    @Activate
    public RepositoryEventListener( @Reference final InternalRepositoryService repositoryService,
                                    @Reference final NodeStorageService storageService )
    {
        this.repositoryRestoredHandler = RepositoryRestoredHandler.create().
            repositoryService( repositoryService ).
            nodeStorageService( storageService ).
            build();
        this.repositoryInvalidateByIdHandler = new RepositoryInvalidateByIdHandler( repositoryService, storageService );
        this.repositoryRestoreInitializedHandler = new RepositoryRestoreInitializedHandler( repositoryService, storageService );
    }

    @Override
    public int getOrder()
    {
        return 0;
    }

    @Override
    public void onEvent( final Event event )
    {
        doHandleEvent( event );
    }

    private void doHandleEvent( final Event event )
    {
        final String type = event.getType();

        switch ( type )
        {
            case RepositoryEvents.RESTORED_EVENT_TYPE:
                handleEventType( event, repositoryRestoredHandler );
                break;
            case RepositoryEvents.RESTORE_INITIALIZED_EVENT_TYPE:
                handleEventType( event, repositoryRestoreInitializedHandler );
                break;
            case RepositoryEvents.UPDATED_EVENT_TYPE:
            case RepositoryEvents.DELETED_EVENT_TYPE:
                if ( !event.isLocalOrigin() )
                {
                    handleEventType( event, repositoryInvalidateByIdHandler );
                }
                break;
            default:
                break;
        }
    }

    private void handleEventType( final Event event, final RepositoryEventHandler repositoryEventHandler )
    {
        try
        {
            repositoryEventHandler.handleEvent( event );
        }
        catch ( Exception e )
        {
            LOG.error( "Not able to handle repository-event", e );
        }
    }
}
