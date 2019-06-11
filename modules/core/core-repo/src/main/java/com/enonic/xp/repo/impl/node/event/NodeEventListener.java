package com.enonic.xp.repo.impl.node.event;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.InternalContext;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.repo.impl.NodeEvents;
import com.enonic.xp.repo.impl.storage.NodeStorageService;

@Component(immediate = true)
public class NodeEventListener
    implements EventListener
{
    private NodeStorageService nodeStorageService;

    private final static Logger LOG = LoggerFactory.getLogger( NodeEventListener.class );

    private final NodeCreatedHandler nodeCreatedHandler = new NodeCreatedHandler();

    private final NodeDeletedHandler nodeDeletedHandler = new NodeDeletedHandler();

    private final NodeMovedHandler nodeMovedHandler = new NodeMovedHandler();

    private final NodePushedHandler nodePushedHandler = new NodePushedHandler();

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
            case NodeEvents.NODE_CREATED_EVENT:
                handleEventType( event, nodeCreatedHandler );
                break;
            case NodeEvents.NODE_DELETED_EVENT:
                handleEventType( event, nodeDeletedHandler );
                break;
            case NodeEvents.NODE_MOVED_EVENT:
                handleEventType( event, nodeMovedHandler );
                break;
            case NodeEvents.NODE_RENAMED_EVENT:
                handleEventType( event, nodeMovedHandler );
                break;
            case NodeEvents.NODE_DUPLICATED_EVENT:
                handleEventType( event, nodeCreatedHandler );
                break;
            case NodeEvents.NODE_PUSHED_EVENT:
                handleEventType( event, nodePushedHandler );
                break;
        }
    }

    private void handleEventType( final Event event, final NodeEventHandler nodeEventHandler )
    {
        try
        {
            nodeEventHandler.handleEvent( this.nodeStorageService, event, InternalContext.from( ContextAccessor.current() ) );
        }
        catch ( Exception e )
        {
            LOG.error( "Not able to handle node-event", e );
        }
    }

    @Reference
    public void setNodeStorageService( final NodeStorageService nodeStorageService )
    {
        this.nodeStorageService = nodeStorageService;
    }
}
