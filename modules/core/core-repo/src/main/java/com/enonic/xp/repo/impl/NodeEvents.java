package com.enonic.xp.repo.impl;

import com.enonic.xp.event.Event2;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.Nodes;

public class NodeEvents
{

    public static final String NODE_MOVED_EVENT = "node.moved";

    public static final String NODE_CREATED_EVENT = "node.created";

    public static final String NODE_DELETED_EVENT = "node.deleted";

    public static final String NODE_PUSHED_EVENT = "node.pushed";

    public static final String NODE_DUPLICATED_EVENT = "node.duplicated";

    public static final String NODE_UPDATED_EVENT = "node.updated";

    public static Event2 moved( final Node from, final Node to )
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

    public static Event2 created( final Node created )
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

    public static Event2 pushed( final Nodes pushedNodes )
    {
        if ( pushedNodes != null && pushedNodes.getSize() > 0 )
        {
            final Event2.Builder builder = Event2.create( NODE_PUSHED_EVENT ).distributed( true );

            addNodeValuesToEventData( builder, pushedNodes );

            return builder.build();
        }
        return null;
    }

    public static Event2 deleted( final Node deleted )
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

    public static Event2 duplicated( final Node duplicated )
    {
        if ( duplicated != null )
        {
            return Event2.create( NODE_DUPLICATED_EVENT ).
                distributed( true ).
                value( "id", duplicated.id() ).
                value( "path", duplicated.path() ).
                build();
        }
        return null;
    }

    public static Event2 updated( final Node updated )
    {
        if ( updated != null )
        {
            return Event2.create( NODE_UPDATED_EVENT ).
                distributed( true ).
                value( "id", updated.id() ).
                value( "path", updated.path() ).
                build();
        }
        return null;
    }

    private static void addNodeValuesToEventData( final Event2.Builder builder, final Nodes nodes )
    {

        final StringBuilder pushedNodesAsString = new StringBuilder();

        for ( final Node node : nodes )
        {
            pushedNodesAsString.append( node.id() ).append( ":" ).append( node.path() ).append( ";" );
        }

        builder.value( "nodes", pushedNodesAsString.toString() );
    }
}
