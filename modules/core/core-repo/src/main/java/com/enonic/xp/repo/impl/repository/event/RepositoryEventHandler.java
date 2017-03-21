package com.enonic.xp.repo.impl.repository.event;

import com.enonic.xp.event.Event;
import com.enonic.xp.repo.impl.InternalContext;

interface RepositoryEventHandler
{
    void handleEvent( final Event event, final InternalContext context );
}
