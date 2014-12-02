package com.enonic.wem.export.internal;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.FindNodeVersionsResult;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.FindNodesByQueryResult;
import com.enonic.wem.api.node.GetActiveNodeVersionsParams;
import com.enonic.wem.api.node.GetActiveNodeVersionsResult;
import com.enonic.wem.api.node.GetNodeVersionsParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeComparison;
import com.enonic.wem.api.node.NodeComparisons;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeIds;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodePaths;
import com.enonic.wem.api.node.NodeQuery;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.api.node.OrderChildNodeParams;
import com.enonic.wem.api.node.RenameNodeParams;
import com.enonic.wem.api.node.SetNodeChildOrderParams;
import com.enonic.wem.api.node.UpdateNodeParams;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.security.UserStoreKey;
import com.enonic.wem.api.workspace.Workspace;

class NodeServiceMock
    implements NodeService
{
    private final Map<NodeId, Node> nodeIdMap = new HashMap<>();

    private final Map<NodePath, Node> nodePathMap = new HashMap<>();

    private final MockNodeTree<NodePath> nodeTree = new MockNodeTree<>( NodePath.ROOT );

    @Override
    public Node create( final CreateNodeParams params )
    {
        final Node createdNode = Node.newNode().
            id( params.getNodeId() != null ? params.getNodeId() : NodeId.from( System.nanoTime() ) ).
            name( NodeName.from( params.getName() ) ).
            parent( params.getParent() ).
            createdTime( Instant.now() ).
            creator( PrincipalKey.ofUser( UserStoreKey.system(), "rmy" ) ).
            childOrder( params.getChildOrder() ).
            build();

        nodeIdMap.putIfAbsent( createdNode.id(), createdNode );
        nodePathMap.putIfAbsent( createdNode.path(), createdNode );

        final MockNodeTree<NodePath> nodePathTreeNode = this.nodeTree.find( createdNode.parent() );

        if ( nodePathTreeNode == null )
        {
            throw new IllegalArgumentException( "Root-path not found" );
        }

        nodePathTreeNode.addChild( createdNode.path() );

        return createdNode;
    }

    @Override
    public Node update( final UpdateNodeParams params )
    {
        return null;
    }

    @Override
    public Node rename( final RenameNodeParams params )
    {
        return null;
    }

    @Override
    public Node push( final NodeId id, final Workspace target )
    {
        return null;
    }

    @Override
    public Node deleteById( final NodeId id )
    {
        return null;
    }

    @Override
    public Node deleteByPath( final NodePath path )
    {
        return null;
    }

    @Override
    public Node getById( final NodeId id )
    {
        return nodeIdMap.get( id );
    }

    @Override
    public Nodes getByIds( final NodeIds ids )
    {
        return null;
    }

    @Override
    public Node getByPath( final NodePath path )
    {
        return nodePathMap.get( path );
    }

    @Override
    public Nodes getByPaths( final NodePaths paths )
    {
        return null;
    }

    @Override
    public Node duplicate( final NodeId nodeId )
    {
        return null;
    }

    @Override
    public FindNodesByParentResult findByParent( final FindNodesByParentParams params )
    {
        final MockNodeTree<NodePath> parentNode = this.nodeTree.find( params.getParentPath() );

        if ( parentNode == null )
        {
            throw new IllegalArgumentException( "Parent with path: " + params.getParentPath() + " not found" );
        }

        final FindNodesByParentResult.Builder resultBuilder = FindNodesByParentResult.create();

        final Nodes.Builder nodesBuilder = Nodes.create();

        for ( final MockNodeTree<NodePath> treeNode : parentNode.children )
        {
            nodesBuilder.add( nodePathMap.get( treeNode.data ) );
        }

        final Nodes nodes = nodesBuilder.build();

        return resultBuilder.hits( nodes.getSize() ).
            nodes( nodes ).
            totalHits( nodes.getSize() ).
            build();
    }

    @Override
    public FindNodesByQueryResult findByQuery( final NodeQuery nodeQuery )
    {
        return null;
    }

    @Override
    public NodeComparison compare( final NodeId id, final Workspace target )
    {
        return null;
    }

    @Override
    public NodeComparisons compare( final NodeIds ids, final Workspace target )
    {
        return null;
    }

    @Override
    public FindNodeVersionsResult findVersions( final GetNodeVersionsParams params )
    {
        return null;
    }

    @Override
    public GetActiveNodeVersionsResult getActiveVersions( final GetActiveNodeVersionsParams params )
    {
        return null;
    }

    @Override
    public Node setChildOrder( final SetNodeChildOrderParams params )
    {
        return null;
    }

    @Override
    public Node moveChild( final OrderChildNodeParams params )
    {
        return null;
    }

    @Override
    public Node getByVersionId( final NodeVersionId nodeVersionid )
    {
        return null;
    }

    @Override
    public void snapshot()
    {

    }


}
