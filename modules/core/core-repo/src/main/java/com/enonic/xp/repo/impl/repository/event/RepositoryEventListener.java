package com.enonic.xp.repo.impl.repository.event;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.repo.impl.RepositoryEvents;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.internal.InternalRepositoryService;

@Component(immediate = true)
public class RepositoryEventListener
    implements EventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( RepositoryEventListener.class );

    private final InternalRepositoryService repositoryService;

    @Activate
    public RepositoryEventListener( @Reference final InternalRepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }

    @Override
    public int getOrder()
    {
        return 0;
    }

    @Override
    public void onEvent( final Event event )
    {
        try
        {
            doHandleEvent( event );
        }
        catch ( Exception e )
        {
            LOG.error( "Not able to handle repository-event", e );
        }
    }

    private void doHandleEvent( final Event event )
    {
        switch ( event.getType() )
        {
            case RepositoryEvents.RESTORED_EVENT_TYPE:
            case RepositoryEvents.RESTORE_INITIALIZED_EVENT_TYPE:
                repositoryService.invalidateAll();
                break;
            case RepositoryEvents.UPDATED_EVENT_TYPE:
            case RepositoryEvents.DELETED_EVENT_TYPE:
                if ( !event.isLocalOrigin() )
                {
                    event.getValueAs( String.class, RepositoryEvents.REPOSITORY_ID_KEY ).map( RepositoryId::from ).ifPresent( this.repositoryService::invalidate );
                }
                break;
            default:
                break;
        }
    }
}
