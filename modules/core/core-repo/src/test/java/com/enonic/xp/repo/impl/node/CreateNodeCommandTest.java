package com.enonic.xp.repo.impl.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.InsertManualStrategy;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAlreadyExistAtPathException;
import com.enonic.xp.node.NodeBinaryReferenceException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.Reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CreateNodeCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }

    @Test
    public void path_is_case_insensitive()
        throws Exception
    {
        createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() );

        assertThrows(NodeAlreadyExistAtPathException.class, () -> createNode( CreateNodeParams.create().
            name( "MYNODE" ).
            parent( NodePath.ROOT ).
            build() ) );
    }

    @Test
    public void parent_must_exist()
        throws Exception
    {
        assertThrows(NodeNotFoundException.class, () -> createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.create( "/fisk" ).build() ).
            build() ) );
    }

    @Test
    public void case_insensitive_path()
        throws Exception
    {
        createNode( CreateNodeParams.create().
            name( "MyNode" ).
            parent( NodePath.ROOT ).
            build() );

        assertThrows(NodeAlreadyExistAtPathException.class, () -> createNode( CreateNodeParams.create().
            name( "mynode" ).
            parent( NodePath.ROOT ).
            build() ) );
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

    @Test
    public void attached_binary_not_given()
        throws Exception
    {
        PropertyTree data = new PropertyTree();
        data.setBinaryReference( "myCar", BinaryReference.from( "myImage" ) );

        assertThrows(NodeBinaryReferenceException.class, () -> createNode( CreateNodeParams.create().
            name( "test" ).
            parent( NodePath.ROOT ).
            data( data ).
            build() ) );
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

        assertNotNull( getNodeByPath( childNode.path() ) );
    }

    @Test
    public void node_with_reference()
    {
        final Node referredNode = createNode( CreateNodeParams.create().
            name( "test2" ).
            parent( NodePath.ROOT ).
            build() );

        PropertyTree data = new PropertyTree();
        data.setReference( "myCar", new Reference( referredNode.id() ) );

        createNode( CreateNodeParams.create().
            name( "test" ).
            parent( NodePath.ROOT ).
            data( data ).
            build() );

        final FindNodesByQueryResult result = FindNodesByQueryCommand.create().
            storageService( this.storageService ).
            searchService( this.searchService ).
            indexServiceInternal( this.indexServiceInternal ).
            query( NodeQuery.create().
                addQueryFilter( ValueFilter.create().
                    fieldName( NodeIndexPath.REFERENCE.getPath() ).
                    addValue( ValueFactory.newReference( Reference.from( referredNode.id().toString() ) ) ).
                    build() ).
                build() ).
            build().
            execute();

        assertEquals( 1, result.getHits() );
    }

    @Test
    public void create_node_with_same_path_in_two_branches_then_delete()
        throws Exception
    {
        final Node defaultNode = CTX_DEFAULT.callWith( () -> createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() ) );

        CTX_OTHER.callWith( this::createDefaultRootNode );

        CTX_OTHER.callWith( () -> createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() ) );

        CTX_OTHER.callWith( () -> doDeleteNode( defaultNode.id() ) );

        doDeleteNode( defaultNode.id() );
    }

}
