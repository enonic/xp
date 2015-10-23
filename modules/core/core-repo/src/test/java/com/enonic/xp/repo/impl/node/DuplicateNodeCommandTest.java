package com.enonic.xp.repo.impl.node;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.Reference;

import static org.junit.Assert.*;

public class DuplicateNodeCommandTest
    extends AbstractNodeTest
{
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test
    public void duplicate_single()
        throws Exception
    {
        final String nodeName = "my-node";
        final Node createdNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( nodeName ).
            build() );

        final Node duplicatedNode = DuplicateNodeCommand.create().
            id( createdNode.id() ).
            indexServiceInternal( indexServiceInternal ).
            binaryBlobStore( binaryBlobStore ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertEquals( nodeName + "-" + DuplicateValueResolver.COPY_TOKEN, duplicatedNode.name().toString() );
    }

    @Test
    public void duplicate_with_children()
        throws Exception
    {
        final String nodeName = "my-node";
        final Node createdNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( nodeName ).
            build() );

        final Node childNode = createNode( CreateNodeParams.create().
            parent( createdNode.path() ).
            name( "my-child" ).
            build() );

        refresh();

        final Node duplicatedNode = DuplicateNodeCommand.create().
            id( createdNode.id() ).
            indexServiceInternal( indexServiceInternal ).
            binaryBlobStore( binaryBlobStore ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        refresh();

        final FindNodesByParentResult children = findByParent( FindNodesByParentParams.create().
            parentPath( duplicatedNode.path() ).
            build() );

        final Nodes childNodes = children.getNodes();
        assertEquals( 1, childNodes.getSize() );
        assertEquals( childNode.name(), childNodes.first().name() );
    }

    @Test
    public void child_reference_updated_same_level_untouched()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node1" ).
            data( createDataWithReferences( Reference.from( "node1_1-id" ), Reference.from( "node2-id" ) ) ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            setNodeId( NodeId.from( "node2-id" ) ).
            name( "node2" ).
            build() );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1-id" ) ).
            parent( node1.path() ).
            name( "node1_1" ).
            build() );

        final Node node1Duplicate = duplicateNode( node1 );

        final Node dNode1_1 = getNodeByPath( NodePath.create( node1Duplicate.path(), node1_1.name().toString() ).build() );
        assertEquals( dNode1_1.id(), node1Duplicate.data().getReference( "node1_1-id" ).getNodeId() );
        assertEquals( node2.id(), node1Duplicate.data().getReference( "node2-id" ).getNodeId() );
    }

    @Test
    public void subchild_reference_updated()
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

        final Node dNode1_1 = getNodeByPath( NodePath.create( node1Duplicate.path(), node1_1.name().toString() ).build() );
        final Reference reference = node1Duplicate.data().getReference( "node1_1-id" );
        assertEquals( dNode1_1.id(), reference.getNodeId() );

        final Node dNode1_1_1 = getNodeByPath( NodePath.create( dNode1_1.path(), node_1_1_1.name().toString() ).build() );
        assertEquals( dNode1_1.id(), node1Duplicate.data().getReference( "node1_1-id" ).getNodeId() );
        assertEquals( dNode1_1_1.id(), dNode1_1.data().getReference( "node1_1_1-id" ).getNodeId() );
        assertEquals( dNode1_1_1.id(), node1Duplicate.data().getReference( "node1_1_1-id" ).getNodeId() );
    }

    @Test
    public void child_in_other_branch_updated_reference()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "node1" ).
            build() );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1-id" ) ).
            parent( node1.path() ).
            name( "node1_1" ).
            data( createDataWithReferences( Reference.from( "node1_2_1-id" ) ) ).
            build() );

        final Node node1_2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_2-id" ) ).
            parent( node1.path() ).
            name( "node1_2" ).
            data( createDataWithReferences( Reference.from( "node1_1_1-id" ) ) ).
            build() );

        final Node node1_1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1_1-id" ) ).
            parent( node1_1.path() ).
            name( "node1_1_1" ).
            data( createDataWithReferences( Reference.from( "node1_2-id" ) ) ).
            data( createDataWithReferences( Reference.from( "node1_2_1-id" ) ) ).
            build() );

        final Node node1_2_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_2_1-id" ) ).
            parent( node1_2.path() ).
            name( "node1_2_1" ).
            data( createDataWithReferences( Reference.from( "node1_1-id" ) ) ).
            data( createDataWithReferences( Reference.from( "node1_1_1-id" ) ) ).
            build() );

        refresh();

        final Node duplicatedNode1 = duplicateNode( node1 );

        final Node dNode1_1 = getNodeByPath( NodePath.create( duplicatedNode1.path(), node1_1.name().toString() ).build() );
        final Node dNode1_2 = getNodeByPath( NodePath.create( duplicatedNode1.path(), node1_2.name().toString() ).build() );
        final Node dNode1_1_1 = getNodeByPath( NodePath.create( dNode1_1.path(), node1_1_1.name().toString() ).build() );
        final Node dNode1_2_1 = getNodeByPath( NodePath.create( dNode1_2.path(), node1_2_1.name().toString() ).build() );
        assertNotNull( dNode1_1 );
        assertNotNull( dNode1_2 );
        assertNotNull( dNode1_1_1 );
        assertNotNull( dNode1_2_1 );

        final Reference node1_1_1_ref_to_1_2_1 = dNode1_1_1.data().getReference( "node1_2_1-id" );
        final Reference node1_2_1_ref_to_1_1_1 = dNode1_2_1.data().getReference( "node1_1_1-id" );
        assertNotNull( node1_1_1_ref_to_1_2_1 );
        assertNotNull( node1_2_1_ref_to_1_1_1 );
        assertEquals( dNode1_2_1.id(), node1_1_1_ref_to_1_2_1.getNodeId() );
        assertEquals( dNode1_1_1.id(), node1_2_1_ref_to_1_1_1.getNodeId() );
    }

    @Test
    public void parent_relation_updated()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1-id" ) ).
            parent( NodePath.ROOT ).
            name( "node1" ).
            build() );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1-id" ) ).
            parent( node1.path() ).
            name( "node1_1" ).
            data( createDataWithReferences( Reference.from( "node1-id" ) ) ).
            build() );

        final Node node1Duplicate = duplicateNode( node1 );

        final Node dNode1_1 = getNodeByPath( NodePath.create( node1Duplicate.path(), node1_1.name().toString() ).build() );

        assertEquals( node1Duplicate.id(), dNode1_1.data().getReference( "node1-id" ).getNodeId() );
    }

    @Test
    public void duplicateNodeWithAttachments()
        throws Exception
    {
        final String nodeName = "my-node";

        final PropertyTree data = new PropertyTree();
        final BinaryReference binaryRef1 = BinaryReference.from( "image1.jpg" );
        final BinaryReference binaryRef2 = BinaryReference.from( "image2.jpg" );
        data.addBinaryReference( "my-image-1", binaryRef1 );
        data.addBinaryReference( "my-image-2", binaryRef2 );

        final Node createdNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( nodeName ).
            data( data ).
            attachBinary( binaryRef1, ByteSource.wrap( "this-is-the-binary-data-for-image1".getBytes() ) ).
            attachBinary( binaryRef2, ByteSource.wrap( "this-is-the-binary-data-for-image2".getBytes() ) ).
            build() );

        final Node duplicatedNode = DuplicateNodeCommand.create().
            id( createdNode.id() ).
            indexServiceInternal( indexServiceInternal ).
            binaryBlobStore( binaryBlobStore ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertEquals( nodeName + "-" + DuplicateValueResolver.COPY_TOKEN, duplicatedNode.name().toString() );
        assertEquals( 2, duplicatedNode.getAttachedBinaries().getSize() );
    }


    @Test
    public void manual_order_kept()
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );

        final Node childNode1 = createNode( CreateNodeParams.create().
            parent( parentNode.path() ).
            name( "child1" ).
            build() );

        final Node childNode2 = createNode( CreateNodeParams.create().
            parent( parentNode.path() ).
            name( "child2" ).
            build() );

        final Node childNode3 = createNode( CreateNodeParams.create().
            parent( parentNode.path() ).
            name( "child3" ).
            build() );

        setManualOrder( parentNode );

        assertOrder( parentNode, "child3", "child2", "child1" );

        put_child1_on_top( parentNode, childNode1, childNode3 );

        assertOrder( parentNode, "child1", "child3", "child2" );

        final Node duplicateParent = DuplicateNodeCommand.create().
            id( parentNode.id() ).
            indexServiceInternal( indexServiceInternal ).
            binaryBlobStore( binaryBlobStore ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertTrue( duplicateParent.getChildOrder().isManualOrder() );

        assertOrder( duplicateParent, "child1", "child3", "child2" );
    }

    private void assertOrder( final Node parentNode, final String first, final String second, final String third )
    {
        final FindNodesByParentResult children = findChildren( parentNode );

        final Iterator<Node> iterator = children.getNodes().iterator();

        assertEquals( first, iterator.next().name().toString() );
        assertEquals( second, iterator.next().name().toString() );
        assertEquals( third, iterator.next().name().toString() );
    }

    private void put_child1_on_top( final Node parentNode, final Node childNode1, final Node childNode3 )
    {
        ReorderChildNodeCommand.create().
            parentNode( getNodeById( parentNode.id() ) ).
            nodeToMove( getNodeById( childNode1.id() ) ).
            nodeToMoveBefore( getNodeById( childNode3.id() ) ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
        refresh();
    }

    private void setManualOrder( final Node parentNode )
    {
        SetNodeChildOrderCommand.create().
            nodeId( parentNode.id() ).
            childOrder( ChildOrder.manualOrder() ).
            indexServiceInternal( indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }

    private FindNodesByParentResult findChildren( final Node node )
    {
        return findByParent( FindNodesByParentParams.create().
            parentPath( node.path() ).
            build() );
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
}
