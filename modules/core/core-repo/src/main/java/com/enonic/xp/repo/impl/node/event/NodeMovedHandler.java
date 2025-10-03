package com.enonic.xp.repo.impl.node.event;

import com.enonic.xp.event.Event;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.storage.NodeStorageService;

public class NodeMovedHandler
    extends AbstractNodeEventHandler
{
    @Override
    public void handleEvent( NodeStorageService nodeStorageService, final Event event, final InternalContext context )
    {
        getValueMapList( event ).stream().map( this::getPath ).forEach( path -> nodeStorageService.invalidatePath( path, context ) );
    }
}
