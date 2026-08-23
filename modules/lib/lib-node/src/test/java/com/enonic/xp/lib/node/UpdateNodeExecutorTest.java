package com.enonic.xp.lib.node;

import org.junit.jupiter.api.Test;

import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.EditableNode;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.util.GeoPoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UpdateNodeExecutorTest
{

    @Test
    void update_user_properties()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "notChanged", "originalValue" );
        data.setString( "myString", "originalValue" );
        final PropertySet mySet = data.addSet( "mySet" );
        mySet.setGeoPoint( "myGeoPoint", new GeoPoint( 30, -30 ) );

        final Node originalNode = Node.create().name( "myNode" ).parentPath( NodePath.ROOT ).data( data ).build();

        final EditableNode editableNode = new EditableNode( originalNode );

        final PropertyTree updateScript = createUpdateScript();

        UpdateNodeExecutor.create().editableNode( editableNode ).propertyTree( updateScript ).build().execute();

        assertEquals( new GeoPoint( 0, 0 ), editableNode.data.getGeoPoint( "mySet.myGeoPoint" ) );
        assertEquals( "updatedValue", editableNode.data.getString( "myString" ) );
    }

    private PropertyTree createUpdateScript()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "myString", "updatedValue" );
        final PropertySet mySet = data.addSet( "mySet" );
        mySet.setGeoPoint( "myGeoPoint", new GeoPoint( 0, 0 ) );
        return data;
    }

    @Test
    void update_child_order()
    {
        final Node originalNode = Node.create().name( "myNode" ).childOrder( ChildOrder.manualOrder() ).parentPath( NodePath.ROOT ).build();

        final EditableNode editableNode = new EditableNode( originalNode );

        final PropertyTree updateScript = new PropertyTree();
        updateScript.setString( "_childOrder", ChildOrder.name().toString() );

        UpdateNodeExecutor.create().editableNode( editableNode ).propertyTree( updateScript ).build().execute();

        assertEquals( ChildOrder.name(), editableNode.childOrder );
    }

    @Test
    void update_order_key()
    {
        final Node originalNode =
            Node.create().name( "myNode" ).parentPath( NodePath.ROOT ).orderKey( "3ala4x8dnbc.original" ).build();

        final EditableNode editableNode = new EditableNode( originalNode );

        final PropertyTree updateScript = new PropertyTree();
        updateScript.setString( "_orderKey", "3ala4x8dnbd.updated" );

        UpdateNodeExecutor.create().editableNode( editableNode ).propertyTree( updateScript ).build().execute();

        assertEquals( "3ala4x8dnbd.updated", editableNode.orderKey );
    }

    @Test
    void order_key_untouched_when_absent()
    {
        final Node originalNode =
            Node.create().name( "myNode" ).parentPath( NodePath.ROOT ).orderKey( "3ala4x8dnbc.original" ).build();

        final EditableNode editableNode = new EditableNode( originalNode );

        UpdateNodeExecutor.create().editableNode( editableNode ).propertyTree( new PropertyTree() ).build().execute();

        assertEquals( "3ala4x8dnbc.original", editableNode.orderKey );
    }

    @Test
    void remove_not_in_new_tree()
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "toBeRemoved", "removeMe" );

        final Node originalNode = Node.create().name( "myNode" ).parentPath( NodePath.ROOT ).data( data ).build();

        final EditableNode editableNode = new EditableNode( originalNode );

        final PropertyTree updateScript = new PropertyTree();

        UpdateNodeExecutor.create().editableNode( editableNode ).propertyTree( updateScript ).build().execute();

        assertNull( editableNode.data.getString( "toBeRemoved" ) );
    }


}
