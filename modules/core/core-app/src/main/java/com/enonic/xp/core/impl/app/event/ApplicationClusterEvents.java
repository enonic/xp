package com.enonic.xp.core.impl.app.event;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.node.Node;

public class ApplicationClusterEvents
{
    public final static String EVENT_TYPE = "application.cluster";

    public static final String EVENT_TYPE_KEY = "eventType";

    public final static String INSTALLED = "installed";

    public final static String UNINSTALLED = "uninstalled";

    public final static String STATE_CHANGE = "state";

    public static final String NODE_ID_PARAM = "id";

    public static final String APPLICATION_KEY_PARAM = "key";

    public static final String STARTED_PARAM = "started";

    public static Event installed( final Node applicationNode )
    {
        return Event.create( EVENT_TYPE ).
            distributed( true ).
            value( EVENT_TYPE_KEY, INSTALLED ).
            value( NODE_ID_PARAM, applicationNode.id() ).
            build();
    }

    public static Event uninstalled( final ApplicationKey applicationKey )
    {
        return Event.create( EVENT_TYPE ).
            distributed( true ).
            value( EVENT_TYPE_KEY, UNINSTALLED ).
            value( APPLICATION_KEY_PARAM, applicationKey.getName() ).
            build();
    }

    public static Event started( final ApplicationKey applicationKey )
    {
        return doCreateStateEvent( applicationKey, STATE_CHANGE, true );
    }

    public static Event stopped( final ApplicationKey applicationKey )
    {
        return doCreateStateEvent( applicationKey, STATE_CHANGE, false );
    }

    private static Event doCreateStateEvent( final ApplicationKey applicationKey, final String eventType, final boolean value )
    {
        return Event.create( EVENT_TYPE ).
            distributed( true ).
            value( EVENT_TYPE_KEY, eventType ).
            value( APPLICATION_KEY_PARAM, applicationKey.getName() ).
            value( STARTED_PARAM, value ).
            build();
    }
}
