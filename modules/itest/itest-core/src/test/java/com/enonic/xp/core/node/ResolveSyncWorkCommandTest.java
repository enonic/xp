package com.enonic.xp.core.node;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DuplicateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.ResolveSyncWorkResult;
import com.enonic.xp.node.SetNodeChildOrderParams;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.repo.impl.node.DuplicateNodeCommand;
import com.enonic.xp.repo.impl.node.MoveNodeCommand;
import com.enonic.xp.repo.impl.node.ResolveSyncWorkCommand;
import com.enonic.xp.repo.impl.node.SortNodeCommand;
import com.enonic.xp.util.Reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ResolveSyncWorkCommandTest
    extends AbstractNodeTest
{

    private static final String LINE_SEPARATOR = System.getProperty( "line.separator" );

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
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
     * node2
     * .....|
     * .....node1                                   (Moved)
     * ..........|
     * ..........node1_1                            (Moved)
     * .................|
     * ..................node1_1_1                  (Moved)
     * ...........................|
     * ............................node1_1_1_1      (Moved)
     *
     * <p>
     * Contents created below look like pic above.
     * ResolveSyncWorkCommand will return all four when called both with includeChildren=true and with includeChildren=false
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

        final ResolveSyncWorkResult resultChildrenIncluded = resolveSyncWorkResult( node1_1_1_1.id(), true );
        final ResolveSyncWorkResult resultChildrenNotIncluded = resolveSyncWorkResult( node1_1_1_1.id(), false );

        assertEquals( resultChildrenIncluded.getSize(), 4 );
        assertEquals( resultChildrenNotIncluded.getSize(), 4 );
    }

    /**
     * node2
     * .....|
     * .....node1                                   (Moved)
     * ..........|
     * ..........node1_1                            (Moved)
     * .................|
     * ..................node1_1_1                  (Moved)
     * ...........................|
     * ............................node1_1_1_1      (Moved)
     * <p>
     * Contents created below look like pic above.
     * ResolveSyncWorkCommand will return node1_1_1 and two of its parents when called both with includeChildren=true and with includeChildren=false
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

        final ResolveSyncWorkResult resultChildrenIncluded = resolveSyncWorkResult( node1_1_1.id(), true );
        final ResolveSyncWorkResult resultChildrenNotIncluded = resolveSyncWorkResult( node1_1_1.id(), false );

        assertEquals( 4, resultChildrenIncluded.getSize() );

        //Should be 4 IMO. Has been discussed internally and left as it is for now
        assertEquals( 3, resultChildrenNotIncluded.getSize() );

    }

    /**
     * node1                          (Equal)
     * .....|
     * .....node1_1                   (Equal)
     * node2                          (Equal)
     * .....|
     * .....node1_1_1                 (Moved)
     * ..............|
     * ..............node1_1_1_1      (Moved)
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

        final ResolveSyncWorkResult resultChildrenIncluded = resolveSyncWorkResult( node1_1_1.id(), true );
        final ResolveSyncWorkResult resultChildrenNotIncluded = resolveSyncWorkResult( node1_1_1.id(), false );

        assertEquals( 2, resultChildrenIncluded.getSize() );

        //Should be 2 IMO. Has been discussed internally and left as it is for now
        assertEquals( 1, resultChildrenNotIncluded.getSize() );
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

        final Node node2_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2_1" ) ).
            parent( node2.path() ).
            name( "node2_1" ).
            build() );

        final ResolveSyncWorkResult result = resolveSyncWorkResult( node1_1.id(), false );

        assertNodes( result, ExpectedNodes.create().
            implicit( node1_1.id() ).
            parent( node1.id() ).
            referred( node2.id() ) );
    }


    /*
    - Node1
      - Node1_1 -> Ref2_1_1
    - Node2 -> Ref3
      - Node2_1
        - Node2_1_1
    - Node3 -> Ref:2_1
  */
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
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertNodes( result, ExpectedNodes.create().
            implicit( node1.id() ).
            child( node1_1.id() ).
            referred( node2_1_1.id(), node3.id() ).
            parent( node2_1.id(), node2.id() ) );
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

        final ResolveSyncWorkResult result = ResolveSyncWorkCommand.create().
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

        final ResolveSyncWorkResult results = resolveSyncWorkResult( getNodeByPath( NodePath.ROOT ).id(), true );

        assertEquals( 10, results.getSize() );

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

        moveNode( "b2_1", new NodePath( "/s2" ), "b2_1" );

        final ResolveSyncWorkResult result = resolveSyncWorkResult( "a2_1" );

        assertEquals( 2, result.getSize() );
    }

    /*
    - S1 (E)
        - A1 (E)
 	    - A2 (E)
 	        - A2_1 - Ref:B2_1 (M)
 	          - A2_1_1
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

        refresh();

        final ResolveSyncWorkResult result = resolveSyncWorkResult( "a2_1" );

        assertNodes( result, ExpectedNodes.create().
            implicit( "a2_1" ).
            referred( "b2_1" ) );

        assertEquals( 2, result.getSize() );
    }

    /*
        - S1 (E)
            - A1 (E)
            - A2 (E)
                - A2_1 - Ref:B2_1 (M)
                  - A2_1_1
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

        final ResolveSyncWorkResult result = resolveSyncWorkResult( "a2_1" );

        assertNodes( result, ExpectedNodes.create().
            implicit( "a2_1" ).
            referred( "b2_1" ).
            parent( "b2", "s2" ) );
    }

    @Test
    public void modified_parent_should_not_be_pushed()
        throws Exception
    {
        createS1S2Tree();

        pushAllNodesInS1S2Tree();

        updateNode( "a2" );
        updateNode( "a2_1" );

        refresh();

        final ResolveSyncWorkResult result = resolveSyncWorkResult( "a2_1" );

        assertNodes( result, ExpectedNodes.create().
            implicit( "a2_1" ) );

        assertEquals( 1, result.getSize() );
    }

    /*
     - S1 (New)
         - A1 (New)
         - A2 (New)
             - A2_1 - Ref:B2_1 (New)
               - A2_1_1
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
            parent( new NodePath( "/s2/b2" ) ).
            name( "b2_2" ).
            build() );

        final ResolveSyncWorkResult result = resolveSyncWorkResult( "a2_1" );

        assertEquals( 6, result.getSize() );
        assertNodes( result, ExpectedNodes.create().
            implicit( "a2_1" ).
            parent( "a2", "s1" ).
            referred( "b2_1" ).
            parent( "b2", "s2" ) );
    }

    /*
     - S1 (New)
         - A1 (New)
         - A2 (New)
             - A2_1 - Ref:B2_1 (New)
               - A2_1_1
     - S2 (New)
         - B1 (New)
         - B2 (New)
             - B2_1 (New)
             - B2_2 (New)

      Resolve: A2_1
    */
    @Test
    public void do_not_publish_dependencies_of_equal_parent()
        throws Exception
    {
        createS1S2Tree();
        pushAllNodesInS1S2Tree();

        updateNode( "a2_1_1" );
        moveNode( "b2_1", new NodePath( "/s2/b2" ), "b2_1_renamed" );

        final ResolveSyncWorkResult result = resolveSyncWorkResult( "a2_1_1" );
        assertNodes( result, ExpectedNodes.create().
            implicit( "a2_1_1" ) );
    }

    @Test
    public void do_not_publish_dependencies_of_modified_parent()
        throws Exception
    {
        createS1S2Tree();
        pushAllNodesInS1S2Tree();

        updateNode( "a2_1_1" );
        updateNode( "a2_1" );
        updateNode( "b2_1" );

        final ResolveSyncWorkResult result = resolveSyncWorkResult( "a2_1_1" );
        assertNodes( result, ExpectedNodes.create().
            implicit( "a2_1_1" ) );
    }

    @Test
    public void do_publish_new_dependencies_of_moved_parent()
        throws Exception
    {
        createS1S2Tree();
        pushAllNodesInS1S2Tree();

        updateNode( "a2_1_1" );
        renameNode( NodeId.from( "a2_1" ), "newName" );

        createNode( NodePath.ROOT, "s3" );

        // Update parent node with new reference
        updateNode( UpdateNodeParams.create().
            id( NodeId.from( "a2_1" ) ).
            editor( ( node ) -> {
                node.data.addReference( "newRef", Reference.from( "s3" ) );
            } ).
            build() );

        updateNode( "b2_1" );

        final ResolveSyncWorkResult result = resolveSyncWorkResult( "a2_1_1" );
        assertNodes( result, ExpectedNodes.create().
            parent( "a2_1" ).
            referred( "s3" ).
            implicit( "a2_1_1" ) );
    }

    /*
     - S1 (New) s
         - A1 (New)
         - A2 (New)
             - A2_1 - Ref:B2_1 (New)
               - A2_1_1
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

        final ResolveSyncWorkResult result = resolveSyncWorkResult( NodeId.from( "s1" ), true );

        assertEquals( 8, result.getSize() );
    }

    /*
        - S1 (New)
         - A1 (New)
         - A2 (New)
             - A2_1 - Ref:B2_1 (New)
               - A2_1_1
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

        final ResolveSyncWorkResult result = resolveSyncWorkResult( NodeId.from( "s1" ), true );

        assertEquals( 8, result.getSize() );
    }

    /*
            - S1 (New)
             - A1 (New)
             - A2 (New)
                 - A2_1 - Ref:B2_1 (New)
                   - A2_1_1
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

        final ResolveSyncWorkResult result = resolveSyncWorkResult( NodeId.from( "a1" ), true );

        assertEquals( 2, result.getSize() );
    }

    /*
         - S1 (New)
          - A1 (New)
          - A2 (New)
              - A2_1 - Ref:B2_1 (New)
                - A2_1_1
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

        final ResolveSyncWorkResult result = resolveSyncWorkResult( NodeId.from( "a2" ), true );

        assertEquals( 7, result.getSize() );
    }

    /*
      - S1 (New)
       - A1 (New)
       - A2 (New)
           - A2_1 - Ref:B2_1 (New)
             - A2_1_1
      - S2 (New)
       - B1 (New)
       - B2 (New)
           - B2_1 (New)
      */
    @Test
    public void make_sure_referred_nodes_includes_parents_if_missing()
    {
        createS1S2Tree();

        final ResolveSyncWorkResult result = resolveSyncWorkResult( NodeId.from( "a2_1" ), true );

        assertNodes( result, ExpectedNodes.create().
            implicit( "a2_1" ).
            child( "a2_1_1" ).
            parent( "a2", "s1" ).
            referred( "b2_1" ).
            parent( "b2", "s2" ) );
    }


    /*
    - S1 (New)
     - A1 (New)
     - A2 (New)
         - A2_1 - Ref:B2_1 (New)
           - A2_1_1
    - S2 (New)
     - B1 (New)
     - B2 (New)
         - B2_1 (New)
    */
    @Test
    public void make_missing_parents_references_parents_nodes_included()
    {
        createS1S2Tree();

        final ResolveSyncWorkResult result = resolveSyncWorkResult( NodeId.from( "a2_1_1" ), true );

        assertNodes( result, ExpectedNodes.create().
            implicit( "a2_1_1" ).
            parent( "a2_1", "a2", "s1" ).
            referred( "b2_1" ).
            parent( "b2", "s2" ) );
    }

    /*
         - S1 (New)
           - A1 (New)
           - A2 (New)
              - A2_1 - Ref:B2_1 (New)
                - A2_1_1
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

        getNodeByPath( new NodePath( "/s1-copy/a1" ) );
        getNodeByPath( new NodePath( "/s1-copy/a2" ) );
        getNodeByPath( new NodePath( "/s1-copy/a2/a2_1" ) );

        refresh();

        final ResolveSyncWorkResult result = resolveSyncWorkResult( s1d.id(), true );

        assertEquals( 8, result.getSize() );

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
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertEquals( 5, result.getSize() );
    }

    @Test
    public void exclude_empty()
    {
        createS1S2Tree();
        refresh();

        ResolveSyncWorkResult result = resolveSyncWorkResult( NodeId.from( "s1" ), NodeIds.empty(), false );
        assertEquals( 1, result.getSize() );
        assertNodes( result, ExpectedNodes.create().
            implicit( "s1" ) );

        result = resolveSyncWorkResult( NodeId.from( "s1" ), NodeIds.empty(), true );
        assertEquals( 8, result.getSize() );

    }

    /*
        - S1
          - A1
          - A2
             - A2_1 - Ref:B2_1
        - S2
          - B1
          - B2
             - B2_1
        - S1d (New)
          - A1d
          - A2d
            - A2_1 - Ref:B2_1
        */
    @Test
    public void exclude_itself()
    {
        createS1S2Tree();
        refresh();

        ResolveSyncWorkResult result = resolveSyncWorkResult( NodeId.from( "s1" ), NodeIds.from( "s1" ), true );

        assertNodes( result, ExpectedNodes.create().
            child( "a1", "a2", "a2_1", "a2_1_1" ).
            referred( "b2_1" ).
            parent( "b2", "s2", "s1" ) );

        result = resolveSyncWorkResult( NodeId.from( "s1" ), NodeIds.from( "s1" ), false );

        assertNodes( result, ExpectedNodes.create() );
    }

    @Test
    public void exclude_all()
    {
        createS1S2Tree();
        refresh();

        ResolveSyncWorkResult result =
            resolveSyncWorkResult( NodeId.from( "s1" ), NodeIds.from( "s1", "a1", "a2", "a2_1", "a2_1_1", "b2_1", "b2", "s2" ), true );
        assertEquals( 0, result.getNodeComparisons().getSize() );
    }

    @Test
    public void exclude_all_children()
    {
        createS1S2Tree();
        refresh();

        final NodeIds children = NodeIds.from( "a1", "a2", "a2_1", "a2_1_1", "b2_1", "b2", "s2" );

        ResolveSyncWorkResult result = resolveSyncWorkResult( NodeId.from( "s1" ), children, false );
        assertNodes( result, ExpectedNodes.create().
            implicit( "s1" ) );

        result = resolveSyncWorkResult( NodeId.from( "s1" ), children, true );
        assertNodes( result, ExpectedNodes.create().
            implicit( "s1" ) );
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
    public void exclude_child_with_refs()
    {
        createS1S2Tree();
        refresh();

        final ResolveSyncWorkResult result = resolveSyncWorkResult( NodeId.from( "s1" ), NodeIds.from( "a2_1_1", "a2_1" ), true );

        assertNodes( result, ExpectedNodes.create().
            implicit( "s1" ).
            child( "a1", "a2" ) );
    }

    @Test
    public void exclude_referencies()
    {
        createS1S2Tree();
        refresh();

        final ResolveSyncWorkResult result = resolveSyncWorkResult( NodeId.from( "s1" ), NodeIds.from( "b2_1" ), true );

        assertNodes( result, ExpectedNodes.create().
            implicit( "s1" ).
            child( "a1", "a2", "a2_1", "a2_1_1" ) );
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
    public void exclude_referencies_in_the_middle()
    {
        createS1S2Tree();
        refresh();

        final ResolveSyncWorkResult result = resolveSyncWorkResult( NodeId.from( "s1" ), true );

        assertNodes( result, ExpectedNodes.create().
            implicit( "s1" ).
            child( "a1", "a2", "a2_1", "a2_1_1" ).
            referred( "b2_1" ).
            parent( "b2", "s2" ) );
    }


    /*
     * s1
     ** a1
     ** a2
     *** a2_1 -> b2_1
     **** a2_1_1
     * s2
     ** b1
     ** b2
     *** b2_1
     */
    @Test
    public void not_include_dependencies()
    {
        createS1S2Tree();
        refresh();

        final ResolveSyncWorkResult result = resolveSyncWorkResult( NodeId.from( "s1" ), NodeIds.empty(), true, false );

        assertNodes( result, ExpectedNodes.create().
            implicit( "s1" ).
            child( "a1", "a2", "a2_1", "a2_1_1" ) );
    }


    @Test
    public void change_child_order_manual_yields_children_changed()
    {
        final Node node1 = createNode( NodePath.ROOT, "node1" );
        createNode( node1.path(), "node1_1" );
        createNode( node1.path(), "node1_2" );
        createNode( node1.path(), "node1_3" );
        createNode( node1.path(), "node1_4" );
        refresh();

        pushNodes( WS_OTHER, Node.ROOT_UUID, NodeId.from( "node1" ), NodeId.from( "node1_1" ), NodeId.from( "node1_2" ),
                   NodeId.from( "node1_3" ), NodeId.from( "node1_4" ) );

        SortNodeCommand.create()
            .params( SetNodeChildOrderParams.create().nodeId( node1.id() ).childOrder( ChildOrder.manualOrder() ).build() )
            .indexServiceInternal( this.indexServiceInternal )
            .searchService( this.searchService )
            .storageService( this.storageService )
            .build()
            .execute();

        final ResolveSyncWorkResult result = resolveSyncWorkResult( node1.id(), NodeIds.empty(), true, false );

        assertNodes( result, ExpectedNodes.create().
            implicit( node1.id() ).
            child( "node1_1", "node1_2", "node1_3", "node1_4" ) );
    }

    @Test
    void resolve_with_more_than_1024_excluded_nodes_at_once()
    {
        final Node rootNode = createNodeSkipVerification( CreateNodeParams.create().name( "rootNode" ).parent( NodePath.ROOT ).build() );

        final Set<NodeId> childrenIds = new HashSet<>();

        createNodes( rootNode, 10, 3, 1, ( child ) -> childrenIds.add( child.id() ) );

        refresh();

        final ResolveSyncWorkResult syncWork = ResolveSyncWorkCommand.create()
            .nodeId( rootNode.id() )
            .target( WS_OTHER )
            .excludedNodeIds( NodeIds.from( childrenIds ) )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();

        assertEquals( 1, syncWork.getSize() );
    }

    /*
     * s1
     ** a1
     ** a2
     *** a2_1 -> b2_1
     **** a2_1_1
     * s2
     ** b1
     ** b2
     *** b2_1
     */
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

        final Node a2_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "a2_1" ) ).
            parent( a2.path() ).
            data( createDataWithReferences( Reference.from( "b2_1" ) ) ).
            name( "a2_1" ).
            build() );

        createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "a2_1_1" ) ).
            parent( a2_1.path() ).
            name( "a2_1_1" ).
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
        pushNodes( WS_OTHER, Node.ROOT_UUID, NodeId.from( "s1" ), NodeId.from( "s2" ), NodeId.from( "a1" ), NodeId.from( "a2" ),
                   NodeId.from( "a2_1" ), NodeId.from( "b1" ), NodeId.from( "b2" ), NodeId.from( "b2_1" ) );
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

    private void moveNode( final String nodeId, final NodePath newParent, final String newName )
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
        return DuplicateNodeCommand.create()
            .params( DuplicateNodeParams.create().nodeId( node1.id() ).build() )
            .indexServiceInternal( indexServiceInternal )
            .binaryService( this.binaryService )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute()
            .getNode();
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

    private ResolveSyncWorkResult resolveSyncWorkResult( final NodeId nodeId, final NodeIds excludeIds, final boolean includeChildren )
    {
        return resolveSyncWorkResult( nodeId, excludeIds, includeChildren, true );
    }

    private ResolveSyncWorkResult resolveSyncWorkResult( final NodeId nodeId, final NodeIds excludeIds, final boolean includeChildren,
                                                         final boolean includeReferences )
    {
        return ResolveSyncWorkCommand.create().
            nodeId( nodeId ).
            target( WS_OTHER ).
            includeDependencies( includeReferences ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            includeChildren( includeChildren ).
            excludedNodeIds( excludeIds ).
            build().
            execute();
    }

    private ResolveSyncWorkResult resolveSyncWorkResult( final NodeId nodeId, final boolean includeChildren )
    {
        return resolveSyncWorkResult( NodeId.from( nodeId ), NodeIds.empty(), includeChildren );
    }

    private ResolveSyncWorkResult resolveSyncWorkResult( final String nodeId )
    {
        return resolveSyncWorkResult( NodeId.from( nodeId ), false );
    }

    private void assertNodes( final ResolveSyncWorkResult result, final ExpectedNodes expectedNodes )
    {
        doAssertNodes( result.getNodeComparisons().getNodeIds(), expectedNodes );
    }


    private void assertNodes( final NodeIds result, final ExpectedNodes expectedNodes )
    {
        doAssertNodes( result, expectedNodes );
    }

    private void doAssertNodes( final NodeIds result, final ExpectedNodes expectedNodes )
    {
        boolean fail = false;

        StringBuilder builder = new StringBuilder();
        builder.append( LINE_SEPARATOR );
        builder.append( "Asserted size [" ).append( expectedNodes.nodes.size() ).append( "], actual [" ).append( result.getSize() ).append(
            "]" );
        builder.append( LINE_SEPARATOR );

        for ( final ExpectedNode expectedNode : expectedNodes )
        {
            builder.append( LINE_SEPARATOR );
            final boolean ok = result.contains( expectedNode.nodeId );
            if ( !ok )
            {
                fail = true;
            }

            builder.append( "Expected: " ).append( expectedNode ).append( ", " ).append( ok ? "<OK>" : "<Missing>" );
        }

        for ( final NodeId resultNode : result )
        {
            final boolean ok = expectedNodes.contains( resultNode );

            if ( !ok )
            {
                fail = true;
            }

            if ( !ok )
            {
                builder.append( LINE_SEPARATOR );
                builder.append( "Unexpected: " ).append( resultNode );
            }
        }

        if ( fail )
        {
            fail( builder.toString() );
        }

        assertEquals( expectedNodes.nodes.size(), result.getSize() );
    }

    private enum Reason
    {
        PARENT, CHILD, REFERRED, IMPLICIT
    }

    private static class ExpectedNodes
        implements Iterable<ExpectedNode>
    {
        final Set<ExpectedNode> nodes = new HashSet<>();

        public static ExpectedNodes create()
        {
            return new ExpectedNodes();
        }

        @Override
        public Iterator<ExpectedNode> iterator()
        {
            return nodes.iterator();
        }

        public boolean contains( final NodeId nodeId )
        {
            for ( final ExpectedNode node : nodes )
            {
                if ( node.nodeId.equals( nodeId ) )
                {
                    return true;
                }
            }

            return false;
        }


        public ExpectedNodes implicit( final NodeId... nodeIds )
        {
            for ( final NodeId nodeId : nodeIds )
            {
                this.nodes.add( new ExpectedNode( nodeId, Reason.IMPLICIT ) );
            }

            return this;
        }

        public ExpectedNodes implicit( final String... nodeIds )
        {
            for ( final String nodeId : nodeIds )
            {
                this.nodes.add( new ExpectedNode( NodeId.from( nodeId ), Reason.IMPLICIT ) );
            }

            return this;
        }

        public ExpectedNodes child( final String... nodeIds )
        {
            for ( final String nodeId : nodeIds )
            {
                this.nodes.add( new ExpectedNode( NodeId.from( nodeId ), Reason.CHILD ) );
            }

            return this;
        }

        public ExpectedNodes child( final NodeId... nodeIds )
        {
            for ( final NodeId nodeId : nodeIds )
            {
                this.nodes.add( new ExpectedNode( nodeId, Reason.CHILD ) );
            }

            return this;
        }

        public ExpectedNodes referred( final NodeId... nodeIds )
        {
            for ( final NodeId nodeId : nodeIds )
            {
                this.nodes.add( new ExpectedNode( nodeId, Reason.REFERRED ) );
            }
            return this;
        }

        public ExpectedNodes referred( final String... nodeIds )
        {
            for ( final String nodeId : nodeIds )
            {
                this.nodes.add( new ExpectedNode( NodeId.from( nodeId ), Reason.REFERRED ) );
            }
            return this;
        }

        public ExpectedNodes parent( final String... nodeIds )
        {
            for ( final String nodeId : nodeIds )
            {
                this.nodes.add( new ExpectedNode( NodeId.from( nodeId ), Reason.PARENT ) );
            }

            return this;
        }


        public ExpectedNodes parent( final NodeId... nodeIds )
        {
            for ( final NodeId nodeId : nodeIds )
            {
                this.nodes.add( new ExpectedNode( nodeId, Reason.PARENT ) );
            }

            return this;
        }

        @Override
        public String toString()
        {
            return this.nodes.stream().
                map( Objects::toString ).
                collect( Collectors.joining( ", " ) );
        }
    }

    private static class ExpectedNode
    {
        private final Reason reason;

        private final NodeId nodeId;

        public ExpectedNode( final NodeId nodeId, final Reason reason )
        {
            this.reason = reason;
            this.nodeId = nodeId;
        }

        @Override
        public String toString()
        {
            return this.nodeId + " (" + reason.name() + ")";
        }
    }

}
