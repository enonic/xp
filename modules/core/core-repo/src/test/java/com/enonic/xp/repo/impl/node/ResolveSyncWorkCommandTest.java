package com.enonic.xp.repo.impl.node;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.google.common.base.Stopwatch;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePublishRequests;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.SetNodeStateParams;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.util.Reference;

import static org.junit.Assert.*;

public class ResolveSyncWorkCommandTest
    extends AbstractNodeTest
{
    private final static NodeId ROOT_UUID = NodeId.from( "000-000-000-000" );

    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test
    public void detect_children_marked_for_deletion()
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

        final NodeIds result = ResolveSyncWorkCommand.create().
            nodeId( ROOT_UUID ).
            includeChildren( true ).
            target( WS_OTHER ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertEquals( 3, result.getSize() );
    }

    /**
     * Tries to resolve one of the nodes in the middle of tree of deleted nodes.
     *
     * @throws Exception
     */
    @Test
    public void deleted_child_dont_include_parent_deletion()
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

        final Node node1_1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1_1" ) ).
            parent( node1_1.path() ).
            name( "node1_1_1" ).
            build() );

        final Node node1_1_1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1_1_1" ) ).
            parent( node1_1_1.path() ).
            name( "node1_1_1_1" ).
            build() );

        pushNodes( WS_OTHER, node1.id(), node1_1.id(), node1_1_1.id(), node1_1_1_1.id() );

        refresh();

        markAsDelete( node1.id() );
        markAsDelete( node1_1.id() );
        markAsDelete( node1_1_1.id() );
        markAsDelete( node1_1_1_1.id() );

        refresh();

        final NodeIds result = ResolveSyncWorkCommand.create().
            nodeId( node1_1.id() ).
            includeChildren( true ).
            target( WS_OTHER ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertEquals( 3, result.getSize() );
        assertTrue( result.contains( node1_1.id() ) );
        assertTrue( result.contains( node1_1_1.id() ) );
        assertTrue( result.contains( node1_1_1_1.id() ) );
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

        final NodeIds result = ResolveSyncWorkCommand.create().
            includeChildren( true ).
            nodeId( getNodeByPath( NodePath.ROOT ).id() ).
            target( WS_OTHER ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertEquals( 3, result.getSize() );
    }

    /**
     * node1                                   (Moved)
     * .....|
     * .....node1_1                            (New)
     * ............|
     * .............node1_1_1                  (New)
     * ......................|
     * .......................node1_1_1_1      (New)
     * <p>
     * Contents created below look like pic above.
     * ResolveSyncWorkCommand will return empty set for node1_1_1_1.id() when called with includeChildren=true,
     * and will return all four when called with includeChildren=false, because:
     * 1) FindNodesWithVersionDifferenceCommand will returns only actual nodes that have Moved status (such like node1)...
     * 2) ... though CompareContentCommand will return status Moved for all children of Moved content.
     * 3) When includeChildren=false ResolveSyncWorkCommand will resolve all parents against the passed node.
     * So ResolveSyncWorkCommand will return only "Moved" parents of passed node and passed node itself when includeChildren=false
     */
    @Test
    public void resolveDependenciesOfMovedNodes()
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

        final Node node1_1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1_1" ) ).
            parent( node1_1.path() ).
            name( "node1_1_1" ).
            build() );

        final Node node1_1_1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1_1_1" ) ).
            parent( node1_1_1.path() ).
            name( "node1_1_1_1" ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2" ) ).
            parent( NodePath.ROOT ).
            name( "node2" ).
            build() );

        pushNodes( WS_OTHER, node1.id(), node2.id(), node1_1.id(), node1_1_1.id(), node1_1_1_1.id() );

        moveNode( node1, node2.path() );

        final NodeIds resultChildrenIncluded = resolveSyncWorkResult( node1_1_1_1.id(), true );
        final NodeIds resultChildrenNotIncluded = resolveSyncWorkResult( node1_1_1_1.id(), false );

        assertEquals( resultChildrenIncluded.getSize(), 0 );
        assertEquals( resultChildrenNotIncluded.getSize(), 4 );
    }

    /**
     * node1                                   (Moved)
     * .....|
     * .....node1_1                            (New)
     * ............|
     * .............node1_1_1                  (New)
     * ......................|
     * .......................node1_1_1_1      (New)
     * <p>
     * Contents created below look like pic above.
     * ResolveSyncWorkCommand will return empty set for node1_1_1.id() when called with includeChildren=true,
     * and will return node1_1_1 and two of its parents when called with includeChildren=false, because:
     * 1) FindNodesWithVersionDifferenceCommand will returns only actual nodes that have Moved status (such like node1)...
     * 2) ... though CompareContentCommand will return status Moved for all children of Moved content.
     * 3) When includeChildren=false ResolveSyncWorkCommand will resolve all parents against the passed node.
     * So ResolveSyncWorkCommand will return only "Moved" parents of passed node and passed node itself when includeChildren=false
     */
    @Test
    public void resolveDependenciesOfMovedNodes2()
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

        final Node node1_1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1_1" ) ).
            parent( node1_1.path() ).
            name( "node1_1_1" ).
            build() );

        final Node node1_1_1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1_1_1" ) ).
            parent( node1_1_1.path() ).
            name( "node1_1_1_1" ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2" ) ).
            parent( NodePath.ROOT ).
            name( "node2" ).
            build() );

        pushNodes( WS_OTHER, node1.id(), node2.id(), node1_1.id(), node1_1_1.id(), node1_1_1_1.id() );

        moveNode( node1, node2.path() );

        final NodeIds resultChildrenIncluded = resolveSyncWorkResult( node1_1_1.id(), true );
        final NodeIds resultChildrenNotIncluded = resolveSyncWorkResult( node1_1_1.id(), false );

        assertEquals( resultChildrenIncluded.getSize(), 0 );
        assertEquals( resultChildrenNotIncluded.getSize(), 3 );
    }

    /**
     * node1                                   (New)
     * .....|
     * .....node1_1                            (New)
     * ............|
     * .............node1_1_1                  (Moved)
     * ......................|
     * .......................node1_1_1_1      (New)
     * <p>
     * Contents created below look like pic above.
     * ResolveSyncWorkCommand will return node1_1_1 for node1_1_1.id() when called both with includeChildren=true and with includeChildren=false
     * 1) FindNodesWithVersionDifferenceCommand will returns only actual nodes that have Moved status (such like node1)...
     * 2) ... though CompareContentCommand will return status Moved for all children of Moved content.
     * 3) When includeChildren=false ResolveSyncWorkCommand will resolve all parents against the passed node.
     * So ResolveSyncWorkCommand will return only "Moved" parents of passed node and passed node itself when includeChildren=false
     */
    @Test
    public void resolveDependenciesOfMovedNodes3()
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

        final Node node1_1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1_1" ) ).
            parent( node1_1.path() ).
            name( "node1_1_1" ).
            build() );

        final Node node1_1_1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1_1_1" ) ).
            parent( node1_1_1.path() ).
            name( "node1_1_1_1" ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2" ) ).
            parent( NodePath.ROOT ).
            name( "node2" ).
            build() );

        pushNodes( WS_OTHER, node1.id(), node2.id(), node1_1.id(), node1_1_1.id(), node1_1_1_1.id() );

        moveNode( node1_1_1, node2.path() );

        refresh();

        final NodeIds resultChildrenIncluded = resolveSyncWorkResult( node1_1_1.id(), true );
        final NodeIds resultChildrenNotIncluded = resolveSyncWorkResult( node1_1_1.id(), false );

        assertEquals( resultChildrenIncluded.getSize(), 1 );
        assertEquals( resultChildrenNotIncluded.getSize(), 1 );
    }

    private void moveNode( Node moveMe, NodePath to )
    {
        MoveNodeCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            id( moveMe.id() ).
            newNodeName( NodeName.from( moveMe.name() + "_new" ) ).
            newParent( to ).
            build().
            execute();
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

        final NodeIds result = resolveSyncWorkResult( node1_1.id(), false );

        assertEquals( 3, result.getSize() );
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

        final NodeIds result = ResolveSyncWorkCommand.create().
            includeChildren( true ).
            nodeId( node1.id() ).
            target( WS_OTHER ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertEquals( 6, result.getSize() );
       /*
        assertNotNull( result.get( node1.id() ) );
        assertNotNull( result.get( node1_1.id() ) );
        assertNotNull( result.get( node2.id() ) );
        assertNotNull( result.get( node2_1.id() ) );
        assertNotNull( result.get( node2_1_1.id() ) );
        assertTrue( result.get( node2_1_1.id() ).reasonReferredFrom() );
        assertNotNull( result.get( node3.id() ) );
        assertTrue( result.get( node3.id() ).reasonReferredFrom() );
        */
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

        refresh();

        final NodeIds result = ResolveSyncWorkCommand.create().
            includeChildren( true ).
            nodeId( node1Duplicate.id() ).
            target( WS_OTHER ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertEquals( 3, result.getSize() );
    }


    /*
        - S1 (N)
            - A1 (N)
            - A2 (N)
                - A2_1 - Ref:B2_1 (N)
        - S2 (N)
            - B1 (N)
            - B2 (N)
                - B2_1 (N)

        Resolve: Root
     */
    @Test
    public void publish_root_all_new()
    {
        createS1S2Tree();

        final NodeIds results = resolveSyncWorkResult( getNodeByPath( NodePath.ROOT ).id(), true );

        assertEquals( 9, results.getSize() );

        /*
        final String rootNodeId = RootNode.UUID.toString();
        assertRequested( requests, rootNodeId );
        assertChildOf( requests, "s1", rootNodeId );
        assertChildOf( requests, "a1", "s1" );
        assertChildOf( requests, "a2", "s1" );
        assertChildOf( requests, "a2_1", "a2" );
        assertChildOf( requests, "s2", rootNodeId );
        assertChildOf( requests, "b1", "s2" );
        assertChildOf( requests, "b2", "s2" );
        assertChildOf( requests, "b2_1", "b2" );
        */
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

     Resolve A2_1
  */
    @Test
    public void reference_moved_must_be_pushed()
        throws Exception
    {
        createS1S2Tree();

        pushAllNodesInS1S2Tree();

        updateNode( "a2_1" );

        moveNode( "b2_1", NodePath.create( "/s2" ).build(), "b2_1" );

        final NodeIds result = resolveSyncWorkResult( "a2_1" );

        assertEquals( 2, result.getSize() );
    }

    /*
    - S1 (E)
        - A1 (E)
 	    - A2 (E)
 	        - A2_1 - Ref:B2_1 (M)
    - S2 (E)
        - B1 (E)
        - B2 (E)
 	        - B2_1 (M)

        Resolve: A2_1
     */
    @Test
    public void reference_has_been_moved_push_reference_not_parents_even_if_modified()
        throws Exception
    {
        createS1S2Tree();

        pushAllNodesInS1S2Tree();

        updateNode( "a2_1" );
        updateNode( "b2" );
        updateNode( "b2_1" );

        final NodeIds result = resolveSyncWorkResult( "a2_1" );

        assertEquals( 2, result.getSize() );
    }

    /*
        - S1 (E)
            - A1 (E)
            - A2 (E)
                - A2_1 - Ref:B2_1 (M)
        - S2 (Moved)
            - B1 (M)
            - B2 (M)
                - B2_1 (M)

        Resolve: a2_1
 	 */
    @Test
    public void reference_and_parents_has_been_moved_must_push_reference_and_parents()
        throws Exception
    {
        createS1S2Tree();

        pushAllNodesInS1S2Tree();

        updateNode( "a2_1" );
        moveNode( "s2", NodePath.ROOT, "s2edit" );

        final NodeIds result = resolveSyncWorkResult( "a2_1" );

        assertEquals( 4, result.getSize() );

    }

    /*
     - S1 (E)
         - A1 (E)
         - A2 (U)
             - A2_1 - Ref:B2_1 (U)
     - S2 (E)
         - B1 (E)
         - B2 (E)
             - B2_1 (E)

      Resolve: A2_1
    */
    @Test
    public void modified_parent_should_not_be_pushed()
        throws Exception
    {
        createS1S2Tree();

        pushAllNodesInS1S2Tree();

        updateNode( "a2" );
        updateNode( "a2_1" );

        final NodeIds result = resolveSyncWorkResult( "a2_1" );

        assertEquals( 1, result.getSize() );
        //assertRequested( requests, "a2_1" );
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
             - B2_2 (New)

      Resolve: A2_1
    */
    @Test
    public void do_not_publish_other_children_of_dependent_parent()
        throws Exception
    {
        createS1S2Tree();

        // Add child-node to B2
        createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "b2_2" ) ).
            parent( NodePath.create( "/s2/b2" ).build() ).
            name( "b2_2" ).
            build() );

        final NodeIds result = resolveSyncWorkResult( "a2_1" );

        assertEquals( 6, result.getSize() );

        /*
        assertRequested( requests, "a2_1" );
        assertReferredFrom( requests, "b2_1", "a2_1" );
        assertParentFor( requests, "b2", "b2_1" );
        assertParentFor( requests, "s2", "b2" );
        assertParentFor( requests, "a2", "a2_1" );
        assertParentFor( requests, "s1", "a2" );
        */
    }

    /*
     - S1 (New) s
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

        final NodeIds result = resolveSyncWorkResult( NodeId.from( "s1" ), true );

        assertEquals( 7, result.getSize() );
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
        Resolve: S1
    */
    @Test
    public void resolve_with_include_children()
        throws Exception
    {
        createS1S2Tree();

        final NodeIds result = resolveSyncWorkResult( NodeId.from( "s1" ), true );

        assertEquals( 7, result.getSize() );
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
            Resolve: S1
        */
    @Test
    public void resolve_with_include_children_a1()
        throws Exception
    {
        createS1S2Tree();

        final NodeIds result = resolveSyncWorkResult( NodeId.from( "a1" ), true );

        assertEquals( 2, result.getSize() );
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
         Resolve: A1
     */
    @Test
    public void resolve_with_include_children_a_2()
        throws Exception
    {
        createS1S2Tree();

        final NodeIds result = resolveSyncWorkResult( NodeId.from( "a2" ), true );

        assertEquals( 6, result.getSize() );
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
      */
    @Test
    public void make_sure_referred_nodes_includes_parents_if_missing()
    {
        createS1S2Tree();

        final NodeIds result = resolveSyncWorkResult( NodeId.from( "a2_1" ), true );

        assertEquals( 6, result.getSize() );
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
         */
    @Test
    public void publish_duplicate_of_original_do_not_publish_original()
        throws Exception
    {
        createS1S2Tree();

        final Node s1d = duplicateNode( getNodeById( NodeId.from( "s1" ) ) );

        final Node a1d = getNodeByPath( NodePath.create( "/s1-copy/a1" ).build() );
        final Node a2d = getNodeByPath( NodePath.create( "/s1-copy/a2" ).build() );
        final Node a2_1d = getNodeByPath( NodePath.create( "/s1-copy/a2/a2_1" ).build() );

        refresh();

        final NodeIds result = resolveSyncWorkResult( s1d.id(), true );

        assertEquals( 7, result.getSize() );

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

        final NodeIds result = ResolveSyncWorkCommand.create().
            includeChildren( true ).
            nodeId( node1.id() ).
            target( WS_OTHER ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertEquals( 5, result.getSize() );
    }

    @Test
    public void pending_delete_with_children_and_reference()
    {
        createS1S2Tree();

        NodeIds result = resolveSyncWorkResult( NodeId.from( "s1" ), true );
        assertEquals( 7, result.getSize() );

        pushAllNodesInS1S2Tree();
        markAsDelete( NodeId.from( "s1" ) );
        markAsDelete( NodeId.from( "s2" ) );

        result = resolveSyncWorkResult( "s1" );
        assertEquals( 4, result.getSize() );

        result = resolveSyncWorkResult( NodeId.from( "s1" ), true );
        assertEquals( 4, result.getSize() );
    }

    @Ignore("Just for development testing")
    @Test
    public void test_large_tree()
    {
        final Node rootNode = createNode( CreateNodeParams.create().
            name( "rootnode" ).
            setNodeId( NodeId.from( "rootnode" ) ).
            parent( NodePath.ROOT ).
            build(), false );

        final Stopwatch timer2 = Stopwatch.createStarted();

        for ( int i = 0; i <= 100; i++ )
        {
            final Node parent = createNode( CreateNodeParams.create().
                name( "myNode" + "-" + i ).
                setNodeId( NodeId.from( "myNode" + "-" + i ) ).
                parent( rootNode.path() ).
                build() );

            createChildren( parent.path(), 100 );
        }

        timer2.stop();
        System.out.println( timer2.toString() + " creating nodes" );

        refresh();

        final Stopwatch timer = Stopwatch.createStarted();
        final NodeIds nodeIds = resolveSyncWorkResult( rootNode.id(), true );
        timer.stop();
        System.out.println( timer.toString() + " diffing " + nodeIds.getSize() + " nodes" );
    }

    private void createChildren( final NodePath parent, final int numberOfChildren )
    {
        for ( int i = 0; i <= numberOfChildren; i++ )
        {
            createNode( CreateNodeParams.create().
                setNodeId( NodeId.from( parent.getLastElement() + "-" + i ) ).
                name( parent.getLastElement() + "-" + i ).
                parent( parent ).
                build(), false );
        }
    }


    private void createS1S2Tree()
    {
        final Node s1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "s1" ) ).
            parent( NodePath.ROOT ).
            name( "s1" ).
            build() );

        createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "a1" ) ).
            parent( s1.path() ).
            name( "a1" ).
            build() );

        final Node a2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "a2" ) ).
            parent( s1.path() ).
            name( "a2" ).
            build() );

        createNode( CreateNodeParams.create().
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

        createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "b1" ) ).
            parent( s2.path() ).
            name( "b1" ).
            build() );

        final Node b2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "b2" ) ).
            parent( s2.path() ).
            name( "b2" ).
            build() );

        createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "b2_1" ) ).
            parent( b2.path() ).
            name( "b2_1" ).
            build() );
    }

    private void pushAllNodesInS1S2Tree()
    {
        pushNodes( NodeIds.from( ROOT_UUID.toString(), "s1", "s2", "a1", "a2", "a2_1", "b1", "b2", "b2_1" ), WS_OTHER );
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

    private void markAsDelete( final NodeId id )
    {
        SetNodeStateParams setNodeStateParams =
            SetNodeStateParams.create().nodeId( id ).nodeState( NodeState.PENDING_DELETE ).recursive( true ).build();

        SetNodeStateCommand.create().
            params( setNodeStateParams ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        refresh();
    }

    void moveNode( final String nodeId, final NodePath newParent, final String newName )
    {
        MoveNodeCommand.create().
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            id( NodeId.from( nodeId ) ).
            newNodeName( NodeName.from( newName ) ).
            newParent( newParent ).
            build().
            execute();
    }


    private Node duplicateNode( final Node node1 )
    {
        return DuplicateNodeCommand.create().
            id( node1.id() ).
            indexServiceInternal( indexServiceInternal ).
            binaryBlobStore( this.binaryBlobStore ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    private PropertyTree createDataWithReferences( final Reference... references )
    {
        PropertyTree data = new PropertyTree();

        for ( final Reference reference : references )
        {
            data.setReference( reference.getNodeId().toString(), reference );
        }

        return data;
    }

    private void assertNodes( final NodePublishRequests nodePublishRequests, final String... nodeIds )
    {
        for ( final String nodeId : nodeIds )
        {
            assertNotNull( nodePublishRequests.get( NodeId.from( nodeId ) ) );
        }
    }

    private void assertNode( final NodePublishRequests nodePublishRequests, final String s1 )
    {
        assertNotNull( nodePublishRequests.get( NodeId.from( s1 ) ) );
    }

    private NodeIds resolveSyncWorkResult( final NodeId nodeId, final boolean includeChildren )
    {
        return ResolveSyncWorkCommand.create().
            nodeId( nodeId ).
            target( WS_OTHER ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            includeChildren( includeChildren ).
            build().
            execute();
    }

    private NodeIds resolveSyncWorkResult( final String nodeId )
    {
        return resolveSyncWorkResult( NodeId.from( nodeId ), false );
    }
}