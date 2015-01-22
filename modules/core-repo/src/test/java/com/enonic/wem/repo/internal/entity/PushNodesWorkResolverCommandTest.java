package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeIds;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.PushNodesWorkResolverResult;
import com.enonic.wem.api.util.Reference;

import static org.junit.Assert.*;

public class PushNodesWorkResolverCommandTest
    extends AbstractNodeTest
{

    @Test
    public void diff_all_new()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            name( "node1" ).
            build() );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1" ) ).
            parent( node1.path() ).
            name( "node1_1" ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2" ) ).
            parent( NodePath.ROOT ).
            name( "node2" ).
            build() );

        final Node node2_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2_1" ) ).
            parent( node2.path() ).
            name( "node2_1" ).
            build() );

        final Node node3 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node3" ) ).
            parent( NodePath.ROOT ).
            name( "node3" ).
            build() );

        final PushNodesWorkResolverResult result = PushNodesWorkResolverCommand.create().
            includeChildren( true ).
            nodeId( null ).
            target( WS_OTHER ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            indexService( this.indexService ).
            build().
            execute();

        final NodeIds published = result.getPublish();
        assertEquals( 5, published.getSize() );
        assertTrue( published.contains( node1.id() ) );
        assertTrue( published.contains( node1_1.id() ) );
        assertTrue( published.contains( node2.id() ) );
        assertTrue( published.contains( node2_1.id() ) );
        assertTrue( published.contains( node3.id() ) );
    }


    @Test
    public void diff_detect_deleted()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            name( "node1" ).
            build() );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1" ) ).
            parent( node1.path() ).
            name( "node1_1" ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2" ) ).
            parent( NodePath.ROOT ).
            name( "node2" ).
            build() );

        final Node node2_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2_1" ) ).
            parent( node2.path() ).
            name( "node2_1" ).
            build() );

        final Node node3 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node3" ) ).
            parent( NodePath.ROOT ).
            name( "node3" ).
            build() );

        pushNodes( WS_OTHER, node1.id(), node1_1.id(), node2.id(), node2_1.id(), node3.id() );

        doDeleteNode( node2_1.id() );
        doDeleteNode( node3.id() );

        final PushNodesWorkResolverResult result = PushNodesWorkResolverCommand.create().
            includeChildren( true ).
            target( WS_OTHER ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            indexService( this.indexService ).
            build().
            execute();

        final NodeIds published = result.getPublish();
        assertEquals( 0, published.getSize() );

        final NodeIds deleted = result.getDelete();
        assertEquals( 2, deleted.getSize() );
        assertTrue( deleted.contains( node2_1.id() ) );
        assertTrue( deleted.contains( node3.id() ) );

    }

    @Test
    public void ignore_nodes_without_diff()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            name( "node1" ).
            build() );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1" ) ).
            parent( node1.path() ).
            name( "node1_1" ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2" ) ).
            parent( NodePath.ROOT ).
            name( "node2" ).
            build() );

        final Node node2_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2_1" ) ).
            parent( node2.path() ).
            name( "node2_1" ).
            build() );

        final Node node3 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node3" ) ).
            parent( NodePath.ROOT ).
            name( "node3" ).
            build() );

        pushNodes( WS_OTHER, node1.id(), node2.id(), node3.id() );

        final PushNodesWorkResolverResult result = PushNodesWorkResolverCommand.create().
            includeChildren( true ).
            nodeId( null ).
            target( WS_OTHER ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            indexService( this.indexService ).
            build().
            execute();

        final NodeIds published = result.getPublish();
        assertEquals( 2, published.getSize() );
        assertTrue( published.contains( node1_1.id() ) );
        assertTrue( published.contains( node2_1.id() ) );
    }


    @Test
    public void include_referred_nodes()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            name( "node1" ).
            build() );

        final PropertyTree node1_1_data = new PropertyTree();
        node1_1_data.addReference( "myRef", Reference.from( "node2" ) );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1" ) ).
            parent( node1.path() ).
            name( "node1_1" ).
            data( node1_1_data ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2" ) ).
            parent( NodePath.ROOT ).
            name( "node2" ).
            build() );

        createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2_1" ) ).
            parent( node2.path() ).
            name( "node2_1" ).
            build() );

        final PushNodesWorkResolverResult result = PushNodesWorkResolverCommand.create().
            nodeId( node1.id() ).
            target( WS_OTHER ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            indexService( this.indexService ).
            build().
            execute();

        final NodeIds published = result.getPublish();
        assertEquals( 3, published.getSize() );
        assertTrue( published.contains( node1.id() ) );
        assertTrue( published.contains( node1_1.id() ) );
        assertTrue( published.contains( node2.id() ) );
    }

    @Test
    public void make_sure_referred_nodes_includes_parents_if_missing()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            name( "node1" ).
            build() );

        final PropertyTree node1_1_data = new PropertyTree();
        node1_1_data.addReference( "myRef", Reference.from( "node2_1_1" ) );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1" ) ).
            parent( node1.path() ).
            name( "node1_1" ).
            data( node1_1_data ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2" ) ).
            parent( NodePath.ROOT ).
            name( "node2" ).
            build() );

        final Node node2_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2_1" ) ).
            parent( node2.path() ).
            name( "node2_1" ).
            build() );

        final Node node2_1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2_1_1" ) ).
            parent( node2_1.path() ).
            name( "node2_1_1" ).
            build() );

        final PushNodesWorkResolverResult result = PushNodesWorkResolverCommand.create().
            includeChildren( true ).
            nodeId( node1.id() ).
            target( WS_OTHER ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            indexService( this.indexService ).
            build().
            execute();

        final NodeIds published = result.getPublish();
        assertEquals( 5, published.getSize() );
        assertTrue( published.contains( node1.id() ) );
        assertTrue( published.contains( node1_1.id() ) );
        assertTrue( published.contains( node2.id() ) );
        assertTrue( published.contains( node2_1.id() ) );
        assertTrue( published.contains( node2_1_1.id() ) );
    }

    @Test
    public void reference_to_another_branch_with_back_reference()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            name( "node1" ).
            build() );

        final PropertyTree node1_1_data = new PropertyTree();
        node1_1_data.addReference( "myRef", Reference.from( "node2_1_1" ) );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1" ) ).
            parent( node1.path() ).
            name( "node1_1" ).
            data( node1_1_data ).
            build() );

        final PropertyTree node2_data = new PropertyTree();
        node2_data.addReference( "myRef", Reference.from( "node1_1" ) );

        final Node node2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2" ) ).
            parent( NodePath.ROOT ).
            name( "node2" ).
            data( node2_data ).
            build() );

        final Node node2_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2_1" ) ).
            parent( node2.path() ).
            name( "node2_1" ).
            build() );

        final Node node2_1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2_1_1" ) ).
            parent( node2_1.path() ).
            name( "node2_1_1" ).
            build() );

        createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node3" ) ).
            parent( NodePath.ROOT ).
            name( "node3" ).
            build() );

        final PushNodesWorkResolverResult result = PushNodesWorkResolverCommand.create().
            includeChildren( true ).
            nodeId( node1.id() ).
            target( WS_OTHER ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            indexService( this.indexService ).
            build().
            execute();

        final NodeIds published = result.getPublish();
        assertEquals( 5, published.getSize() );
        assertTrue( published.contains( node1.id() ) );
        assertTrue( published.contains( node1_1.id() ) );
        assertTrue( published.contains( node2.id() ) );
        assertTrue( published.contains( node2_1.id() ) );
        assertTrue( published.contains( node2_1_1.id() ) );
    }

    @Test
    public void reference_to_another_branch_with_another_branch_reference()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            name( "node1" ).
            build() );

        final PropertyTree node1_1_data = new PropertyTree();
        node1_1_data.addReference( "myRef", Reference.from( "node2_1_1" ) );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1" ) ).
            parent( node1.path() ).
            name( "node1_1" ).
            data( node1_1_data ).
            build() );

        final PropertyTree node2_data = new PropertyTree();
        node2_data.addReference( "myRef", Reference.from( "node3" ) );

        final Node node2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2" ) ).
            parent( NodePath.ROOT ).
            name( "node2" ).
            data( node2_data ).
            build() );

        final Node node2_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2_1" ) ).
            parent( node2.path() ).
            name( "node2_1" ).
            build() );

        final Node node2_1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2_1_1" ) ).
            parent( node2_1.path() ).
            name( "node2_1_1" ).
            build() );

        final PropertyTree node3_data = new PropertyTree();
        node1_1_data.addReference( "myRef", Reference.from( "node2_1" ) );

        final Node node3 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node3" ) ).
            parent( NodePath.ROOT ).
            name( "node3" ).
            data( node3_data ).
            build() );

        final PushNodesWorkResolverResult result = PushNodesWorkResolverCommand.create().
            includeChildren( true ).
            nodeId( node1.id() ).
            target( WS_OTHER ).
            workspaceService( this.workspaceService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            indexService( this.indexService ).
            build().
            execute();

        final NodeIds published = result.getPublish();
        assertEquals( 6, published.getSize() );
        assertTrue( published.contains( node1.id() ) );
        assertTrue( published.contains( node1_1.id() ) );
        assertTrue( published.contains( node2.id() ) );
        assertTrue( published.contains( node2_1.id() ) );
        assertTrue( published.contains( node2_1_1.id() ) );
        assertTrue( published.contains( node3.id() ) );
    }


}