package com.enonic.xp.repo.impl;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.event.Event2;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.Nodes;

public class NodeEvents
{

    public static final String NODE_CREATED_EVENT = "node.created";

    public static final String NODE_DELETED_EVENT = "node.deleted";

    public static final String NODE_PUSHED_EVENT = "node.pushed";

    public static final String NODE_DUPLICATED_EVENT = "node.duplicated";

    public static final String NODE_UPDATED_EVENT = "node.updated";

    public static final String NODE_RENAMED_EVENT = "node.renamed";

    public static final String NODE_SORTED_EVENT = "node.sorted";

    public static final String NODE_STATE_UPDATED_EVENT = "node.stateUpdated";

    public static Event2 created( final Node createdNode )
    {
        return event( NODE_CREATED_EVENT, createdNode );
    }

    public static Event2 pushed( final Nodes pushedNodes )
    {
        if ( pushedNodes != null )
        {
            return event( NODE_PUSHED_EVENT, pushedNodes ).
                build();
        }
        return null;
    }

    public static Event2 stateUpdated( final Nodes updatedNodes )
    {
        if ( updatedNodes != null )
        {
            return event( NODE_STATE_UPDATED_EVENT, updatedNodes ).
                value( "state", updatedNodes.first().getNodeState().toString() ).
                build();
        }
        return null;
    }

    public static Event2 deleted( final Node deletedNode )
    {
        return event( NODE_DELETED_EVENT, deletedNode );
    }

    public static Event2 duplicated( final Node duplicatedNode )
    {
        return event( NODE_DUPLICATED_EVENT, duplicatedNode );
    }

    public static Event2 updated( final Node updatedNode )
    {
        return event( NODE_UPDATED_EVENT, updatedNode );
    }

    public static Event2 renamed( final Node renamedNode )
    {
        return event( NODE_RENAMED_EVENT, renamedNode );
    }

    public static Event2 sorted( final Node sortedNode )
    {
        return event( NODE_SORTED_EVENT, sortedNode );
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

    private static Event2 event( String type, Node node )
    {
        if ( node != null )
        {
            return event( type, Nodes.from( node ) ).build();
        }
        return null;
    }

    private static Event2.Builder event( String type, Nodes nodes )
    {
        return Event2.create( type ).
            distributed( true ).
            value( "nodes", nodesToList( nodes ) );
    }

    private static ImmutableList nodesToList( final Nodes nodes )
    {
        List<ImmutableMap> list = new LinkedList<>();
        nodes.stream().
            map( NodeEvents::nodeToMap ).
            forEach( list::add );

        return ImmutableList.copyOf( list );
    }

    private static ImmutableMap nodeToMap( final Node node )
    {
        return ImmutableMap.builder().
            put( "id", node.id() ).
            put( "path", node.path() ).
            build();


    }
}
