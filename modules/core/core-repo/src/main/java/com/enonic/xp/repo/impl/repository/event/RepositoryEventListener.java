package com.enonic.xp.repo.impl.repository.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.InternalContext;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.repo.impl.RepositoryEvents;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.RepositoryService;

public class RepositoryEventListener
    implements EventListener
{
    private final static Logger LOG = LoggerFactory.getLogger( RepositoryEventListener.class );

    private RepositoryRestoredHandler repositoryRestoredHandler;

    private RepositoryInvalidateByIdHandler repositoryInvalidateByIdHandler;

    private RepositoryRestoreInitializedHandler repositoryRestoreInitializedHandler;

    private RepositoryEventListener( final Builder builder )
    {
        this.repositoryRestoredHandler = RepositoryRestoredHandler.create().
            repositoryService( builder.repositoryService ).
            nodeStorageService( builder.storageService ).
            build();
        this.repositoryInvalidateByIdHandler = new RepositoryInvalidateByIdHandler( builder.repositoryService, builder.storageService );
        this.repositoryRestoreInitializedHandler =
            new RepositoryRestoreInitializedHandler( builder.repositoryService, builder.storageService );
    }

    public static Builder create()
    {
        return new Builder();
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

    public static final class Builder
    {
        private NodeStorageService storageService;

        private RepositoryService repositoryService;

        private Builder()
        {
        }

        public Builder storageService( final NodeStorageService storageService )
        {
            this.storageService = storageService;
            return this;
        }

        public Builder repositoryService( final RepositoryService repositoryService )
        {
            this.repositoryService = repositoryService;
            return this;
        }

        public RepositoryEventListener build()
        {
            return new RepositoryEventListener( this );
        }
    }
}
