package com.enonic.xp.repo.impl.node;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

public class FindNodesByQueryCommandTest_order_boolean
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
    public void most_matching_boolean_expressions()
    {
        PropertyTree data1 = new PropertyTree();
        data1.addDouble( "double1", 1.0 );

        createNode( CreateNodeParams.create().
            name( "node1" ).
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            data( data1 ).
            build() );

        PropertyTree data2 = new PropertyTree();
        data2.addDouble( "double1", 1.0 );
        data2.addDouble( "double2", 2.0 );

        createNode( CreateNodeParams.create().
            name( "node2" ).
            setNodeId( NodeId.from( "node2" ) ).
            parent( NodePath.ROOT ).
            data( data2 ).
            build() );

        PropertyTree data3 = new PropertyTree();
        data3.addDouble( "double1", 1.0 );
        data3.addDouble( "double2", 2.0 );
        data3.addDouble( "double3", 3.0 );
        data3.addDouble( "double4", 4.0 );

        createNode( CreateNodeParams.create().
            name( "node3" ).
            setNodeId( NodeId.from( "node3" ) ).
            parent( NodePath.ROOT ).
            data( data3 ).
            build() );

        PropertyTree data4 = new PropertyTree();
        data4.addDouble( "double1", 1.0 );
        data4.addDouble( "double2", 2.0 );
        data4.addDouble( "double3", 3.0 );

        createNode( CreateNodeParams.create().
            name( "node4" ).
            setNodeId( NodeId.from( "node4" ) ).
            parent( NodePath.ROOT ).
            data( data4 ).
            build() );

        assertOrder( doQuery( "double1 = 1.0 OR double2 = 2.0 OR double3 = 3.0" ), "node3", "node4", "node2", "node1" );

    }


}
