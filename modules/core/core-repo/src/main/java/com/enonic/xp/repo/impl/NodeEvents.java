package com.enonic.xp.repo.impl;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.event.Event;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.Nodes;

public class NodeEvents
{

    public static final String NODE_CREATED_EVENT = "node.created";

    public static final String NODE_DELETED_EVENT = "node.deleted";

    public static final String NODE_PUSHED_EVENT = "node.pushed";

    public static final String NODE_DUPLICATED_EVENT = "node.duplicated";

    public static final String NODE_UPDATED_EVENT = "node.updated";

    public static final String NODE_MOVED_EVENT = "node.moved";

    public static final String NODE_RENAMED_EVENT = "node.renamed";

    public static final String NODE_SORTED_EVENT = "node.sorted";

    public static final String NODE_STATE_UPDATED_EVENT = "node.stateUpdated";

    public static Event created( final Node createdNode )
    {
        return event( NODE_CREATED_EVENT, createdNode );
    }

    public static Event pushed( final Nodes pushedNodes )
    {
        if ( pushedNodes != null )
        {
            return event( NODE_PUSHED_EVENT, pushedNodes ).
                build();
        }
        return null;
    }

    public static Event stateUpdated( final Nodes updatedNodes )
    {
        if ( updatedNodes != null )
        {
            return event( NODE_STATE_UPDATED_EVENT, updatedNodes ).
                value( "state", updatedNodes.first().getNodeState().toString() ).
                build();
        }
        return null;
    }

    public static Event deleted( final Node deletedNode )
    {
        return event( NODE_DELETED_EVENT, deletedNode );
    }

    public static Event duplicated( final Node duplicatedNode )
    {
        return event( NODE_DUPLICATED_EVENT, duplicatedNode );
    }

    public static Event updated( final Node updatedNode )
    {
        return event( NODE_UPDATED_EVENT, updatedNode );
    }

    public static Event moved( final Node sourceNode, final Node targetNode )
    {
        final ImmutableMap<Object, Object> node = ImmutableMap.builder().
            put( "id", sourceNode.id().toString() ).
            put( "path", sourceNode.path().toString() ).
            put( "newPath", targetNode.path().toString() ).
            build();

        return Event.create( NODE_MOVED_EVENT ).
            distributed( true ).
            value( "nodes", ImmutableList.of( node ) ).
            build();
    }

    public static Event renamed( final Node sourceNode, final Node targetNode )
    {
        final ImmutableMap<Object, Object> node = ImmutableMap.builder().
            put( "id", sourceNode.id().toString() ).
            put( "path", sourceNode.path().toString() ).
            put( "newPath", targetNode.path().toString() ).
            build();

        return Event.create( NODE_RENAMED_EVENT ).
            distributed( true ).
            value( "nodes", ImmutableList.of( node ) ).
            build();
    }

    public static Event sorted( final Node sortedNode )
    {
        return event( NODE_SORTED_EVENT, sortedNode );
    }

    private static Event event( String type, Node node )
    {
        if ( node != null )
        {
            return event( type, Nodes.from( node ) ).build();
        }
        return null;
    }

    private static Event.Builder event( String type, Nodes nodes )
    {
        return Event.create( type ).
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
            put( "id", node.id().toString() ).
            put( "path", node.path().toString() ).
            build();


    }
}
