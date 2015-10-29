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

    private void doHandleEvent( final Event2 event2 )
    {
        final String type = event2.getType();

        switch ( type )
        {
            case NodeEvents.NODE_CREATED_EVENT:
                handleNodeCreated( event2 );
                break;
        }
    }

    private void handleNodeCreated( final Event2 event2 )
    {
        try
        {
            final NodesEventData nodesEventData = NodesEventData.create( event2 );

            for ( final NodeEventData eventValues : nodesEventData.getNodeEventDataList() )
            {
                this.storageService.handleNodeAdded( eventValues.getNodeId(), eventValues.getNodePath(),
                                                     InternalContext.from( ContextAccessor.current() ) );
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
