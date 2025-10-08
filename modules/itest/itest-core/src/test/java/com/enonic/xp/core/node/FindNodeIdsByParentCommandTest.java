package com.enonic.xp.core.node;

import java.io.IOException;
import java.util.Iterator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
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
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.repo.impl.node.CreateRootNodeCommand;
import com.enonic.xp.repo.impl.node.FindNodeIdsByParentCommand;
import com.enonic.xp.repo.impl.node.GetNodesByIdsCommand;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FindNodeIdsByParentCommandTest
    extends AbstractNodeTest
{

    @BeforeEach
    public void setUp()
        throws Exception
    {
        this.createDefaultRootNode();
    }


    @Test
    public void getChildren()
        throws Exception
    {

        final Node root = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            name( "node1" ).
            build() );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1" ) ).
            parent( root.path() ).
            name( "node1_1" ).
            build() );

        createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1_1" ) ).
            parent( node1_1.path() ).
            name( "node1_1_1" ).
            build() );

        nodeService.refresh( RefreshMode.ALL );

        final FindNodesByParentResult result = FindNodeIdsByParentCommand.create().
            parentPath( root.path() ).
            recursive( true ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        assertEquals( 2, result.getNodeIds().getSize() );
    }


    @Test
    public void find_after_move_to_folder_with_same_name()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node" ) ).
            parent( NodePath.ROOT ).
            name( "node" ).
            build() );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1" ) ).
            parent( node.path() ).
            name( "node1_1" ).
            build() );

        createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1_1" ) ).
            parent( node1_1.path() ).
            name( "node1_1_1" ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            name( "node1" ).
            build() );

        nodeService.refresh( RefreshMode.ALL );

        moveNode( node1_1.id(), node2.path() );

        final FindNodesByParentResult children = FindNodeIdsByParentCommand.create().
            parentPath( node.path() ).
            recursive( true ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        assertEquals( 0, children.getNodeIds().getSize() );
    }

    @Test
    public void findByParent_recursive()
        throws IOException
    {
        /*
                |-node1
                    |-node1_1
                        |-node1_1_1
                            |-node1_1_1_1
                    |-node1_1_dummy
                    |-node1_2
         */

        final Node node = createNode( NodePath.ROOT, "node1" );
        final Node node1_1 = createNode( node.path(), "node1_1" );
        createNode( node.path(), "node1_1_dummy" );
        createNode( node.path(), "node1_2" );
        final Node node1_1_1 = createNode( node1_1.path(), "node1_1_1" );
        final Node node_1_1_1_1 = createNode( node1_1_1.path(), "node1_1_1_1" );
        nodeService.refresh( RefreshMode.ALL );

        assertEquals( 6, getByParentRecursive( Node.ROOT_UUID ).getTotalHits() );
        assertEquals( 5, getByParentRecursive( node.id() ).getTotalHits() );
        assertEquals( 2, getByParentRecursive( node1_1.id() ).getTotalHits() );
        assertEquals( 1, getByParentRecursive( node1_1_1.id() ).getTotalHits() );
        assertEquals( 0, getByParentRecursive( node_1_1_1_1.id() ).getTotalHits() );
    }

    @Test
    public void getChildren_2()
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

        assertEquals( 2, children.getTotalHits() );
        assertEquals( 2, children.getNodeIds().getSize() );
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
        nodeService.refresh( RefreshMode.ALL );

        FindNodesByParentResult children = findByParent( FindNodesByParentParams.create().
            parentPath( createdNode.path() ).
            size( 9 ).
            build() );

        assertEquals( 9, children.getNodeIds().getSize() );

        children = findByParent( FindNodesByParentParams.create().
            parentPath( createdNode.path() ).
            size( 100 ).
            build() );

        assertEquals( 20, children.getNodeIds().getSize() );
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
        nodeService.refresh( RefreshMode.ALL );

        FindNodesByParentResult children = findByParent( FindNodesByParentParams.create().
            parentPath( createdNode.path() ).
            size( 10 ).
            from( 0 ).
            build() );

        assertEquals( 10, children.getNodeIds().getSize() );

        assertEquals( "my-child-0", getNode( children.getNodeIds().first() ).name().toString() );

        children = findByParent( FindNodesByParentParams.create().
            parentPath( createdNode.path() ).
            size( 10 ).
            from( 10 ).
            build() );

        assertEquals( 10, children.getNodeIds().getSize() );
        assertEquals( "my-child-10", getNode( children.getNodeIds().first() ).name().toString() );
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
                setNodeId( NodeId.from( "my-child-" + i ) ).
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
        nodeService.refresh( RefreshMode.ALL );

        final FindNodesByParentResult result = findByParent( FindNodesByParentParams.create().
            parentPath( createdNode.path() ).
            build() );

        assertEquals( 1, result.getNodeIds().getSize() );
        assertEquals( childNode, getNode( result.getNodeIds().first() ) );
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

        // Use default parent ordering; name
        FindNodesByParentResult result = findByParent( FindNodesByParentParams.create().
            parentPath( createdNode.path() ).
            build() );

        Nodes childNodes = GetNodesByIdsCommand.create().
            ids( result.getNodeIds() ).
            indexServiceInternal( indexServiceInternal ).
            storageService( storageService ).
            searchService( searchService ).
            build().
            execute();

        assertEquals( 3, childNodes.getSize() );
        Iterator<Node> iterator = childNodes.iterator();
        assertEquals( childNode_a_2, iterator.next() );
        assertEquals( childNode_b_3, iterator.next() );
        assertEquals( childNode_c_1, iterator.next() );

        // Override by specify childOrder-parameter by order-field
        result = findByParent( FindNodesByParentParams.create().
            parentPath( createdNode.path() ).
            childOrder( ChildOrder.from( "order ASC" ) ).
            build() );

        childNodes = GetNodesByIdsCommand.create().
            ids( result.getNodeIds() ).
            indexServiceInternal( indexServiceInternal ).
            storageService( storageService ).
            searchService( searchService ).
            build().
            execute();

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
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
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
        nodeService.refresh( RefreshMode.ALL );

        final FindNodesByParentResult result = findByParent( FindNodesByParentParams.create().
            parentPath( NodePath.ROOT ).
            build() );

        final Iterator<Node> iterator = GetNodesByIdsCommand.create().
            ids( result.getNodeIds() ).
            indexServiceInternal( indexServiceInternal ).
            storageService( storageService ).
            searchService( searchService ).
            build().
            execute().
            iterator();

        assertEquals( node2.id(), iterator.next().id() );
        assertEquals( node3.id(), iterator.next().id() );
        assertEquals( node1.id(), iterator.next().id() );

    }

    private FindNodesByParentResult findByParent( final FindNodesByParentParams params )
    {
        return FindNodeIdsByParentCommand.create()
            .parentId( params.getParentId() )
            .parentPath( params.getParentPath() )
            .recursive( params.isRecursive() )
            .queryFilters( params.getQueryFilters() )
            .from( params.getFrom() )
            .size( params.getSize() )
            .countOnly( params.isCountOnly() )
            .childOrder( params.getChildOrder() )
            .indexServiceInternal( indexServiceInternal )
            .storageService( this.storageService )
            .searchService( this.searchService )
            .build()
            .execute();
    }

    private FindNodesByParentResult getByParentRecursive( final NodeId nodeId )
    {
        return FindNodeIdsByParentCommand.create().
            parentId( nodeId ).
            recursive( true ).
            searchService( this.searchService ).
            storageService( this.storageService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();
    }

}
