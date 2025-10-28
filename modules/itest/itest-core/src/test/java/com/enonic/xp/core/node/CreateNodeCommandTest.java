package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.core.AbstractNodeTest;
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
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.filter.ValueFilter;
import com.enonic.xp.repo.impl.node.FindNodesByQueryCommand;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.Reference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CreateNodeCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void path_is_case_insensitive()
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
    void parent_must_exist()
    {
        assertThrows(NodeNotFoundException.class, () -> createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( new NodePath( "/fisk" ) ).
            build() ) );
    }

    @Test
    void case_insensitive_path()
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
    void timestamp_set()
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
    void populate_manual_order_value_and_insert_first()
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

        assertNotNull( c1.getManualOrderValue() );
        assertNotNull( c2.getManualOrderValue() );
        assertNotNull( c3.getManualOrderValue() );
        assertTrue( c1.getManualOrderValue() < c2.getManualOrderValue() );
        assertTrue( c2.getManualOrderValue() < c3.getManualOrderValue() );
    }

    @Test
    void populate_manual_order_value_and_insert_last()
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

        assertNotNull( c1.getManualOrderValue() );
        assertNotNull( c2.getManualOrderValue() );
        assertNotNull( c3.getManualOrderValue() );
        assertTrue( c1.getManualOrderValue() > c2.getManualOrderValue() );
        assertTrue( c2.getManualOrderValue() > c3.getManualOrderValue() );
    }

    @Test
    void attach_binary()
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
    void attach_binaries()
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
    void attached_binary_not_given()
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
    void norwegian_characters_in_name()
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
    void node_with_reference()
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
        nodeService.refresh( RefreshMode.ALL );

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

        assertEquals( 1, result.getNodeHits().getSize() );
    }

    @Test
    void create_node_with_same_path_in_two_branches_then_delete()
    {
        final Node defaultNode = ctxDefault().callWith( () -> createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() ) );

        ctxOther().callWith( this::createDefaultRootNode );

        ctxOther().callWith( () -> createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() ) );

        ctxOther().callWith( () -> doDeleteNode( defaultNode.id() ) );

        doDeleteNode( defaultNode.id() );
    }

}
