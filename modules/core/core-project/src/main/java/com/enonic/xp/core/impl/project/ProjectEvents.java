package com.enonic.xp.core.impl.project;

import com.enonic.xp.event.Event;
import com.enonic.xp.project.ProjectName;

public class ProjectEvents
{
    public static final String PROJECT_NAME_KEY = "name";

    public final static String CREATED_EVENT_TYPE = "project.created";

    public static Event created( final ProjectName projectName )
    {
        return doCreateStateEvent( projectName, CREATED_EVENT_TYPE );
    }

    private static Event doCreateStateEvent( final ProjectName projectName, final String type )
    {
        return Event.create( type ).
            distributed( true ).
            value( PROJECT_NAME_KEY, projectName.toString() ).
            localOrigin( true ).
            build();
    }
}
