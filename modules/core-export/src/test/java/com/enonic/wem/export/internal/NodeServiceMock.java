package com.enonic.wem.export.internal;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.io.ByteSource;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.node.ApplyNodePermissionsParams;
import com.enonic.wem.api.node.AttachedBinaries;
import com.enonic.wem.api.node.AttachedBinary;
import com.enonic.wem.api.node.BinaryAttachment;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.CreateRootNodeParams;
import com.enonic.wem.api.node.EditableNode;
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
import com.enonic.wem.api.node.NodeState;
import com.enonic.wem.api.node.NodeVersionDiffQuery;
import com.enonic.wem.api.node.NodeVersionDiffResult;
import com.enonic.wem.api.node.NodeVersionId;
import com.enonic.wem.api.node.Nodes;
import com.enonic.wem.api.node.PushNodesResult;
import com.enonic.wem.api.node.RenameNodeParams;
import com.enonic.wem.api.node.ReorderChildNodesParams;
import com.enonic.wem.api.node.ReorderChildNodesResult;
import com.enonic.wem.api.node.ResolveSyncWorkResult;
import com.enonic.wem.api.node.RootNode;
import com.enonic.wem.api.node.SetNodeChildOrderParams;
import com.enonic.wem.api.node.SyncWorkResolverParams;
import com.enonic.wem.api.node.UpdateNodeParams;
import com.enonic.wem.api.util.BinaryReference;
import com.enonic.wem.api.workspace.Workspace;

class NodeServiceMock
    implements NodeService
{
    private final Map<NodeId, Node> nodeIdMap = new HashMap<>();

    private final Map<NodePath, Node> nodePathMap = new HashMap<>();

    private final MockNodeTree<NodePath> nodeTree = new MockNodeTree<>( NodePath.ROOT );

    private final static Logger LOG = LoggerFactory.getLogger( NodeServiceMock.class );

    private final Map<BinaryReference, ByteSource> blobStore = Maps.newHashMap();

    @Override
    public Node create( final CreateNodeParams params )
    {
        final Node.Builder builder = Node.newNode().
            id( params.getNodeId() != null ? params.getNodeId() : NodeId.from( System.nanoTime() ) ).
            name( NodeName.from( params.getName() ) ).
            parentPath( params.getParent() ).
            childOrder( params.getChildOrder() );

        final AttachedBinaries.Builder attachmentBuilder = AttachedBinaries.create();

        for ( final BinaryAttachment binaryAttachment : params.getBinaryAttachments() )
        {
            final BlobKey blobKey = new BlobKey( binaryAttachment.getReference().toString() );
            attachmentBuilder.add( new AttachedBinary( binaryAttachment.getReference(), blobKey ) );
            blobStore.put( binaryAttachment.getReference(), binaryAttachment.getByteSource() );
        }

        builder.attachedBinaries( attachmentBuilder.build() );

        final Node createdNode = builder.build();

        nodeIdMap.putIfAbsent( createdNode.id(), createdNode );
        // LOG.info( "Store id " + createdNode.id() );
        nodePathMap.putIfAbsent( createdNode.path(), createdNode );
        //LOG.info( "Store path " + createdNode.path() );

        final MockNodeTree<NodePath> nodePathTreeNode = this.nodeTree.find( createdNode.parentPath() );

        if ( nodePathTreeNode == null )
        {
            LOG.error( "Could not find nodePathTreeNode for created node: " + createdNode.path() + ", node not inserted in tree" );
            return createdNode;
        }

        nodePathTreeNode.addChild( createdNode.path() );

        return createdNode;
    }

    @Override
    public Node update( final UpdateNodeParams params )
    {
        final Node persistedNode = nodeIdMap.get( params.getId() );
        final EditableNode editableNode = new EditableNode( persistedNode );
        params.getEditor().edit( editableNode );

        final Node editedNode = editableNode.build();
        if ( editedNode.equals( persistedNode ) )
        {
            return persistedNode;
        }

        final Node.Builder updateNodeBuilder = Node.newNode( editedNode ).
            permissions( persistedNode.getPermissions() );

        return updateNodeBuilder.build();
    }

    @Override
    public Node rename( final RenameNodeParams params )
    {
        throw new UnsupportedOperationException( "Not implemented in mock" );
    }

    @Override
    public PushNodesResult push( final NodeIds ids, final Workspace target )
    {
        throw new UnsupportedOperationException( "Not implemented in mock" );
    }

    @Override
    public Node deleteById( final NodeId id )
    {
        final Node toBeRemoved = this.nodeIdMap.get( id );

        final MockNodeTree<NodePath> treeNode = nodeTree.find( toBeRemoved.path() );
        treeNode.getParent().children.remove( treeNode );

        this.nodePathMap.remove( toBeRemoved.path() );
        this.nodeIdMap.remove( toBeRemoved.id() );

        return toBeRemoved;
    }

    @Override
    public Node deleteByPath( final NodePath path )
    {
        final MockNodeTree<NodePath> treeNode = nodeTree.find( path );
        treeNode.getParent().children.remove( treeNode );

        final Node toBeRemoved = this.nodePathMap.get( path );

        this.nodePathMap.remove( path );
        this.nodeIdMap.remove( toBeRemoved.id() );

        return toBeRemoved;
    }

    @Override
    public Node getById( final NodeId id )
    {
        return nodeIdMap.get( id );
    }

    @Override
    public Nodes getByIds( final NodeIds ids )
    {
        throw new UnsupportedOperationException( "Not implemented in mock" );
    }

    @Override
    public Node getByPath( final NodePath path )
    {
        return nodePathMap.get( path );
    }

    @Override
    public Nodes getByPaths( final NodePaths paths )
    {
        throw new UnsupportedOperationException( "Not implemented in mock" );
    }

    @Override
    public Node duplicate( final NodeId nodeId )
    {
        throw new UnsupportedOperationException( "Not implemented in mock" );
    }

    @Override
    public Node move( final NodeId nodeId, final NodePath parentNodePath )
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
    public NodeVersionDiffResult diff( final NodeVersionDiffQuery query )
    {
        throw new UnsupportedOperationException( "Not implemented in mock" );
    }

    @Override
    public FindNodesByQueryResult findByQuery( final NodeQuery nodeQuery )
    {
        throw new UnsupportedOperationException( "Not implemented in mock" );
    }

    @Override
    public NodeComparison compare( final NodeId id, final Workspace target )
    {
        throw new UnsupportedOperationException( "Not implemented in mock" );
    }

    @Override
    public NodeComparisons compare( final NodeIds ids, final Workspace target )
    {
        throw new UnsupportedOperationException( "Not implemented in mock" );
    }

    @Override
    public FindNodeVersionsResult findVersions( final GetNodeVersionsParams params )
    {
        throw new UnsupportedOperationException( "Not implemented in mock" );
    }

    @Override
    public GetActiveNodeVersionsResult getActiveVersions( final GetActiveNodeVersionsParams params )
    {
        throw new UnsupportedOperationException( "Not implemented in mock" );
    }

    @Override
    public Node setChildOrder( final SetNodeChildOrderParams params )
    {
        throw new UnsupportedOperationException( "Not implemented in mock" );
    }

    @Override
    public ReorderChildNodesResult reorderChildren( final ReorderChildNodesParams params )
    {
        throw new UnsupportedOperationException( "Not implemented in mock" );
    }

    @Override
    public Node getByVersionId( final NodeVersionId nodeVersionid )
    {
        throw new UnsupportedOperationException( "Not implemented in mock" );
    }

    @Override
    public ByteSource getBinary( final NodeId nodeId, final BinaryReference reference )
    {
        return this.blobStore.get( reference );
    }

    @Override
    public void snapshot()
    {
        throw new UnsupportedOperationException( "Not implemented in mock" );
    }

    @Override
    public int applyPermissions( final ApplyNodePermissionsParams params )
    {
        return 0;
    }

    @Override
    public ResolveSyncWorkResult resolveSyncWork( final SyncWorkResolverParams params )
    {
        return null;
    }

    @Override
    public RootNode createRootNode( final CreateRootNodeParams params )
    {
        throw new UnsupportedOperationException( "Not implemented in mock" );
    }

    @Override
    public Node setNodeState( final NodeId nodeId, final NodeState nodeState )
    {
        throw new UnsupportedOperationException( "Not implemented in mock" );
    }

    @Override
    public RootNode getRoot()
    {
        throw new UnsupportedOperationException( "Not implemented in mock" );
    }

    public NodeServiceMock()
    {
        super();
    }
}
