package com.enonic.wem.repo.internal.entity;

import java.util.Iterator;

import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.CreateRootNodeParams;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

import static org.junit.Assert.*;

public class FindNodesByParentCommandTest
    extends AbstractNodeTest
{

    @Test
    public void getChildren()
        throws Exception
    {
        this.createDefaultRootNode();

        final String nodeName = "my-node";
        final Node createdNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( nodeName ).
            build() );

        createNode( CreateNodeParams.create().
            parent( createdNode.path() ).
            name( "my-child1" ).
            build() );

        createNode( CreateNodeParams.create().
            parent( createdNode.path() ).
            name( "my-child2" ).
            build() );

        refresh();

        final FindNodesByParentResult children = findByParent( FindNodesByParentParams.create().
            parentPath( createdNode.path() ).
            build() );

        assertEquals( 2, children.getHits() );
        assertEquals( 2, children.getNodes().getSize() );
    }

    @Test
    public void size()
        throws Exception
    {
        this.createDefaultRootNode();

        final Node createdNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            build() );

        int size = 20;

        createChildren( createdNode, size );

        FindNodesByParentResult children = findByParent( FindNodesByParentParams.create().
            parentPath( createdNode.path() ).
            size( 9 ).
            build() );

        assertEquals( 9, children.getHits() );

        children = findByParent( FindNodesByParentParams.create().
            parentPath( createdNode.path() ).
            size( 100 ).
            build() );

        assertEquals( 20, children.getHits() );
    }

    @Test
    public void from()
        throws Exception
    {
        this.createDefaultRootNode();

        final Node createdNode = createNode( CreateNodeParams.create().
            parent( NodePath.ROOT ).
            name( "my-node" ).
            childOrder( ChildOrder.from( "myOrderNumber ASC" ) ).
            build() );

        int size = 20;

        createChildren( createdNode, size );

        FindNodesByParentResult children = findByParent( FindNodesByParentParams.create().
            parentPath( createdNode.path() ).
            size( 10 ).
            from( 0 ).
            build() );

        assertEquals( 10, children.getHits() );
        assertEquals( "my-child-0", children.getNodes().first().name().toString() );

        children = findByParent( FindNodesByParentParams.create().
            parentPath( createdNode.path() ).
            size( 10 ).
            from( 10 ).
            build() );

        assertEquals( 10, children.getHits() );
        assertEquals( "my-child-10", children.getNodes().first().name().toString() );
    }

    private void createChildren( final Node createdNode, final int size )
    {
        for ( int i = 0; i < size; i++ )
        {
            // Add numeric order value to ensure numeric ordering
            final PropertyTree data = new PropertyTree();
            data.setDouble( "myOrderNumber", (double) i );

            createNode( CreateNodeParams.create().
                parent( createdNode.path() ).
                name( "my-child-" + i ).
                data( data ).
                build() );
        }
    }

    @Test
    public void get_by_parent_one_child()
        throws Exception
    {
        this.createDefaultRootNode();

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
            branchService( branchService ).
            indexServiceInternal( indexServiceInternal ).
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
        this.createDefaultRootNode();

        final Node createdNode = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "parent" ) ).
            name( "my-node" ).
            parent( NodePath.ROOT ).
            childOrder( ChildOrder.from( NodeIndexPath.NAME + " ASC" ) ).
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
            branchService( branchService ).
            indexServiceInternal( indexServiceInternal ).
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
            branchService( branchService ).
            indexServiceInternal( indexServiceInternal ).
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

    private PropertyTree createOrderProperty( final Double value )
    {
        final PropertyTree rootDataSet = new PropertyTree();
        rootDataSet.setDouble( "order", value );
        return rootDataSet;
    }

    @Test
    public void child_order_use_root_node()
        throws Exception
    {
        CreateRootNodeCommand.create().
            params( CreateRootNodeParams.create().
                childOrder( ChildOrder.from( "order ASC" ) ).
                permissions( AccessControlList.of( AccessControlEntry.create().
                    allowAll().
                    principal( TEST_DEFAULT_USER.getKey() ).
                    build() ) ).
                build() ).
            queryService( this.queryService ).
            branchService( this.branchService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        final Node node1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1" ) ).
            name( "node1" ).
            parent( NodePath.ROOT ).
            data( createOrderProperty( 4.0 ) ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2" ) ).
            name( "node2" ).
            parent( NodePath.ROOT ).
            data( createOrderProperty( 1.0 ) ).
            build() );

        final Node node3 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node3" ) ).
            name( "node3" ).
            parent( NodePath.ROOT ).
            data( createOrderProperty( 3.0 ) ).
            build() );

        final FindNodesByParentResult result = findByParent( FindNodesByParentParams.create().
            parentPath( NodePath.ROOT ).
            build() );

        final Iterator<Node> iterator = result.getNodes().iterator();

        assertEquals( node2.id(), iterator.next().id() );
        assertEquals( node3.id(), iterator.next().id() );
        assertEquals( node1.id(), iterator.next().id() );

    }
}
