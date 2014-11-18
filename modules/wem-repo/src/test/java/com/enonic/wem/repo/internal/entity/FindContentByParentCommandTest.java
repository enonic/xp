package com.enonic.wem.repo.internal.entity;

import org.junit.Test;

import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.FindNodesByParentParams;
import com.enonic.wem.api.node.FindNodesByParentResult;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;

import static org.junit.Assert.*;

public class FindContentByParentCommandTest
    extends AbstractNodeTest
{

    @Test
    public void getChildren()
        throws Exception
    {
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
            final RootDataSet data = new RootDataSet();
            data.setProperty( "myOrderNumber", Value.newDouble( i ) );

            createNode( CreateNodeParams.create().
                parent( createdNode.path() ).
                name( "my-child-" + i ).
                data( data ).
                build() );
        }
    }
}
