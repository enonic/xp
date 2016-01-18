package com.enonic.xp.core.impl.app;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.node.Node;

public class ApplicationEvents
{
    final static String APPLICATION_INSTALLED_EVENT = "application.installed";

    final static String APPLICATION_STATE_CHANGE_EVENT = "application.state";

    static final String NODE_ID_PARAM = "id";

    static final String APPLICATION_KEY_PARAM = "key";

    static final String STARTED_PARAM = "started";

    public static Event installed( final Node applicationNode )
    {
        return Event.create( APPLICATION_INSTALLED_EVENT ).
            distributed( true ).
            value( NODE_ID_PARAM, applicationNode.id() ).
            build();
    }

    public static Event started( final ApplicationKey applicationKey )
    {
        return doCreateStateEvent( applicationKey, true );
    }

    public static Event stopped( final ApplicationKey applicationKey )
    {
        return doCreateStateEvent( applicationKey, false );
    }

    private static Event doCreateStateEvent( final ApplicationKey applicationKey, final boolean value )
    {
        return Event.create( APPLICATION_STATE_CHANGE_EVENT ).
            distributed( true ).
            value( APPLICATION_KEY_PARAM, applicationKey.getName() ).
            value( STARTED_PARAM, value ).
            build();
    }
}
