package com.enonic.xp.repo.impl;

import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
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

    public static final String NODE_MANUAL_ORDER_EVENT = "node.manualOrderUpdated";

    @Deprecated
    public static final String NODE_STATE_UPDATED_EVENT = "node.stateUpdated";

    public static final String NODE_PERMISSIONS_UPDATED = "node.permissionsUpdated";

    public static Event created( final Node createdNode )
    {
        return event( NODE_CREATED_EVENT, createdNode );
    }

    public static Event pushed( final Collection<PushNodeEntry> pushNodeEntries, Branch targetBranch )
    {
        return event( NODE_PUSHED_EVENT, pushNodeEntries, targetBranch ).build();
    }

    public static Event pushed( final Node node )
    {
        return event( NODE_PUSHED_EVENT, node );
    }

    public static Event deleted( final NodeBranchEntries deletedNodes )
    {
        return event( NODE_DELETED_EVENT, deletedNodes ).build();
    }

    public static Event duplicated( final Node duplicatedNode )
    {
        return event( NODE_DUPLICATED_EVENT, duplicatedNode );
    }

    public static Event updated( final Node updatedNode )
    {
        return event( NODE_UPDATED_EVENT, updatedNode );
    }

    public static Event permissionsUpdated( final Node updatedNode )
    {
        return event( NODE_PERMISSIONS_UPDATED, updatedNode );
    }

    public static Event moved( final MoveNodeResult result )
    {
        return Event.create( NODE_MOVED_EVENT )
            .distributed( true )
            .value( "nodes", result.getMovedNodes()
                .stream()
                .map( movedNode -> createMoved( movedNode.getPreviousPath(), movedNode.getNode() ) )
                .collect( Collectors.toList() ) )
            .build();
    }

    public static Event renamed( final NodePath previousPath, final Node targetNode )
    {
        return Event.create( NODE_RENAMED_EVENT )
            .distributed( true )
            .value( "nodes", ImmutableList.of( createMoved( previousPath, targetNode ) ) )
            .build();
    }

    public static Event sorted( final Node sortedNode )
    {
        return event( NODE_SORTED_EVENT, sortedNode );
    }

    public static Event manualOrderUpdated( final Node sortedNode )
    {
        return event( NODE_MANUAL_ORDER_EVENT, sortedNode );
    }

    private static Event event( String type, Node node )
    {
        if ( node != null )
        {
            return event( type, Nodes.from( node ), ContextAccessor.current().getBranch() ).build();
        }
        return null;
    }

    private static Event.Builder event( String type, Nodes nodes, Branch branch )
    {
        return Event.create( type ).distributed( true ).value( "nodes", nodesToList( nodes, branch ) );
    }

    private static Event.Builder event( String type, NodeBranchEntries nodes )
    {
        return Event.create( type ).distributed( true ).value( "nodes", nodesToList( nodes ) );
    }

    private static Event.Builder event( String type, Collection<PushNodeEntry> nodes, Branch targetBranch )
    {
        return Event.create( type ).distributed( true ).value( "nodes", nodesToList( nodes, targetBranch ) );
    }

    private static ImmutableList<ImmutableMap<String, String>> nodesToList( final Nodes nodes, final Branch branch )
    {
        return nodes.stream().map( node -> NodeEvents.nodeToMap( node, branch ) ).collect( ImmutableList.toImmutableList() );
    }

    private static ImmutableList<ImmutableMap<String, String>> nodesToList( final NodeBranchEntries nodes )
    {
        return nodes.stream().map( NodeEvents::nodeToMap ).collect( ImmutableList.toImmutableList() );
    }

    private static ImmutableList<ImmutableMap<String, String>> nodesToList( final Collection<PushNodeEntry> pushNodeEntries,
                                                                            Branch targetBranch )
    {
        return pushNodeEntries.stream()
            .map( node -> NodeEvents.nodeToMap( node, targetBranch ) )
            .collect( ImmutableList.toImmutableList() );
    }

    private static ImmutableMap<String, String> nodeToMap( final NodeBranchEntry node )
    {
        final ImmutableMap.Builder<String, String> map = ImmutableMap.<String, String>builder()
            .put( "id", node.getNodeId().toString() )
            .put( "path", node.getNodePath().toString() )
            .put( "branch", ContextAccessor.current().getBranch().getValue() );

        final RepositoryId repositoryId = ContextAccessor.current().getRepositoryId();
        if ( repositoryId != null )
        {
            map.put( "repo", repositoryId.toString() );
        }
        return map.build();
    }

    private static ImmutableMap<String, String> nodeToMap( final Node node, final Branch branch )
    {
        final ImmutableMap.Builder<String, String> map = ImmutableMap.<String, String>builder()
            .put( "id", node.id().toString() )
            .put( "path", node.path().toString() ).put( "branch", branch.getValue() );

        final RepositoryId repositoryId = ContextAccessor.current().getRepositoryId();
        if ( repositoryId != null )
        {
            map.put( "repo", repositoryId.toString() );
        }
        return map.build();
    }

    private static ImmutableMap<String, String> nodeToMap( final PushNodeEntry node, final Branch targetBranch )
    {
        final ImmutableMap.Builder<String, String> nodeAsMap = ImmutableMap.<String, String>builder()
            .put( "id", node.getNodeBranchEntry().getNodeId().toString() )
            .put( "path", node.getNodeBranchEntry().getNodePath().toString() )
            .put( "branch", targetBranch.getValue() );
        final RepositoryId repositoryId = ContextAccessor.current().getRepositoryId();
        if ( repositoryId != null )
        {
            nodeAsMap.put( "repo", repositoryId.toString() );
        }
        if ( node.getCurrentTargetPath() != null )
        {
            nodeAsMap.put( "currentTargetPath", node.getCurrentTargetPath().toString() );
        }
        return nodeAsMap.build();
    }

    private static ImmutableMap<String, String> createMoved( final NodePath previousPath, final Node targetNode )
    {
        final ImmutableMap.Builder<String, String> map = ImmutableMap.<String, String>builder()
            .put( "id", targetNode.id().toString() )
            .put( "path", previousPath.toString() )
            .put( "branch", ContextAccessor.current().getBranch().getValue() );
        final RepositoryId repositoryId = ContextAccessor.current().getRepositoryId();
        if ( repositoryId != null )
        {
            map.put( "repo", repositoryId.toString() );
        }
        map.put( "newPath", targetNode.path().toString() );
        return map.build();
    }
}
