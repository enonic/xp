package com.enonic.xp.repo.impl.repository;

import java.util.Optional;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;

@Component(immediate = true)
public class RepositoryClusterEventListener
    implements EventListener
{
    private RepositoryService repositoryService;

    @Override
    public void onEvent( final Event event )
    {
        if ( event != null && !event.isLocalOrigin() )
        {
            doHandleEvent( event );
        }
    }

    private void doHandleEvent( final Event event )
    {
        final String type = event.getType();

        if ( type.equals( RepositoryClusterEvents.EVENT_TYPE ) )
        {

            final String eventSubType = event.getValueAs( String.class, RepositoryClusterEvents.EVENT_TYPE_KEY ).get();

            if ( RepositoryClusterEvents.UPDATED_EVENT_TYPE.equals( eventSubType ) ||
                RepositoryClusterEvents.DELETED_EVENT_TYPE.equals( eventSubType ) )
            {
                final Optional<String> repositoryIdOptional = event.getValueAs( String.class, RepositoryClusterEvents.REPOSITORY_ID_KEY );
                if ( repositoryIdOptional.isPresent() )
                {
                    repositoryService.invalidate( RepositoryId.from( repositoryIdOptional.get() ) );
                }
            }
        }
    }

    @Reference
    public void setRepositoryService( final RepositoryService repositoryService )
    {
        this.repositoryService = repositoryService;
    }
}
