package com.enonic.xp.repo.impl;

import com.enonic.xp.event.Event2;
import com.enonic.xp.node.Node;

public class NodeEvents
{

    public static final String NODE_MOVED_EVENT = "node.moved";

    public static final String NODE_CREATED_EVENT = "node.created";

    public static Event2 moved( Node from, Node to )
    {
        return Event2.create( NODE_MOVED_EVENT ).
            distributed( true ).
            value( "id", from.id() ).
            value( "path", from.path() ).
            value( "toId", to.id() ).
            value( "toPath", to.path() ).
            build();
    }

    public static Event2 created( Node created )
    {
        return Event2.create( NODE_CREATED_EVENT ).
            distributed( true ).
            value( "id", created.id() ).
            value( "path", created.path() ).
            build();
    }
}
