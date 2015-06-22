package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
import org.junit.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.InsertManualStrategy;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBinaryReferenceException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.util.BinaryReference;

import static org.junit.Assert.*;

public class CreateNodeCommandTest
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
    public void populate_manual_order_value_and_insert_first()
        throws Exception
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "my-node" ) ).
            parent( NodePath.ROOT ).
            name( "my-node" ).
            childOrder( ChildOrder.manualOrder() ).
            build() );

        final Node c1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "child-node-1" ) ).
            parent( parentNode.path() ).
            name( "child-node-1" ).
            build() );

        final Node c2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "child-node-2" ) ).
            parent( parentNode.path() ).
            name( "child-node-2" ).
            build() );

        final Node c3 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "child-node-3" ) ).
            parent( parentNode.path() ).
            name( "child-node-3" ).
            build() );
        refresh();

        assertTrue( c1.getManualOrderValue() != null && c2.getManualOrderValue() != null && c3.getManualOrderValue() != null );
        assertTrue( c1.getManualOrderValue() < c2.getManualOrderValue() );
        assertTrue( c2.getManualOrderValue() < c3.getManualOrderValue() );
    }


    @Test
    public void populate_manual_order_value_and_insert_last()
        throws Exception
    {
        final Node parentNode = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "my-node" ) ).
            parent( NodePath.ROOT ).
            name( "my-node" ).
            childOrder( ChildOrder.manualOrder() ).
            build() );

        final Node c1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "child-node-1" ) ).
            parent( parentNode.path() ).
            name( "child-node-1" ).
            insertManualStrategy( InsertManualStrategy.LAST ).
            build() );

        final Node c2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "child-node-2" ) ).
            parent( parentNode.path() ).
            name( "child-node-2" ).
            insertManualStrategy( InsertManualStrategy.LAST ).
            build() );

        final Node c3 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "child-node-3" ) ).
            parent( parentNode.path() ).
            name( "child-node-3" ).
            insertManualStrategy( InsertManualStrategy.LAST ).
            build() );
        refresh();

        assertTrue( c1.getManualOrderValue() != null && c2.getManualOrderValue() != null && c3.getManualOrderValue() != null );
        assertTrue( c1.getManualOrderValue() > c2.getManualOrderValue() );
        assertTrue( c2.getManualOrderValue() > c3.getManualOrderValue() );
    }

    @Test
    public void attach_binary()
        throws Exception
    {
        PropertyTree data = new PropertyTree();
        data.setBinaryReference( "myCar", BinaryReference.from( "myImage" ) );

        final Node node = createNode( CreateNodeParams.create().
            name( "test" ).
            parent( NodePath.ROOT ).
            data( data ).
            attachBinary( BinaryReference.from( "myImage" ), ByteSource.empty() ).
            build() );

        final AttachedBinaries attachedBinaries = node.getAttachedBinaries();

        assertEquals( 1, attachedBinaries.getSize() );
    }

    @Test
    public void attach_binaries()
        throws Exception
    {
        PropertyTree data = new PropertyTree();
        data.setBinaryReference( "myCar", BinaryReference.from( "myImage" ) );
        data.setBinaryReference( "myOtherCar", BinaryReference.from( "myOtherImage" ) );

        final Node node = createNode( CreateNodeParams.create().
            name( "test" ).
            parent( NodePath.ROOT ).
            data( data ).
            attachBinary( BinaryReference.from( "myImage" ), ByteSource.wrap( "myImageBytes".getBytes() ) ).
            attachBinary( BinaryReference.from( "myOtherImage" ), ByteSource.wrap( "myOtherImageBytes".getBytes() ) ).
            build() );

        final AttachedBinaries attachedBinaries = node.getAttachedBinaries();

        assertEquals( 2, attachedBinaries.getSize() );
    }

    @Test(expected = NodeBinaryReferenceException.class)
    public void attached_binary_not_given()
        throws Exception
    {
        PropertyTree data = new PropertyTree();
        data.setBinaryReference( "myCar", BinaryReference.from( "myImage" ) );

        createNode( CreateNodeParams.create().
            name( "test" ).
            parent( NodePath.ROOT ).
            data( data ).
            build() );
    }

    @Test
    public void norwegian_characters_in_name()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "parent with Æ ø Å å" ).
            parent( NodePath.ROOT ).
            build() );

        final Node childNode = createNode( CreateNodeParams.create().
            name( "test øøå" ).
            parent( node.path() ).
            build() );

        printContentRepoIndex();

        assertNotNull( getNodeByPath( childNode.path() ) );
    }

    @Test
    public void timestamp_set()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() );

        assertNotNull( node.getTimestamp() );

        final Node storedNode = getNodeById( node.id() );

        assertNotNull( storedNode.getTimestamp() );
    }
}
