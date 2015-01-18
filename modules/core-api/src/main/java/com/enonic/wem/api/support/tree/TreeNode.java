package com.enonic.wem.api.support.tree;


import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * The TreeNode implements Iterable which will iterate the child nodes of the TreeNode.
 *
 * @param <T> The type of TreeNode.
 */
public class TreeNode<T>
    implements Iterable<TreeNode<T>>
{
    private TreeNode<T> parent;

    private final T object;

    private final List<TreeNode<T>> children = Lists.newArrayList();

    public TreeNode( final T object )
    {
        this.object = object;
    }

    public void addChild( TreeNode<T> node )
    {
        node.parent = this;
        children.add( node );
    }

    public TreeNode<T> addChild( T object )
    {
        final TreeNode<T> node = new TreeNode<T>( object );
        node.parent = this;
        children.add( node );
        return node;
    }

    public TreeNode<T> getParent()
    {
        return parent;
    }

    public T getObject()
    {
        return object;
    }

    public int size()
    {
        return children.size();
    }

    public int deepSize()
    {
        int deepSize = size();
        for ( final TreeNode node : children )
        {
            deepSize += node.deepSize();
        }
        return deepSize;
    }

    public boolean hasChildren()
    {
        return !children.isEmpty();
    }

    @Override
    public Iterator<TreeNode<T>> iterator()
    {
        return children.iterator();
    }

    public Iterable<TreeNode<T>> getChildren()
    {
        return children;
    }

    public TreeNode<? extends T> getChild( int index )
    {
        return children.get( index );
    }
}
