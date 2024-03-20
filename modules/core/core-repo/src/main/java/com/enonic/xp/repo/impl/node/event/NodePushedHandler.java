package com.enonic.xp.repo.impl.node.event;

import java.util.List;
import java.util.Map;

import com.enonic.xp.event.Event;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.NodeStorageService;

public class NodePushedHandler
    extends AbstractNodeEventHandler
{
    private static final String CURRENT_TARGET_PATH = "currentTargetPath";

    @Override
    public void handleEvent( NodeStorageService storageService, final Event event, final InternalContext context )
    {
        final List<Map<Object, Object>> valueMapList = getValueMapList( event );

        for ( final Map<Object, Object> map : valueMapList )
        {
            final NodePath currentTargetPath =
                map.containsKey( CURRENT_TARGET_PATH ) ? new NodePath( map.get( CURRENT_TARGET_PATH ).toString() ) : null;
            storageService.handleNodePushed( getId( map ), getPath( map ), currentTargetPath, context );
        }
    }
}
