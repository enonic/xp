package com.enonic.xp.core.impl.app.event;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.node.NodeId;

public class ApplicationClusterEvents
{
    public static final String EVENT_TYPE = "application.cluster";

    public static final String EVENT_TYPE_KEY = "eventType";

    public static final String INSTALL = "install";

    public static final String INSTALLED = "installed";

    public static final String START = "start";

    public static final String STOP = "stop";

    public static final String UNINSTALL = "uninstall";

    public static final String UNINSTALLED = "uninstalled";

    public static final String STATE_CHANGE = "state";

    public static final String NODE_ID_PARAM = "id";

    public static final String APPLICATION_KEY_PARAM = "key";

    public static final String STARTED_PARAM = "started";

    public static Event install( final ApplicationKey applicationKey, final NodeId nodeId )
    {
        return Event.create( EVENT_TYPE ).
            distributed( true ).
            value( EVENT_TYPE_KEY, INSTALL ).
            value( NODE_ID_PARAM, nodeId.toString() ).
            value( APPLICATION_KEY_PARAM, applicationKey.getName() ).
            build();
    }

    public static Event installed( final ApplicationKey applicationKey, final NodeId nodeId )
    {
        return Event.create( EVENT_TYPE ).
            distributed( true ).
            value( EVENT_TYPE_KEY, INSTALLED ).
            value( NODE_ID_PARAM, nodeId.toString() ).
            value( APPLICATION_KEY_PARAM, applicationKey.getName() ).
            build();
    }

    public static Event uninstalled( final ApplicationKey applicationKey )
    {
        return doCreateEvent( applicationKey, UNINSTALLED );
    }

    public static Event uninstall( final ApplicationKey applicationKey )
    {
        return doCreateEvent( applicationKey, UNINSTALL );
    }

    public static Event start( final ApplicationKey applicationKey )
    {
        return doCreateEvent( applicationKey, START );
    }

    public static Event stop( final ApplicationKey applicationKey )
    {
        return doCreateEvent( applicationKey, STOP );
    }

    public static Event started( final ApplicationKey applicationKey )
    {
        return doCreateStateEvent( applicationKey, true );
    }

    public static Event stopped( final ApplicationKey applicationKey )
    {
        return doCreateStateEvent( applicationKey, false );
    }

    private static Event doCreateEvent( final ApplicationKey applicationKey, final String eventType )
    {
        return Event.create( EVENT_TYPE ).
            distributed( true ).
            value( EVENT_TYPE_KEY, eventType ).
            value( APPLICATION_KEY_PARAM, applicationKey.getName() ).
            build();
    }

    private static Event doCreateStateEvent( final ApplicationKey applicationKey, final boolean value )
    {
        return Event.create( EVENT_TYPE ).
            distributed( true ).
            value( EVENT_TYPE_KEY, STATE_CHANGE ).
            value( APPLICATION_KEY_PARAM, applicationKey.getName() ).
            value( STARTED_PARAM, value ).
            build();
    }
}
