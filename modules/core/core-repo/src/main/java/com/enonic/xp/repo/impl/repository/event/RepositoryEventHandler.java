package com.enonic.xp.repo.impl.repository.event;

import com.enonic.xp.event.Event;

interface RepositoryEventHandler
{
    void handleEvent( Event event );
}
