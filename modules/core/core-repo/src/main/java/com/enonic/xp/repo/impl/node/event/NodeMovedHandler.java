package com.enonic.xp.repo.impl.node.event;

import java.util.List;
import java.util.Map;

import com.enonic.xp.event.Event2;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.NodeMovedParams;
import com.enonic.xp.repo.impl.storage.StorageService;

public class NodeMovedHandler
    extends AbstractNodeEventHandler
{

    @Override
    public void handleEvent( StorageService storageService, final Event2 event, final InternalContext context )
    {
        final List<Map<Object, Object>> valueMapList = getValueMapList( event );

        for ( final Map<Object, Object> map : valueMapList )

        {
            final NodeMovedParams nodeMovedParams =
                new NodeMovedParams( getPath( map ), NodePath.create( map.get( NEW_PATH ).toString() ).build(), getId( map ) );

            storageService.handleNodeMoved( nodeMovedParams, context );
        }
    }
}
