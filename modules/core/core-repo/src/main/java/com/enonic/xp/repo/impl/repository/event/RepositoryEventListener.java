package com.enonic.xp.repo.impl.repository.event;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.RepositoryEvents;
import com.enonic.xp.repo.impl.storage.StorageService;

@Component(immediate = true)
public class RepositoryEventListener
    implements EventListener
{
    private StorageService storageService;

    private final static Logger LOG = LoggerFactory.getLogger( RepositoryEventListener.class );

    private final RepositoryRestoredHandler repositoryRestoredHandler = new RepositoryRestoredHandler();

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
            if ( event.isLocalOrigin() )
            {
                return;
            }

            doHandleEvent( event );
        }

    }

    private void doHandleEvent( final Event event )
    {
        final String type = event.getType();

        switch ( type )
        {
            case RepositoryEvents.REPOSITORY_RESTORED_EVENT:
                handleEventType( event, repositoryRestoredHandler );
                break;
        }
    }

    private void handleEventType( final Event event, final RepositoryEventHandler repositoryEventHandler )
    {
        try
        {
            repositoryEventHandler.handleEvent( this.storageService, event, InternalContext.from( ContextAccessor.current() ) );
        }
        catch ( Exception e )
        {
            LOG.error( "Not able to handle repository-event", e );
        }
    }

    @Reference
    public void setStorageService( final StorageService storageService )
    {
        this.storageService = storageService;
    }
}
