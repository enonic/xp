package com.enonic.wem.api.support.tree;


import java.util.Iterator;
import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * The Tree implements Iterable which will iterate the root nodes of the Tree.
 *
 * @param <T> The type of Tree.
 */
public final class Tree<T>
    implements Iterable<TreeNode<T>>
{
    private final List<TreeNode<T>> rootNodes = Lists.newArrayList();

    public Tree()
    {
        // nothing
    }

    public Tree( final Iterable<? extends T> rootNodeObjects )
    {
        for ( T nodeObject : rootNodeObjects )
        {
            createNode( nodeObject );
        }
    }

    /**
     * Creates a TreeNode for the given object and adds it to this Tree.
     *
     * @return the created TreeNode.
     */
    public TreeNode<T> createNode( T object )
    {
        final TreeNode<T> node = new TreeNode<T>( object );
        rootNodes.add( node );
        return node;
    }


    /**
     * Adds the given TreeNode to this Tree.
     *
     * @return this Tree.
     */
    public Tree<T> addNode( final TreeNode<T> node )
    {
        Preconditions.checkState( node.getParent() == null, "Only TreeNode without parent can be added as a root node " );
        rootNodes.add( node );
        return this;
    }

    /**
     * Creates TreeNode-s for the given objects and adds it this Tree.
     *
     * @return this Tree.
     */
    public Tree<T> createNodes( final List<? extends T> rootNodeObjects )
    {
        for ( T nodeObject : rootNodeObjects )
        {
            createNode( nodeObject );
        }
        return this;
    }

    /**
     * Adds the given TreeNode-s to this Tree.
     *
     * @return this Tree.
     */
    public Tree<T> createNodes( final Iterable<? extends T> iterableOfRootNodeObjects )
    {
        for ( T nodeObject : iterableOfRootNodeObjects )
        {
            createNode( nodeObject );
        }
        return this;
    }

    /**
     * Adds the given TreeNode-s to this Tree.
     *
     * @return this Tree.
     */
    public Tree<T> addNodes( final Iterable<TreeNode<T>> iterableOfRootNodes )
    {
        for ( TreeNode<T> nodeObject : iterableOfRootNodes )
        {
            addNode( nodeObject );
        }
        return this;
    }

    public int size()
    {
        return rootNodes.size();
    }

    public int deepSize()
    {
        int deepSize = size();
        for ( TreeNode contentNode : rootNodes )
        {
            deepSize += contentNode.deepSize();
        }
        return deepSize;
    }

    @Override
    public Iterator<TreeNode<T>> iterator()
    {
        return rootNodes.iterator();
    }

    public TreeNode<T> getRootNode( int index )
    {
        return rootNodes.get( index );
    }
}
