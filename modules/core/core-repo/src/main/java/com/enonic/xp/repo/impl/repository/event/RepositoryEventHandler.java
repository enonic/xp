package com.enonic.xp.repo.impl.repository.event;

import com.enonic.xp.context.InternalContext;
import com.enonic.xp.event.Event;

interface RepositoryEventHandler
{
    void handleEvent( final Event event, final InternalContext context );
}
