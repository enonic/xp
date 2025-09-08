package com.enonic.xp.repo.impl;

import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.event.Event;
import com.enonic.xp.node.MoveNodeResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.PushNodeEntry;
import com.enonic.xp.repository.RepositoryId;

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

    public static final String NODE_PERMISSIONS_UPDATED = "node.permissionsUpdated";

    public static Event created( final Node createdNode, final InternalContext internalContext )
    {
        return event( NODE_CREATED_EVENT, createdNode, internalContext );
    }

    public static Event pushed( final Collection<PushNodeEntry> pushNodeEntries, Branch targetBranch, RepositoryId repositoryId )
    {
        return event( NODE_PUSHED_EVENT, pushNodeEntries, targetBranch, repositoryId ).build();
    }

    public static Event pushed( final Node node, final InternalContext internalContext )
    {
        return event( NODE_PUSHED_EVENT, node, internalContext );
    }

    public static Event deleted( final NodeBranchEntries deletedNodes, final InternalContext internalContext )
    {
        return event( NODE_DELETED_EVENT, deletedNodes, internalContext ).build();
    }

    public static Event duplicated( final Node duplicatedNode, final InternalContext internalContext )
    {
        return event( NODE_DUPLICATED_EVENT, duplicatedNode, internalContext );
    }

    public static Event updated( final Node updatedNode, final InternalContext internalContext )
    {
        return event( NODE_UPDATED_EVENT, updatedNode, internalContext );
    }

    public static Event permissionsUpdated( final Node updatedNode, final InternalContext internalContext )
    {
        return event( NODE_PERMISSIONS_UPDATED, updatedNode, internalContext );
    }

    public static Event moved( final MoveNodeResult result, final InternalContext internalContext )
    {
        return Event.create( NODE_MOVED_EVENT )
            .distributed( true )
            .value( "nodes", result.getMovedNodes()
                .stream()
                .map( movedNode -> createMoved( movedNode.getPreviousPath(), movedNode.getNode(), internalContext ) )
                .collect( Collectors.toList() ) )
            .build();
    }

    public static Event renamed( final NodePath previousPath, final Node targetNode, final InternalContext internalContext )
    {
        return Event.create( NODE_RENAMED_EVENT )
            .distributed( true )
            .value( "nodes", ImmutableList.of( createMoved( previousPath, targetNode, internalContext ) ) )
            .build();
    }

    public static Event sorted( final Node sortedNode, final InternalContext internalContext )
    {
        return event( NODE_SORTED_EVENT, sortedNode, internalContext );
    }

    private static Event event( String type, Node node, InternalContext internalContext )
    {
        if ( node != null )
        {
            return event( type, Nodes.from( node ), internalContext ).build();
        }
        return null;
    }

    private static Event.Builder event( String type, Nodes nodes, InternalContext internalContext )
    {
        return Event.create( type ).distributed( true ).value( "nodes", nodesToList( nodes, internalContext ) );
    }

    private static Event.Builder event( String type, NodeBranchEntries nodes, InternalContext internalContext )
    {
        return Event.create( type ).distributed( true ).value( "nodes", nodesToList( nodes, internalContext ) );
    }

    private static Event.Builder event( String type, Collection<PushNodeEntry> nodes, Branch targetBranch, RepositoryId repositoryId )
    {
        return Event.create( type ).distributed( true ).value( "nodes", nodesToList( nodes, targetBranch, repositoryId ) );
    }

    private static ImmutableList<ImmutableMap<String, String>> nodesToList( final Nodes nodes, final InternalContext internalContext )
    {
        return nodes.stream().map( node -> NodeEvents.nodeToMap( node, internalContext ) ).collect( ImmutableList.toImmutableList() );
    }

    private static ImmutableList<ImmutableMap<String, String>> nodesToList( final NodeBranchEntries nodes,
                                                                            final InternalContext internalContext )
    {
        return nodes.stream().map( node -> NodeEvents.nodeToMap( node, internalContext ) ).collect( ImmutableList.toImmutableList() );
    }

    private static ImmutableList<ImmutableMap<String, String>> nodesToList( final Collection<PushNodeEntry> pushNodeEntries,
                                                                            Branch targetBranch, RepositoryId repositoryId )
    {
        return pushNodeEntries.stream()
            .map( node -> NodeEvents.nodeToMap( node, targetBranch, repositoryId ) )
            .collect( ImmutableList.toImmutableList() );
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

    private static ImmutableMap<String, String> nodeToMap( final PushNodeEntry node, final Branch targetBranch,
                                                           final RepositoryId repositoryId )
    {
        final ImmutableMap.Builder<String, String> nodeAsMap = ImmutableMap.<String, String>builder()
            .put( "id", node.getNodeBranchEntry().getNodeId().toString() )
            .put( "path", node.getNodeBranchEntry().getNodePath().toString() )
            .put( "branch", targetBranch.getValue() )
            .put( "repo", repositoryId.toString() );
        if ( node.getCurrentTargetPath() != null )
        {
            nodeAsMap.put( "currentTargetPath", node.getCurrentTargetPath().toString() );
        }
        return nodeAsMap.build();
    }

    private static ImmutableMap<String, String> createMoved( final NodePath previousPath, final Node targetNode,
                                                             final InternalContext internalContext )
    {
        return ImmutableMap.<String, String>builder()
            .put( "id", targetNode.id().toString() )
            .put( "path", previousPath.toString() )
            .put( "branch", internalContext.getBranch().getValue() )
            .put( "repo", internalContext.getRepositoryId().toString() )
            .put( "newPath", targetNode.path().toString() )
            .build();
    }
}
