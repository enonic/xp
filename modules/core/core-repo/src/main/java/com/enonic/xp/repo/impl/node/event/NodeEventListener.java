package com.enonic.xp.repo.impl.node.event;

import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.event.Event;
import com.enonic.xp.event.EventListener;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeEvents;
import com.enonic.xp.repo.impl.storage.NodeStorageService;
import com.enonic.xp.repository.RepositoryId;

@Component(immediate = true)
public class NodeEventListener
    implements EventListener
{
    private static final String BRANCH = "branch";

    private static final String REPOSITORY_ID = "repo";

    private NodeStorageService nodeStorageService;

    private static final Logger LOG = LoggerFactory.getLogger( NodeEventListener.class );

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
            default:
                break;
        }
    }

    private void handleEventType( final Event event, final NodeEventHandler nodeEventHandler )
    {
        try
        {
            nodeEventHandler.handleEvent( this.nodeStorageService, event, createNodeContext( event.getData() ) );
        }
        catch ( Exception e )
        {
            LOG.error( "Not able to handle node-event", e );
        }
    }

    private InternalContext createNodeContext( final Map<String, Object> map )
    {
        final InternalContext.Builder nodeContext = InternalContext.create( ContextAccessor.current() );

        final RepositoryId repositoryId = getRepositoryId( map );
        if ( repositoryId != null )
        {
            nodeContext.repositoryId( repositoryId );
        }

        final Branch branch = getBranch( map );
        if ( branch != null )
        {
            nodeContext.branch( branch );
        }
        return nodeContext.build();
    }


    private RepositoryId getRepositoryId( final Map<String, Object> map )
    {
        return map.containsKey( REPOSITORY_ID ) ? RepositoryId.from( map.get( REPOSITORY_ID ).toString() ) : null;
    }

    private Branch getBranch( final Map<String, Object> map )
    {
        return map.containsKey( BRANCH ) ? Branch.from( map.get( BRANCH ).toString() ) : null;
    }

    @Reference
    public void setNodeStorageService( final NodeStorageService nodeStorageService )
    {
        this.nodeStorageService = nodeStorageService;
    }
}
