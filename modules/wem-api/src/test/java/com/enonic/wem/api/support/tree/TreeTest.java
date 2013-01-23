package com.enonic.wem.api.support.tree;


import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class TreeTest
{
    @Test
    public void deepSize()
    {
        TreeNode<String> node_1 = new TreeNode<String>( "1" );
        TreeNode<String> node_1_1 = new TreeNode<String>( "1-1" );
        TreeNode<String> node_1_1_1 = new TreeNode<String>( "1-1-1" );

        node_1.addChild( node_1_1 );
        node_1_1.addChild( node_1_1_1 );

        Tree<String> tree = new Tree<String>();
        tree.addNode( node_1 );

        assertEquals( 3, tree.deepSize() );
    }
}
