package com.enonic.xp.repo.impl;

import com.enonic.xp.event.Event;

public class RepositoryEvents
{
    public static final String REPOSITORY_RESTORED_EVENT = "repository.restored";

    public static Event restored()
    {
        return Event.create( REPOSITORY_RESTORED_EVENT ).
            distributed( true ).
            build();
    }
}
