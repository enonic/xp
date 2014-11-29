package com.enonic.wem.export.internal;

import org.junit.Test;

import com.enonic.wem.api.node.NodePath;

import static org.junit.Assert.*;

public class TreeNodeTest
{


    @Test
    public void testName()
        throws Exception
    {

        TreeNode<NodePath> nodeTree = new TreeNode<>( NodePath.ROOT );

        final TreeNode<NodePath> node1 = nodeTree.addChild( NodePath.newNodePath( NodePath.ROOT, "1" ).build() );
        final TreeNode<NodePath> node2 = nodeTree.addChild( NodePath.newNodePath( NodePath.ROOT, "2" ).build() );
        final TreeNode<NodePath> node3 = nodeTree.addChild( NodePath.newNodePath( NodePath.ROOT, "3" ).build() );

        final TreeNode<NodePath> node1_1 = node1.addChild( NodePath.newNodePath( node1.data, "1.1" ).build() );
        final TreeNode<NodePath> node1_2 = node1.addChild( NodePath.newNodePath( node1.data, "1.2" ).build() );

        final TreeNode<NodePath> node1_1_1 = node1_1.addChild( NodePath.newNodePath( node1_1.data, "1.1.1" ).build() );

        final TreeNode<NodePath> foundNode = nodeTree.find( NodePath.newPath( "/1/1.1/1.1.1" ).build() );

        assertNotNull( foundNode );

    }
}