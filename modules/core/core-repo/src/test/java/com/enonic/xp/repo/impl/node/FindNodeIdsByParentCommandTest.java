package com.enonic.xp.repo.impl.node;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;

import static org.junit.Assert.*;

public class FindNodeIdsByParentCommandTest
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

        refresh();

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

        refresh();

        MoveNodeCommand.create().
            newParent( node2.path() ).
            id( node1_1.id() ).
            indexServiceInternal( indexServiceInternal ).
            searchService( searchService ).
            storageService( storageService ).
            build().
            execute();

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

        assertEquals( 6, getByParentRecursive( Node.ROOT_UUID ).getHits() );
        assertEquals( 5, getByParentRecursive( node.id() ).getHits() );
        assertEquals( 2, getByParentRecursive( node1_1.id() ).getHits() );
        assertEquals( 1, getByParentRecursive( node1_1_1.id() ).getHits() );
        assertEquals( 0, getByParentRecursive( node_1_1_1_1.id() ).getHits() );
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