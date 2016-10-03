package com.enonic.xp.repo.impl.node.event;

import java.util.List;
import java.util.Map;

import com.enonic.xp.event.Event;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.StorageService;

public class NodeCreatedHandler
    extends AbstractNodeEventHandler
{

    @Override
    public void handleEvent( StorageService storageService, final Event event, final InternalContext context )
    {
        final List<Map<Object, Object>> valueMapList = getValueMapList( event );

        for ( final Map<Object, Object> map : valueMapList )

        {
            final InternalContext nodeContext = InternalContext.create( context ).
                branch( getBranch( map ) ).
                build();
            storageService.handleNodeCreated( getId( map ), getPath( map ), nodeContext );
        }
    }


}
