package com.enonic.xp.repo.impl;

import com.enonic.xp.event.Event2;
import com.enonic.xp.node.Node;

public class NodeEvents
{

    public static final String NODE_MOVED_EVENT = "Node moved";

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
}
