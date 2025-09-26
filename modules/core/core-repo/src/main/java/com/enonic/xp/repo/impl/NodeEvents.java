package com.enonic.xp.repo.impl;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.event.Event;
import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.PushNodeResult;

import static com.enonic.xp.event.EventConstants.NODES_FIELD;

public final class NodeEvents
{
    public static final String NODE_CREATED_EVENT = "node.created";

    public static final String NODE_DELETED_EVENT = "node.deleted";

    public static final String NODE_PUSHED_EVENT = "node.pushed";

    public static final String NODE_DUPLICATED_EVENT = "node.duplicated";

    public static final String NODE_UPDATED_EVENT = "node.updated";

    public static final String NODE_MOVED_EVENT = "node.moved";

    public static final String NODE_RENAMED_EVENT = "node.renamed";

    public static final String NODE_SORTED_EVENT = "node.sorted";

    public static final String NODE_PERMISSIONS_UPDATED = "node.permissionsUpdated";

    public static Event created( final Node createdNode, final InternalContext internalContext )
    {
        return event( NODE_CREATED_EVENT, createdNode, internalContext );
    }

    public static Event pushed( final Collection<PushNodeResult> pushNodeResults, final InternalContext internalContext )
    {
        return buildEvent( NODE_PUSHED_EVENT, pushNodeResults.stream(), internalContext, n -> createPushed( n, internalContext ) );
    }

    public static Event pushed( final Node node, final InternalContext internalContext )
    {
        return event( NODE_PUSHED_EVENT, node, internalContext );
    }

    public static Event deleted( final NodeBranchEntries deletedNodes, final InternalContext internalContext )
    {
        return buildEvent( NODE_DELETED_EVENT, deletedNodes.stream(), internalContext, node -> nodeToMap( node, internalContext ) );
    }

    public static Event duplicated( final Node duplicatedNode, final InternalContext internalContext )
    {
        return event( NODE_DUPLICATED_EVENT, duplicatedNode, internalContext );
    }

    public static Event updated( final Node updatedNode, final InternalContext internalContext )
    {
        return event( NODE_UPDATED_EVENT, updatedNode, internalContext );
    }

    public static Event patched( final Node patchedNode, final InternalContext internalContext )
    {
        return event( NODE_UPDATED_EVENT, patchedNode, internalContext );
    }

    public static Event permissionsUpdated( final Node updatedNode, final InternalContext internalContext )
    {
        return event( NODE_PERMISSIONS_UPDATED, updatedNode, internalContext );
    }

    public static Event moved( final Collection<MoveNodeResult.MovedNode> movedNodes, final InternalContext internalContext )
    {
        return buildEvent( NODE_MOVED_EVENT, movedNodes.stream(), internalContext, n -> createMoved( n, internalContext ) );
    }

    public static Event renamed( final MoveNodeResult.MovedNode movedNode, final InternalContext internalContext )
    {
        return buildEvent( NODE_RENAMED_EVENT, Stream.of( movedNode ), internalContext, n -> createMoved( n, internalContext ) );
    }

    public static Event sorted( final Node sortedNode, final InternalContext internalContext )
    {
        return event( NODE_SORTED_EVENT, sortedNode, internalContext );
    }

    private static Event event( String type, Node node, InternalContext internalContext )
    {
        return buildEvent( type, Stream.of( node ), internalContext, n -> nodeToMap( n, internalContext ) );
    }

    private static <T> Event buildEvent( String type, Stream<T> nodes, InternalContext internalContext,
                                         Function<T, ImmutableMap<String, String>> nodesMapper )
    {
        final Event.Builder builder = Event.create( type ).distributed( true );
        if ( internalContext.getEventMetadata() != null )
        {
            internalContext.getEventMetadata().forEach( builder::value );
        }
        builder.value( NODES_FIELD, nodes.map( nodesMapper ).collect( ImmutableList.toImmutableList() ) );

        return builder.build();
    }

    private static ImmutableMap<String, String> nodeToMap( final NodeBranchEntry node, final InternalContext internalContext )
    {
        return ImmutableMap.<String, String>builder()
            .put( "id", node.getNodeId().toString() )
            .put( "path", node.getNodePath().toString() )
            .put( "branch", internalContext.getBranch().getValue() )
            .put( "repo", internalContext.getRepositoryId().toString() )
            .build();
    }

    private static ImmutableMap<String, String> nodeToMap( final Node node, final InternalContext internalContext )
    {
        return ImmutableMap.<String, String>builder()
            .put( "id", node.id().toString() )
            .put( "path", node.path().toString() )
            .put( "branch", internalContext.getBranch().getValue() )
            .put( "repo", internalContext.getRepositoryId().toString() )
            .build();
    }

    private static ImmutableMap<String, String> createPushed( final PushNodeResult node, final InternalContext internalContext )
    {
        final ImmutableMap.Builder<String, String> nodeAsMap = ImmutableMap.<String, String>builder()
            .put( "id", node.getNodeId().toString() )
            .put( "path", node.getNodePath().toString() )
            .put( "branch", internalContext.getBranch().getValue() )
            .put( "repo", internalContext.getRepositoryId().toString() );
        if ( node.getTargetPath() != null )
        {
            nodeAsMap.put( "currentTargetPath", node.getTargetPath().toString() );
        }
        return nodeAsMap.build();
    }

    private static ImmutableMap<String, String> createMoved( final MoveNodeResult.MovedNode node, final InternalContext internalContext )
    {
        return ImmutableMap.<String, String>builder()
            .put( "id", node.getNode().id().toString() )
            .put( "path", node.getPreviousPath().toString() )
            .put( "branch", internalContext.getBranch().getValue() )
            .put( "repo", internalContext.getRepositoryId().toString() )
            .put( "newPath", node.getNode().path().toString() )
            .build();
    }
}
