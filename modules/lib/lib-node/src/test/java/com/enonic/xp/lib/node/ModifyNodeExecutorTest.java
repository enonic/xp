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

public class ModifyNodeExecutorTest
{

    @Test
    public void modify_user_properties()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "notChanged", "originalValue" );
        data.setString( "myString", "originalValue" );
        final PropertySet mySet = data.addSet( "mySet" );
        mySet.setGeoPoint( "myGeoPoint", new GeoPoint( 30, -30 ) );

        final Node originalNode = Node.create().
            name( "myNode" ).
            parentPath( NodePath.ROOT ).
            data( data ).
            build();

        final EditableNode editableNode = new EditableNode( originalNode );

        final PropertyTree updateScript = createUpdateScript();

        ModifyNodeExecutor.create().
            editableNode( editableNode ).
            propertyTree( updateScript ).
            build().
            execute();

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
    public void update_child_order()
        throws Exception
    {
        final Node originalNode = Node.create().
            name( "myNode" ).
            childOrder( ChildOrder.manualOrder() ).
            parentPath( NodePath.ROOT ).
            build();

        final EditableNode editableNode = new EditableNode( originalNode );

        final PropertyTree updateScript = new PropertyTree();
        updateScript.setString( "_childOrder", ChildOrder.reverseManualOrder().toString() );

        ModifyNodeExecutor.create().
            editableNode( editableNode ).
            propertyTree( updateScript ).
            build().
            execute();

        assertEquals( ChildOrder.reverseManualOrder(), editableNode.childOrder );
    }

    @Test
    public void remove_not_in_new_tree()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "toBeRemoved", "removeMe" );

        final Node originalNode = Node.create().
            name( "myNode" ).
            parentPath( NodePath.ROOT ).
            data( data ).
            build();

        final EditableNode editableNode = new EditableNode( originalNode );

        final PropertyTree updateScript = new PropertyTree();

        ModifyNodeExecutor.create().
            editableNode( editableNode ).
            propertyTree( updateScript ).
            build().
            execute();

        assertNull( editableNode.data.getString( "toBeRemoved" ) );
    }


}
