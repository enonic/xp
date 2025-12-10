package com.enonic.xp.core.node;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.common.io.ByteSource;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueTypes;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.DuplicateNodeListener;
import com.enonic.xp.node.DuplicateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.OperationNotPermittedException;
import com.enonic.xp.node.ReorderChildNodeParams;
import com.enonic.xp.node.SortNodeParams;
import com.enonic.xp.repo.impl.node.DuplicateNodeCommand;
import com.enonic.xp.node.DuplicateNodeResult;
import com.enonic.xp.repo.impl.node.SortNodeCommand;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.Reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DuplicateNodeCommandTest
    extends AbstractNodeTest
{
    @Mock(strictness = Mock.Strictness.LENIENT)
    private DuplicateNodeListener duplicateNodeListener;

    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void duplicate_single()
    {
        final String nodeName = "my-node";
        final Node node = createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( nodeName ).build() );

        final Node duplicatedNode = duplicateNode( node ).getNode();

        assertDuplicatedTree( node.path(), node, duplicatedNode );

        assertEquals( nodeName + "-copy", duplicatedNode.name().toString() );
    }

    @Test
    void duplicate_twice()
    {
        final String nodeName = "my-node";
        final Node node = createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( nodeName ).build() );

        final DuplicateNodeResult duplicatedNode1 = duplicateNode( node );
        final DuplicateNodeResult duplicatedNode2 = duplicateNode( node );

        assertEquals( "my-node-copy", duplicatedNode1.getNode().name().toString() );
        assertEquals( "my-node-copy-2", duplicatedNode2.getNode().name().toString() );
    }

    @Test
    void with_children()
    {
        final String nodeName = "my-node";
        final Node node = createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( nodeName ).build() );

        createNode( CreateNodeParams.create().parent( node.path() ).name( "my-child" ).build() );

        final DuplicateNodeResult result = duplicateNode( node );

        assertDuplicatedTree( node.path(), node, result.getNode() );
        assertEquals( "my-child", result.getChildren().first().name().toString() );

        verify( duplicateNodeListener, times( 2 ) ).nodesDuplicated( anyInt() );
    }

    @Test
    void references_outside_tree()
    {
        final Node node1 = createNode( CreateNodeParams.create()
                                           .parent( NodePath.ROOT )
                                           .name( "node1" )
                                           .data( createDataWithReferences( Reference.from( "node1_1-id" ), Reference.from( "node2-id" ) ) )
                                           .build() );

        final Node node2 =
            createNode( CreateNodeParams.create().parent( NodePath.ROOT ).setNodeId( NodeId.from( "node2-id" ) ).name( "node2" ).build() );

        final Node node1_1 = createNode(
            CreateNodeParams.create().setNodeId( NodeId.from( "node1_1-id" ) ).parent( node1.path() ).name( "node1_1" ).build() );

        final DuplicateNodeResult result = duplicateNode( node1 );

        assertDuplicatedTree( node1.path(), node1, result.getNode() );
        assertEquals( 1, result.getChildren().getSize() );
    }

    @Test
    void subchild_with_reference_within_tree()
    {
        final Node node1 = createNode( CreateNodeParams.create()
                                           .parent( NodePath.ROOT )
                                           .name( "node1" )
                                           .data( createDataWithReferences( Reference.from( "node1_1-id" ),
                                                                            Reference.from( "node1_1_1-id" ) ) )
                                           .build() );

        final Node node1_1 = createNode( CreateNodeParams.create()
                                             .setNodeId( NodeId.from( "node1_1-id" ) )
                                             .parent( node1.path() )
                                             .name( "node1_1" )
                                             .data( createDataWithReferences( Reference.from( "node1_1_1-id" ) ) )
                                             .build() );

        createNode(
            CreateNodeParams.create().setNodeId( NodeId.from( "node1_1_1-id" ) ).parent( node1_1.path() ).name( "node1_1_1" ).build() );

        refresh();

        final Node node1Duplicate = duplicateNode( node1 ).getNode();

        assertDuplicatedTree( node1.path(), node1, node1Duplicate );
    }

    @Test
    void child_in_other_branch_updated_reference()
    {
        final Node node1 = createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( "node1" ).build() );

        final Node node1_1 = createNode( CreateNodeParams.create()
                                             .setNodeId( NodeId.from( "node1_1-id" ) )
                                             .parent( node1.path() )
                                             .name( "node1_1" )
                                             .data( createDataWithReferences( Reference.from( "node1_2_1-id" ) ) )
                                             .build() );

        final Node node1_2 = createNode( CreateNodeParams.create()
                                             .setNodeId( NodeId.from( "node1_2-id" ) )
                                             .parent( node1.path() )
                                             .name( "node1_2" )
                                             .data( createDataWithReferences( Reference.from( "node1_1_1-id" ) ) )
                                             .build() );

        createNode( CreateNodeParams.create()
                        .setNodeId( NodeId.from( "node1_1_1-id" ) )
                        .parent( node1_1.path() )
                        .name( "node1_1_1" )
                        .data( createDataWithReferences( Reference.from( "node1_2-id" ) ) )
                        .data( createDataWithReferences( Reference.from( "node1_2_1-id" ) ) )
                        .build() );

        createNode( CreateNodeParams.create()
                        .setNodeId( NodeId.from( "node1_2_1-id" ) )
                        .parent( node1_2.path() )
                        .name( "node1_2_1" )
                        .data( createDataWithReferences( Reference.from( "node1_1-id" ) ) )
                        .data( createDataWithReferences( Reference.from( "node1_1_1-id" ) ) )
                        .build() );

        refresh();

        final Node duplicatedNode1 = duplicateNode( node1 ).getNode();

        assertDuplicatedTree( node1.path(), node1, duplicatedNode1 );
    }

    @Test
    void relation_to_parent_updated_to_duplicated_parent()
    {
        final Node node1 =
            createNode( CreateNodeParams.create().setNodeId( NodeId.from( "node1-id" ) ).parent( NodePath.ROOT ).name( "node1" ).build() );

        final Node node1_1 = createNode( CreateNodeParams.create()
                                             .setNodeId( NodeId.from( "node1_1-id" ) )
                                             .parent( node1.path() )
                                             .name( "node1_1" )
                                             .data( createDataWithReferences( Reference.from( "node1-id" ) ) )
                                             .build() );

        final Node node1Duplicate = duplicateNode( node1 ).getNode();

        assertDuplicatedTree( node1.path(), node1, node1Duplicate );
    }

    @Test
    void attachments_duplicated()
    {
        final String nodeName = "my-node";

        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef1 = BinaryReference.from( "image1.jpg" );
        final BinaryReference binaryRef2 = BinaryReference.from( "image2.jpg" );
        data.addBinaryReference( "my-image-1", binaryRef1 );
        data.addBinaryReference( "my-image-2", binaryRef2 );

        final Node createdNode = createNode( CreateNodeParams.create()
                                                 .parent( NodePath.ROOT )
                                                 .name( nodeName )
                                                 .data( data )
                                                 .attachBinary( binaryRef1,
                                                                ByteSource.wrap( "this-is-the-binary-data-for-image1".getBytes() ) )
                                                 .attachBinary( binaryRef2,
                                                                ByteSource.wrap( "this-is-the-binary-data-for-image2".getBytes() ) )
                                                 .build() );

        final Node duplicatedNode = duplicateNode( createdNode ).getNode();

        assertDuplicatedTree( createdNode.path(), createdNode, duplicatedNode );
        assertEquals( nodeName + "-copy", duplicatedNode.name().toString() );
        assertEquals( 2, duplicatedNode.getAttachedBinaries().getSize() );
    }

    @Test
    void manual_order_kept()
    {
        final Node parentNode =
            createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( "my-node" ).build() );

        final Node childNode1 = createNode(
            CreateNodeParams.create().parent( parentNode.path() ).name( "child1" ).build() );

        createNode( CreateNodeParams.create().parent( parentNode.path() ).name( "child2" ).build() );

        final Node childNode3 = createNode(
            CreateNodeParams.create().parent( parentNode.path() ).name( "child3" ).build() );

        SortNodeCommand.create()
            .params( SortNodeParams.create()
                         .nodeId( parentNode.id() )
                         .childOrder( ChildOrder.manualOrder() )
                         .addManualOrder( ReorderChildNodeParams.create().nodeId( childNode1.id() ).moveBefore( childNode3.id() ).build() )
                         .build() )
            .indexServiceInternal( this.indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();

        assertOrder( parentNode, "child1", "child3", "child2" );

        final Node duplicateParent = duplicateNode( parentNode ).getNode();

        assertDuplicatedTree( parentNode.path(), parentNode, duplicateParent );

        assertTrue( duplicateParent.getChildOrder().isManualOrder() );

        assertOrder( duplicateParent, "child1", "child3", "child2" );
    }

    @Test
    void duplicate_with_capital_node_id()
    {
        final String nodeName = "my-node";
        final Node a =
            createNode( CreateNodeParams.create().parent( NodePath.ROOT ).name( nodeName ).setNodeId( NodeId.from( "MyNodeId" ) ).build() );

        createNode( CreateNodeParams.create().parent( a.path() ).name( "my-child" ).setNodeId( NodeId.from( "MyChildId" ) ).build() );

        final Node duplicatedNode = duplicateNode( a ).getNode();

        assertDuplicatedTree( a.path(), a, duplicatedNode );
    }

    @Test
    void cannot_duplicate_root_node()
    {
        assertThrows( OperationNotPermittedException.class, () -> duplicateNode( getNode( Node.ROOT_UUID ) ) );
    }

    @Test
    void reference_to_child_in_other_parent_within_duplicate_root()
    {
        final PropertyTree data = new PropertyTree();
        data.addReference( "refTo1_2_1", Reference.from( "a1_2_1" ) );

        final Node a1 = createNode( NodePath.ROOT, "a1" );

        createNode( CreateNodeParams.create().setNodeId( NodeId.from( "a1_1" ) ).name( "a1_1" ).parent( a1.path() ).data( data ).build() );

        final Node a1_2 = createNode( a1.path(), "a1_2" );
        createNode( a1_2.path(), "a1_2_1" );

        final Node a1Duplicate = duplicateNode( a1 ).getNode();

        refresh();
        final NodeIds children = findByParent( a1Duplicate.path() ).getNodeIds();
        assertEquals( 2, children.getSize() );

        assertDuplicatedTree( a1.path(), a1, a1Duplicate );
    }

    private void assertDuplicatedTree( final NodePath duplicatedTreeRootPath, final Node originalNode, final Node duplicatedNode )
    {
        final List<Property> originalReferences = originalNode.data().getProperties( ValueTypes.REFERENCE );
        final List<Property> duplicateReferences = duplicatedNode.data().getProperties( ValueTypes.REFERENCE );

        assertReferenceIntegrity( duplicatedTreeRootPath, originalReferences, duplicateReferences );

        refresh();
        final Nodes originalChildren = getNodes( findByParent( originalNode.path() ).getNodeIds() );
        final Nodes duplicatedChildren = getNodes( findByParent( duplicatedNode.path() ).getNodeIds() );

        assertEquals( originalChildren.getSize(), duplicatedChildren.getSize() );

        originalChildren.forEach( ( originalChild ) -> {
            final Node duplicatedChild = getNodeByName( duplicatedChildren, originalChild.name() );
            assertDuplicatedTree( duplicatedTreeRootPath, originalChild, duplicatedChild );
        } );
    }

    private Node getNodeByName( final Nodes nodes, final NodeName name )
    {
        Node foundNode = null;

        for ( final Node candidate : nodes )
        {
            if ( candidate.name().equals( name ) )
            {
                foundNode = candidate;
                break;
            }
        }

        if ( foundNode == null )
        {
            fail( "did not find duplicated child with name [" + name + "]" );
        }

        return foundNode;
    }

    private void assertReferenceIntegrity( final NodePath duplicatedRootPath, final List<Property> originalReferences, final List<Property> duplicateReferences )
    {
        assertEquals( originalReferences.size(), duplicateReferences.size() );

        final Set<Property> referencesWithinSameTree = originalReferences.stream()
            .filter( ( ref ) -> isWithinDuplicatedTree( duplicatedRootPath, ref ) )
            .collect( Collectors.toSet() );

        final Set<Property> referencesOutsideTree = originalReferences.stream()
            .filter( ( ref ) -> !isWithinDuplicatedTree( duplicatedRootPath, ref ) )
            .collect( Collectors.toSet() );

        assertReferencesInTreeMovedToDuplicatedNode( duplicateReferences, referencesWithinSameTree );
        assertReferencesOutsideTreeKept( duplicateReferences, referencesOutsideTree );
    }

    private boolean isWithinDuplicatedTree( final NodePath duplicatedRootPath, final Property ref )
    {
        final Value referredNode = ref.getValue();
        final NodePath referredPath = getNode( referredNode.asReference().getNodeId() ).path();
        return isChildOrSamePath( duplicatedRootPath, referredPath );
    }

    private void assertReferencesOutsideTreeKept( final List<Property> duplicateReferences, final Set<Property> referencesOutsideTree )
    {
        if ( !duplicateReferences.isEmpty() )
        {
            assertTrue( duplicateReferences.containsAll( referencesOutsideTree ), "Ref outside duplicate-tree updated" );
        }
    }

    private void assertReferencesInTreeMovedToDuplicatedNode( final List<Property> duplicateReferences, final Set<Property> referencesWithinSameTree )
    {
        for ( final Property ref : referencesWithinSameTree )
        {
            boolean found = false;

            for ( final Property dup : duplicateReferences )
            {
                if ( ref.getName().equals( dup.getName() ) )
                {
                    found = true;
                    assertNotEquals( ref.getReference().getNodeId(), dup.getReference().getNodeId(),
                                     "Reference within duplicated tree should be moved to duplicated child" );
                }
            }

            if ( !found )
            {
                fail( "Did not find reference within tree with name [ " + ref.getName() + "] in duplicated references" );
            }
        }
    }

    private boolean isChildOrSamePath( final NodePath parent, final NodePath candidate )
    {
        return parent.equals( candidate ) || candidate.getParentPaths().contains( parent );
    }

    private void assertOrder( final Node parentNode, final String... names )
    {
        refresh();
        assertThat( findByParent( parentNode.path() ).getNodeIds() ).map( this::getNode )
            .map( Node::name )
            .map( NodeName::toString )
            .containsExactly( names );
    }

    private DuplicateNodeResult duplicateNode( final Node node1 )
    {
        return DuplicateNodeCommand.create()
            .params( DuplicateNodeParams.create().nodeId( node1.id() ).duplicateListener( duplicateNodeListener ).build() )
            .indexServiceInternal( indexServiceInternal )
            .binaryService( this.binaryService )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();
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
}
