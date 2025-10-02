package com.enonic.xp.repo.impl.node.event;

import java.util.List;
import java.util.Map;

import com.enonic.xp.event.Event;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.NodeStorageService;

public class NodeMovedHandler
    extends AbstractNodeEventHandler
{
    private static final String NEW_PATH = "newPath";

    @Override
    public void handleEvent( NodeStorageService nodeStorageService, final Event event, final InternalContext context )
    {
        final List<Map<Object, Object>> valueMapList = getValueMapList( event );

        for ( final Map<Object, Object> map : valueMapList )

        {
            nodeStorageService.handleNodeMoved( getPath( map ), context );
        }
    }
}
