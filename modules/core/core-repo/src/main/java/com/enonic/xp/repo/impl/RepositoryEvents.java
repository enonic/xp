package com.enonic.xp.repo.impl;

import com.enonic.xp.event.Event;
import com.enonic.xp.repository.RepositoryId;

public class RepositoryEvents
{
    public static final String REPOSITORY_ID_KEY = "id";

    public final static String RESTORED_EVENT_TYPE = "repository.restored";

    public final static String RESTORE_INITIALIZED_EVENT_TYPE = "repository.restoreInitialized";

    public final static String CREATED_EVENT_TYPE = "repository.created";

    public final static String UPDATED_EVENT_TYPE = "repository.updated";

    public final static String DELETED_EVENT_TYPE = "repository.deleted";

    public static Event created( final RepositoryId repositoryId )
    {
        return doCreateStateEvent( repositoryId, CREATED_EVENT_TYPE );
    }

    public static Event updated( final RepositoryId repositoryId )
    {
        return doCreateStateEvent( repositoryId, UPDATED_EVENT_TYPE );
    }

    public static Event deleted( final RepositoryId repositoryId )
    {
        return doCreateStateEvent( repositoryId, DELETED_EVENT_TYPE );
    }

    private static Event doCreateStateEvent( final RepositoryId repositoryId, final String eventType )
    {
        return Event.create( eventType ).
            distributed( true ).
            value( REPOSITORY_ID_KEY, repositoryId.toString() ).
            localOrigin( true ).
            build();
    }

    public static Event restored()
    {
        return Event.create( RESTORED_EVENT_TYPE ).
            distributed( true ).
            localOrigin( true ).
            build();
    }

    public static Event restoreInitialized()
    {
        return Event.create( RESTORE_INITIALIZED_EVENT_TYPE ).
            distributed( true ).
            localOrigin( true ).
            build();
    }
}
