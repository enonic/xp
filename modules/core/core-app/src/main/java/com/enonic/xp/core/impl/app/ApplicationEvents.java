package com.enonic.xp.core.impl.app;

import com.enonic.xp.event.Event;
import com.enonic.xp.node.Node;

public class ApplicationEvents
{
    final static String APPLICATION_INSTALLED_EVENT = "application.installed";

    static final String NODE_ID_PARAM = "id";

    public static Event installed( final Node applicationNode )
    {
        return Event.create( APPLICATION_INSTALLED_EVENT ).
            distributed( true ).
            value( NODE_ID_PARAM, applicationNode.id() ).
            build();
    }
}
