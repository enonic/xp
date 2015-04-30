package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePublishRequest;
import com.enonic.xp.node.NodePublishRequests;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.SetNodeStateParams;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.util.Reference;

import static org.junit.Assert.*;

public class ResolveSyncWorkCommandTest
    extends AbstractNodeTest
{

    private Node rootNode;

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.rootNode = this.createDefaultRootNode();
    }

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

        final ResolveSyncWorkResult result = ResolveSyncWorkCommand.create().
            includeChildren( true ).
            nodeId( null ).
            target( WS_OTHER ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        final NodePublishRequests nodePublishRequests = result.getNodePublishRequests();
        assertEquals( 6, nodePublishRequests.size() );
        assertNotNull( nodePublishRequests.get( node1.id() ) );
        assertNotNull( nodePublishRequests.get( node1_1.id() ) );
        assertNotNull( nodePublishRequests.get( node2.id() ) );
        assertNotNull( nodePublishRequests.get( node2_1.id() ) );
        assertNotNull( nodePublishRequests.get( node3.id() ) );
        assertNotNull( nodePublishRequests.get( rootNode.id() ) );
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

        markAsDelete( node2_1.id() );
        markAsDelete( node3.id() );

        final ResolveSyncWorkResult result = ResolveSyncWorkCommand.create().
            includeChildren( true ).
            target( WS_OTHER ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        final NodePublishRequests nodePublishRequests = result.getNodePublishRequests();
        assertEquals( 1, nodePublishRequests.size() );
        assertNotNull( nodePublishRequests.get( rootNode.id() ) );

        final NodeIds deleted = result.getDelete();
        assertEquals( 2, deleted.getSize() );
        assertTrue( deleted.contains( node2_1.id() ) );
        assertTrue( deleted.contains( node3.id() ) );
    }

    private void markAsDelete( final NodeId id )
    {
        SetNodeStateParams setNodeStateParams =
            SetNodeStateParams.create().nodeId( id ).nodeState( NodeState.PENDING_DELETE ).recursive( true ).build();
        SetNodeStateCommand.create().
            params( setNodeStateParams ).
            indexServiceInternal( this.indexServiceInternal ).
            queryService( this.queryService ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            build().
            execute();
    }

    @Test
    public void deleted_not_in_target()
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

        pushNodes( WS_OTHER, node1.id(), node1_1.id(), node2.id() );

        markAsDelete( node2_1.id() );
        markAsDelete( node3.id() );

        final ResolveSyncWorkResult result = ResolveSyncWorkCommand.create().
            includeChildren( true ).
            target( WS_OTHER ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        final NodePublishRequests nodePublishRequests = result.getNodePublishRequests();
        assertEquals( 3, nodePublishRequests.size() );

        final NodeIds deleted = result.getDelete();
        assertEquals( 0, deleted.getSize() );
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

        final ResolveSyncWorkResult result = ResolveSyncWorkCommand.create().
            includeChildren( true ).
            nodeId( null ).
            target( WS_OTHER ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        final NodePublishRequests nodePublishRequests = result.getNodePublishRequests();
        assertEquals( 3, nodePublishRequests.size() );
        assertNotNull( nodePublishRequests.get( node1_1.id() ) );
        assertNotNull( nodePublishRequests.get( node2_1.id() ) );
        assertNotNull( nodePublishRequests.get( rootNode.id() ) );
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

        final ResolveSyncWorkResult result = getResolveSyncWorkResult( node1.id(), false );

        final NodePublishRequests nodePublishRequests = result.getNodePublishRequests();
        assertEquals( 3, nodePublishRequests.size() );
        assertNotNull( nodePublishRequests.get( node1.id() ) );
        assertNotNull( nodePublishRequests.get( node1_1.id() ) );

        final NodePublishRequest node2PublishRequest = nodePublishRequests.get( node2.id() );
        assertNotNull( node2PublishRequest );
        node2PublishRequest.reasonReferredFrom();
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

        final ResolveSyncWorkResult result = ResolveSyncWorkCommand.create().
            includeChildren( true ).
            nodeId( node1.id() ).
            target( WS_OTHER ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        final NodePublishRequests nodePublishRequests = result.getNodePublishRequests();
        assertEquals( 5, nodePublishRequests.size() );
        assertNotNull( nodePublishRequests.get( node1.id() ) );
        assertNotNull( nodePublishRequests.get( node1_1.id() ) );
        assertNotNull( nodePublishRequests.get( node2.id() ) );
        assertNotNull( nodePublishRequests.get( node2_1.id() ) );

        final NodePublishRequest node2PublishRequest = nodePublishRequests.get( node2.id() );
        assertNotNull( node2PublishRequest );
        assertTrue( node2PublishRequest.reasonParentFor() );

        final NodePublishRequest node2_1PublishRequest = nodePublishRequests.get( node2_1.id() );
        assertNotNull( node2_1PublishRequest );
        assertTrue( node2_1PublishRequest.reasonParentFor() );
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

        final ResolveSyncWorkResult result = ResolveSyncWorkCommand.create().
            includeChildren( true ).
            nodeId( node1.id() ).
            target( WS_OTHER ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        final NodePublishRequests nodePublishRequests = result.getNodePublishRequests();
        assertEquals( 5, nodePublishRequests.size() );
        assertNotNull( nodePublishRequests.get( node1.id() ) );
        assertNotNull( nodePublishRequests.get( node1_1.id() ) );
        assertNotNull( nodePublishRequests.get( node2.id() ) );
        assertNotNull( nodePublishRequests.get( node2_1.id() ) );
        assertNotNull( nodePublishRequests.get( node2_1_1.id() ) );

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

        final ResolveSyncWorkResult result = ResolveSyncWorkCommand.create().
            includeChildren( true ).
            nodeId( node1.id() ).
            target( WS_OTHER ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        final NodePublishRequests nodePublishRequests = result.getNodePublishRequests();
        assertEquals( 6, nodePublishRequests.size() );
        assertNotNull( nodePublishRequests.get( node1.id() ) );
        assertNotNull( nodePublishRequests.get( node1_1.id() ) );
        assertNotNull( nodePublishRequests.get( node2.id() ) );
        assertNotNull( nodePublishRequests.get( node2_1.id() ) );
        assertNotNull( nodePublishRequests.get( node2_1_1.id() ) );
        assertTrue( nodePublishRequests.get( node2_1_1.id() ).reasonReferredFrom() );
        assertNotNull( nodePublishRequests.get( node3.id() ) );
        assertTrue( nodePublishRequests.get( node3.id() ).reasonReferredFrom() );
    }

    @Test
    public void duplicate_node_with_reference_then_resolve()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node1" ).
            data( createDataWithReferences( Reference.from( "node1_1-id" ), Reference.from( "node1_1_1-id" ) ) ).
            build() );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1-id" ) ).
            parent( node1.path() ).
            name( "node1_1" ).
            data( createDataWithReferences( Reference.from( "node1_1_1-id" ) ) ).
            build() );

        final Node node_1_1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1_1-id" ) ).
            parent( node1_1.path() ).
            name( "node1_1_1" ).
            build() );

        refresh();

        final Node node1Duplicate = duplicateNode( node1 );

        final ResolveSyncWorkResult result = ResolveSyncWorkCommand.create().
            includeChildren( true ).
            nodeId( node1Duplicate.id() ).
            target( WS_OTHER ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        assertEquals( 3, result.getNodePublishRequests().size() );
    }

    @Test
    public void include_renamed_parents()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            name( "node1" ).
            build() );

        final PropertyTree node1_1_data = new PropertyTree();
        node1_1_data.addReference( "myRef", Reference.from( "node2_1" ) );

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

        final Node node2_2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2_2" ) ).
            parent( node2.path() ).
            name( "node2_2" ).
            build() );

        pushNodes( WS_OTHER, node1_1.id(), node2.id(), node1.id() );

        renameNode( node1 );
        renameNode( node1_1 );
        renameNode( node2 );

        final ResolveSyncWorkResult result = getResolveSyncWorkResult( node1_1.id(), false );

        final NodePublishRequests nodePublishRequests = result.getNodePublishRequests();
        assertEquals( 4, nodePublishRequests.size() );
    }

    private ResolveSyncWorkResult getResolveSyncWorkResult( final NodeId nodeId, final boolean includeChildren )
    {
        return ResolveSyncWorkCommand.create().
            nodeId( nodeId ).
            target( WS_OTHER ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            includeChildren( includeChildren ).
            build().
            execute();
    }

    private ResolveSyncWorkResult getResolveSyncWorkResult( final String nodeId )
    {
        return getResolveSyncWorkResult( NodeId.from( nodeId ), false );
    }


    /*
    - S1 (E)
        - A1 (E)
 	    - A2 (E)
 	        - A2_1 - Ref:B2_1 (M)
    - S2 (Moved)
        - B1 (E)
        - B2 (E)
 	        - B2_1 (E)

 	 Push only A2_1 since B2_1 is unchanged, even if S2 is moved (this has to be done in separate push)
     */
    @Test
    public void reference_not_updated()
        throws Exception
    {
        createS1S2Tree();

        pushAllNodesInS1S2Tree();

        updateNode( "a2_1" );

        final ResolveSyncWorkResult result = getResolveSyncWorkResult( "a2_1" );

        final NodePublishRequests nodePublishRequests = result.getNodePublishRequests();

        assertEquals( 1, nodePublishRequests.size() );
    }

    /*
    - S1 (E)
        - A1 (E)
 	    - A2 (E)
 	        - A2_1 - Ref:B2_1 (M)
    - S2 (Moved)
        - B1 (E)
        - B2 (E)
 	        - B2_1 (M)

 	    Push A2_1 and B2_1, S2 is moved but this has to be done in separate push
     */
    @Test
    public void reference_is_updated()
        throws Exception
    {
        createS1S2Tree();

        pushAllNodesInS1S2Tree();

        updateNode( "a2_1" );
        updateNode( "b2_1" );

        final ResolveSyncWorkResult result = getResolveSyncWorkResult( "a2_1" );

        final NodePublishRequests nodePublishRequests = result.getNodePublishRequests();

        assertEquals( 2, nodePublishRequests.size() );
    }

    /*
    - S1 (E)
        - A1 (E)
        - A2 (M)
            - A2_1 - Ref:B2_1 (M)
    - S2 (Moved)
        - B1 (E)
        - B2 (E)
           - B2_1 (E)

        Push A2_1, A2 is modified but this has to be done in separate push
    */
    @Test
    public void parent_modified_should_be_ignored()
        throws Exception
    {
        createS1S2Tree();

        pushAllNodesInS1S2Tree();

        updateNode( "a2" );
        updateNode( "a2_1" );

        final ResolveSyncWorkResult result = getResolveSyncWorkResult( "a2_1" );

        final NodePublishRequests nodePublishRequests = result.getNodePublishRequests();

        assertEquals( 1, nodePublishRequests.size() );
    }

    /*
    - S1 (E)
        - A1 (E)
        - A2 (M)
          - A2_1 - Ref:B2_1 (M)
    - S2 (Moved)
        - B1 (E)
        - B2 (E)
          - B2_1 (Moved to S2)

        Push A2_1, B2_1 since modified
    */
    @Test
    public void reference_target_moved()
        throws Exception
    {
        createS1S2Tree();

        pushAllNodesInS1S2Tree();

        updateNode( "a2_1" );

        moveNode( "b2_1", NodePath.newPath( "/s2" ).build(), "b2_1" );

        final ResolveSyncWorkResult result = getResolveSyncWorkResult( "a2_1" );

        final NodePublishRequests nodePublishRequests = result.getNodePublishRequests();

        assertEquals( 2, nodePublishRequests.size() );

        assertNode( nodePublishRequests, "a2_1" );
        assertNode( nodePublishRequests, "b2_1" );
    }

    /*
    - S1 (New)
        - A1 (New)
        - A2 (New)
            - A2_1 - Ref:B2_1 (New)
    - S2 (New)
        - B1 (New)
        - B2 (New)
            - B2_1 (New)

        Push A2_1, A2, S1, B2_1, B2, S2. A1 and B1 should remain untouched
    */
    @Test
    public void do_not_publish_other_children_of_dependent_parent()
        throws Exception
    {
        createS1S2Tree();

        final ResolveSyncWorkResult result = getResolveSyncWorkResult( "a2_1" );

        final NodePublishRequests nodePublishRequests = result.getNodePublishRequests();

        assertEquals( 6, nodePublishRequests.size() );

        assertNode( nodePublishRequests, "s1" );
        assertNode( nodePublishRequests, "a2" );
        assertNode( nodePublishRequests, "a2_1" );
        assertNode( nodePublishRequests, "s2" );
        assertNode( nodePublishRequests, "b2" );
        assertNode( nodePublishRequests, "b2_1" );
    }


    /*
 - S1 (New)
     - A1 (New)
     - A2 (New)
         - A2_1 - Ref:B2_1 (New)
 - S2 (New)
     - B1 (New)
     - B2 (New)
         - B2_1 (New)
  - S1d (New)
    - A1d (New)
    - A2d (New)
         - A2_1d - Ref:B2_1 (New)

    Duplicate S1, then publish S1
    Should publish S1, A1, A2, A2_1, B2_1, B2, S2
 */
    @Test
    public void publish_original_with_duplicate_do_not_publish_duplicate()
        throws Exception
    {
        createS1S2Tree();

        duplicateNode( getNodeById( NodeId.from( "s1" ) ) );

        final ResolveSyncWorkResult result = getResolveSyncWorkResult( "s1" );

        final NodePublishRequests nodePublishRequests = result.getNodePublishRequests();

        assertEquals( 7, nodePublishRequests.size() );

        assertNode( nodePublishRequests, "s1" );
        assertNode( nodePublishRequests, "a2" );
        assertNode( nodePublishRequests, "a2_1" );
        assertNode( nodePublishRequests, "s2" );
        assertNode( nodePublishRequests, "b2" );
        assertNode( nodePublishRequests, "b2_1" );
    }

    /*
    - S1 (New)
     - A1 (New)
     - A2 (New)
         - A2_1 - Ref:B2_1 (New)
    - S2 (New)
     - B1 (New)
     - B2 (New)
         - B2_1 (New)
    - S1d (New)
    - A1d (New)
    - A2d (New)
         - A2_1d - Ref:B2_1 (New)

    Publish S1 with children, should publish S1, A1, A2, A2_1, B2_1, B2, S2
    */
    @Test
    public void resolve_with_include_children()
        throws Exception
    {
        createS1S2Tree();

        final ResolveSyncWorkResult result = getResolveSyncWorkResult( NodeId.from( "s1" ), true );

        final NodePublishRequests nodePublishRequests = result.getNodePublishRequests();

        assertEquals( 7, nodePublishRequests.size() );

        assertNode( nodePublishRequests, "s1" );
        assertNode( nodePublishRequests, "a1" );
        assertNode( nodePublishRequests, "a2" );
        assertNode( nodePublishRequests, "a2_1" );
        assertNode( nodePublishRequests, "s2" );
        assertNode( nodePublishRequests, "b2" );
        assertNode( nodePublishRequests, "b2_1" );

        assertTrue( nodePublishRequests.get( NodeId.from( "b2_1" ) ).reasonReferredFrom() );
        assertTrue( nodePublishRequests.get( NodeId.from( "b2" ) ).reasonParentFor() );
        assertTrue( nodePublishRequests.get( NodeId.from( "s2" ) ).reasonParentFor() );

        //assertTrue( nodePublishRequests.get( NodeId.from( "a1" ) ).reasonChildOf() );
        //assertTrue( nodePublishRequests.get( NodeId.from( "a2" ) ).reasonChildOf() );
        //assertTrue( nodePublishRequests.get( NodeId.from( "a2_1" ) ).reasonChildOf() );
    }


    @Test
    public void publish_duplicate_of_original_do_not_publish_original()
        throws Exception
    {
        createS1S2Tree();

        final Node s1d = duplicateNode( getNodeById( NodeId.from( "s1" ) ) );

        final ResolveSyncWorkResult result = getResolveSyncWorkResult( s1d.id(), false );

        final NodePublishRequests nodePublishRequests = result.getNodePublishRequests();

        assertEquals( 7, nodePublishRequests.size() );
    }

    private void createS1S2Tree()
    {
        final Node s1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "s1" ) ).
            parent( NodePath.ROOT ).
            name( "s1" ).
            build() );

        final Node a1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "a1" ) ).
            parent( s1.path() ).
            name( "a1" ).
            build() );

        final Node a2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "a2" ) ).
            parent( s1.path() ).
            name( "a2" ).
            build() );

        final Node a2_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "a2_1" ) ).
            parent( a2.path() ).
            data( createDataWithReferences( Reference.from( "b2_1" ) ) ).
            name( "a2_1" ).
            build() );

        final Node s2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "s2" ) ).
            parent( NodePath.ROOT ).
            name( "s2" ).
            build() );

        final Node b1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "b1" ) ).
            parent( s2.path() ).
            name( "b1" ).
            build() );

        final Node b2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "b2" ) ).
            parent( s2.path() ).
            name( "b2" ).
            build() );

        final Node b2_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "b2_1" ) ).
            parent( b2.path() ).
            name( "b2_1" ).
            build() );
    }

    private void pushAllNodesInS1S2Tree()
    {
        pushNodes( NodeIds.from( "s1", "s2", "a1", "a2", "a2_1", "b1", "b2", "b2_1" ), WS_OTHER );
    }


    private void updateNode( final String nodeId )
    {
        final UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            editor( toBeEdited -> {
                final PropertyTree nodeData = toBeEdited.data;
                nodeData.addString( "newValue", "hepp" );
            } ).
            id( NodeId.from( nodeId ) ).
            build();

        updateNode( updateNodeParams );
    }

    void moveNode( final String nodeId, final NodePath newParent, final String newName )
    {
        MoveNodeCommand.create().
            queryService( this.queryService ).
            indexServiceInternal( this.indexServiceInternal ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            id( NodeId.from( nodeId ) ).
            newNodeName( NodeName.from( newName ) ).
            newParent( newParent ).
            build().
            execute();
    }

    private void renameNode( final Node node )
    {
        MoveNodeCommand.create().
            id( node.id() ).
            newNodeName( NodeName.from( node.id().toString() + "edited" ) ).
            indexServiceInternal( this.indexServiceInternal ).
            versionService( this.versionService ).
            queryService( this.queryService ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            build().
            execute();
    }


    private Node duplicateNode( final Node node1 )
    {
        return DuplicateNodeCommand.create().
            id( node1.id() ).
            versionService( versionService ).
            indexServiceInternal( indexServiceInternal ).
            nodeDao( nodeDao ).
            queryService( queryService ).
            branchService( branchService ).
            binaryBlobStore( this.binaryBlobStore ).
            build().
            execute();
    }

    private PropertyTree createDataWithReferences( final Reference... references )
    {
        PropertyTree data = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        for ( final Reference reference : references )
        {
            data.setReference( reference.getNodeId().toString(), reference );
        }

        return data;
    }

    private void assertNode( final NodePublishRequests nodePublishRequests, final String s1 )
    {
        assertNotNull( nodePublishRequests.get( NodeId.from( s1 ) ) );
    }
}