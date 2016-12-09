package com.enonic.xp.repo.impl.repository;

import com.enonic.xp.event.Event;
import com.enonic.xp.repository.RepositoryId;

public class RepositoryClusterEvents
{
    public final static String EVENT_TYPE = "repository.cluster";

    public static final String EVENT_TYPE_KEY = "eventType";

    public static final String REPOSITORY_ID_KEY = "id";

    public final static String CREATED_EVENT_TYPE = "created";

    public final static String UPDATED_EVENT_TYPE = "updated";

    public final static String DELETED_EVENT_TYPE = "deleted";

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
        return Event.create( EVENT_TYPE ).
            distributed( true ).
            value( EVENT_TYPE_KEY, eventType ).
            value( REPOSITORY_ID_KEY, repositoryId.toString() ).
            localOrigin( true ).
            build();
    }
}
