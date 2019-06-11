package com.enonic.xp.repo.impl.node.event;

import java.util.List;
import java.util.Map;

import com.enonic.xp.context.InternalContext;
import com.enonic.xp.event.Event;
import com.enonic.xp.repo.impl.storage.NodeStorageService;

public class NodeCreatedHandler
    extends AbstractNodeEventHandler
{

    @Override
    public void handleEvent( NodeStorageService nodeStorageService, final Event event, final InternalContext context )
    {
        final List<Map<Object, Object>> valueMapList = getValueMapList( event );

        for ( final Map<Object, Object> map : valueMapList )

        {
            final InternalContext nodeContext = createNodeContext( map, context );
            nodeStorageService.handleNodeCreated( getId( map ), getPath( map ), nodeContext );
        }
    }


}
