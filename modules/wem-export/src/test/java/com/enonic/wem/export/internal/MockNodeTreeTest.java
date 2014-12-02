package com.enonic.wem.export.internal;

import org.junit.Test;

import com.enonic.wem.api.node.NodePath;

import static org.junit.Assert.*;

public class MockNodeTreeTest
{


    @Test
    public void find()
        throws Exception
    {

        MockNodeTree<NodePath> nodeTree = new MockNodeTree<>( NodePath.ROOT );

        final MockNodeTree<NodePath> node1 = nodeTree.addChild( NodePath.newNodePath( NodePath.ROOT, "1" ).build() );
        nodeTree.addChild( NodePath.newNodePath( NodePath.ROOT, "2" ).build() );
        nodeTree.addChild( NodePath.newNodePath( NodePath.ROOT, "3" ).build() );
        final MockNodeTree<NodePath> node1_1 = node1.addChild( NodePath.newNodePath( node1.data, "1.1" ).build() );
        node1.addChild( NodePath.newNodePath( node1.data, "1.2" ).build() );
        node1_1.addChild( NodePath.newNodePath( node1_1.data, "1.1.1" ).build() );

        final MockNodeTree<NodePath> foundNode = nodeTree.find( NodePath.newPath( "/1/1.1/1.1.1" ).build() );

        assertNotNull( foundNode );
    }
}