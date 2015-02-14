package com.enonic.xp.core.impl.export;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.enonic.wem.api.node.NodePath;

public class MockNodeTree<T>
    implements Iterable<MockNodeTree<T>>
{
    final T data;

    private MockNodeTree<T> parent;

    final List<MockNodeTree<T>> children;

    public MockNodeTree<T> find( final NodePath nodePath )
    {
        return doFindTreeNode( nodePath, this );
    }


    private MockNodeTree<T> doFindTreeNode( final NodePath nodePath, final MockNodeTree<T> treeNode )
    {
        if ( treeNode == null )
        {
            return null;
        }

        if ( nodePath.equals( treeNode.data ) )
        {
            return treeNode;
        }

        for ( final MockNodeTree<T> child : treeNode.children )
        {
            final MockNodeTree<T> foundNode = doFindTreeNode( nodePath, child );

            if ( foundNode != null )
            {
                return foundNode;
            }
        }

        return null;
    }


    public MockNodeTree( T data )
    {
        this.data = data;
        this.children = new LinkedList<>();
    }

    public MockNodeTree<T> addChild( T child )
    {
        MockNodeTree<T> childNode = new MockNodeTree<>( child );
        childNode.parent = this;
        this.children.add( childNode );
        return childNode;
    }

    public MockNodeTree<T> getParent()
    {
        return parent;
    }

    @Override
    public Iterator<MockNodeTree<T>> iterator()
    {
        return children.iterator();
    }

    // other features ...

}

