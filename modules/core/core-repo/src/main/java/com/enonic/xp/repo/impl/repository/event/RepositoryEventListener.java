package com.enonic.xp.repo.impl.repository.event;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.RepositoryEvents;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.RepositoryService;

@Component(immediate = true)
public class RepositoryEventListener
    implements EventListener
{
    private NodeStorageService storageService;

    private RepositoryService repositoryService;

    private final static Logger LOG = LoggerFactory.getLogger( RepositoryEventListener.class );

    private RepositoryRestoredHandler repositoryRestoredHandler;

    private RepositoryInvalidateByIdHandler repositoryInvalidateByIdHandler;

    private RepositoryRestoreInitializedHandler repositoryRestoreInitializedHandler;

    private ApplicationService applicationService;

    @Activate
    public void initialize()
    {
        this.repositoryRestoredHandler = RepositoryRestoredHandler.create().
            repositoryService( repositoryService ).
            nodeStorageService( storageService ).
            applicationService( applicationService ).
            build();
        this.repositoryInvalidateByIdHandler = new RepositoryInvalidateByIdHandler( repositoryService );
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
        if ( event != null )
        {
            doHandleEvent( event );
        }
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
                if ( !event.isLocalOrigin() )
                {
                    handleEventType( event, repositoryInvalidateByIdHandler );
                }
                break;
            case RepositoryEvents.DELETED_EVENT_TYPE:
                if ( !event.isLocalOrigin() )
                {
                    handleEventType( event, repositoryInvalidateByIdHandler );
                }
                break;
        }
    }

    private void handleEventType( final Event event, final RepositoryEventHandler repositoryEventHandler )
    {
        try
        {
            repositoryEventHandler.handleEvent( event, InternalContext.from( ContextAccessor.current() ) );
        }
        catch ( Exception e )
        {
            LOG.error( "Not able to handle repository-event", e );
        }
    }

    @Reference
    public void setStorageService( final NodeStorageService storageService )
    {
        this.storageService = storageService;
    }

    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }

    @Reference
    public void setApplicationService( final ApplicationService applicationService )
    {
        this.applicationService = applicationService;
    }
}
