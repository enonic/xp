package com.enonic.xp.repo.impl.node.event;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.Event2;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeEvents;
import com.enonic.xp.repo.impl.storage.StorageService;

@Component(immediate = true)
public class NodeEventListener
    implements EventListener
{
    private StorageService storageService;

    private final static Logger LOG = LoggerFactory.getLogger( NodeEventListener.class );

    private NodeCreatedHandler nodeCreatedHandler = new NodeCreatedHandler();

    private NodeDeletedHandler nodeDeletedHandler = new NodeDeletedHandler();

    @Override
    public int getOrder()
    {
        return 0;
    }

    @Override
    public void onEvent( final Event event )
    {
        if ( event instanceof Event2 )
        {
            doHandleEvent( (Event2) event );
        }

    }

    private void doHandleEvent( final Event2 event )
    {
        final String type = event.getType();

        switch ( type )
        {
            case NodeEvents.NODE_CREATED_EVENT:
                handleEventType( event, nodeCreatedHandler );
                break;
            case NodeEvents.NODE_DELETED_EVENT:
                handleEventType( event, nodeDeletedHandler );
                break;
        }
    }

    private void handleEventType( final Event2 event, final NodeEventHandler nodeEventHandler )
    {
        try
        {
            final NodesEventData nodesEventData = NodesEventData.create( event );

            for ( final NodeEventData eventValues : nodesEventData.getNodeEventDataList() )
            {
                nodeEventHandler.handleEvent( this.storageService, eventValues, InternalContext.from( ContextAccessor.current() ) );
            }
        }
        catch ( Exception e )
        {
            LOG.error( "Not able to handle node-event", e );
        }
    }

    @Reference
    public void setStorageService( final StorageService storageService )
    {
        this.storageService = storageService;
    }
}
