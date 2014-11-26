package com.enonic.wem.repo.internal.entity;

import java.util.Iterator;

import org.junit.Test;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.index.NodeIndexPaths;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.Nodes;

import static org.junit.Assert.*;

public class FindNodesByParentCommandTest
    extends AbstractNodeTest
{

    @Test
    public void get_by_parent_one_child()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            name( "my-node" ).
            parent( NodePath.ROOT ).
            build() );

        final Node childNode = createNode( CreateNodeParams.create().
            parent( createdNode.path() ).
            name( "my-child" ).
            build() );

        final FindNodesByParentResult result = FindNodesByParentCommand.create().
            params( FindNodesByParentParams.create().
                parentPath( createdNode.path() ).
                build() ).queryService( queryService ).
            workspaceService( workspaceService ).
            indexService( indexService ).
            versionService( versionService ).
            nodeDao( nodeDao ).
            build().
            execute();

        assertEquals( 1, result.getNodes().getSize() );
        assertEquals( childNode, result.getNodes().first() );
    }

    @Test
    public void get_children_order()
        throws Exception
    {
        final Node createdNode = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "parent" ) ).
            name( "my-node" ).
            parent( NodePath.ROOT ).
            childOrder( ChildOrder.from( NodeIndexPaths.NAME_PATH.toString() + " ASC" ) ).
            build() );

        final Node childNode_b_3 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "childnode_b_3" ) ).
            parent( createdNode.path() ).
            name( "b" ).
            data( createOrderProperty( 3.0 ) ).
            build() );

        final Node childNode_a_2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "childnode_a_2" ) ).
            parent( createdNode.path() ).
            name( "a" ).
            data( createOrderProperty( 2.0 ) ).
            build() );

        final Node childNode_c_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "childnode_c_1" ) ).
            parent( createdNode.path() ).
            name( "c" ).
            data( createOrderProperty( 1.0 ) ).
            build() );

        refresh();

        printContentRepoIndex();

        // Use default parent ordering; name
        FindNodesByParentResult result = FindNodesByParentCommand.create().
            params( FindNodesByParentParams.create().
                parentPath( createdNode.path() ).
                build() ).
            queryService( queryService ).
            workspaceService( workspaceService ).
            indexService( indexService ).
            versionService( versionService ).
            nodeDao( nodeDao ).
            build().
            execute();

        Nodes childNodes = result.getNodes();

        assertEquals( 3, childNodes.getSize() );
        Iterator<Node> iterator = childNodes.iterator();
        assertEquals( childNode_a_2, iterator.next() );
        assertEquals( childNode_b_3, iterator.next() );
        assertEquals( childNode_c_1, iterator.next() );

        // Override by specify childOrder-parameter by order-field
        result = FindNodesByParentCommand.create().
            params( FindNodesByParentParams.create().
                parentPath( createdNode.path() ).
                childOrder( ChildOrder.from( "order ASC" ) ).
                build() ).
            queryService( queryService ).
            workspaceService( workspaceService ).
            indexService( indexService ).
            versionService( versionService ).
            nodeDao( nodeDao ).
            build().
            execute();

        childNodes = result.getNodes();

        assertEquals( 3, childNodes.getSize() );
        iterator = childNodes.iterator();
        assertEquals( childNode_c_1, iterator.next() );
        assertEquals( childNode_a_2, iterator.next() );
        assertEquals( childNode_b_3, iterator.next() );
    }

    private RootDataSet createOrderProperty( final Double value )
    {
        final RootDataSet rootDataSet = new RootDataSet();
        rootDataSet.setProperty( "order", Value.newDouble( value ) );
        return rootDataSet;
    }

}
