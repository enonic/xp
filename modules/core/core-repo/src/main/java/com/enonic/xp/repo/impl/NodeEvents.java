package com.enonic.xp.repo.impl;

import com.enonic.xp.event.Event2;
import com.enonic.xp.node.Node;

public class NodeEvents
{

    public static final String NODE_MOVED_EVENT = "node.moved";

    public static final String NODE_CREATED_EVENT = "node.created";

    public static final String NODE_DELETED_EVENT = "node.deleted";

    public static Event2 moved( Node from, Node to )
    {
        if ( from != null && to != null )
        {
            return Event2.create( NODE_MOVED_EVENT ).
                distributed( true ).
                value( "id", from.id() ).
                value( "path", from.path() ).
                value( "toId", to.id() ).
                value( "toPath", to.path() ).
                build();
        }
        return null;
    }

    public static Event2 created( Node created )
    {
        if ( created != null )
        {
            return Event2.create( NODE_CREATED_EVENT ).
                distributed( true ).
                value( "id", created.id() ).
                value( "path", created.path() ).
                build();
        }
        return null;
    }

    public static Event2 deleted( Node deleted )
    {
        if ( deleted != null )
        {
            return Event2.create( NODE_DELETED_EVENT ).
                distributed( true ).
                value( "id", deleted.id() ).
                value( "path", deleted.path() ).
                build();
        }
        return null;
    }
}
