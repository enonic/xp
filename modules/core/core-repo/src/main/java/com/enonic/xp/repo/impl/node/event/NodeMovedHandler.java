package com.enonic.xp.repo.impl.node.event;

import java.util.List;
import java.util.Map;

import com.enonic.xp.event.Event;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.NodeMovedParams;
import com.enonic.xp.repo.impl.storage.NodeStorageService;

public class NodeMovedHandler
    extends AbstractNodeEventHandler
{

    @Override
    public void handleEvent( NodeStorageService nodeStorageService, final Event event, final InternalContext context )
    {
        final List<Map<Object, Object>> valueMapList = getValueMapList( event );

        for ( final Map<Object, Object> map : valueMapList )

        {
            final InternalContext nodeContext = createNodeContext( map, context );
            final NodeMovedParams nodeMovedParams =
                new NodeMovedParams( getPath( map ), NodePath.create( map.get( NEW_PATH ).toString() ).build(), getId( map ) );

            nodeStorageService.handleNodeMoved( nodeMovedParams, nodeContext );
        }
    }
}
