package com.enonic.wem.export.internal;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.enonic.wem.api.node.NodePath;

public class TreeNode<T>
    implements Iterable<TreeNode<T>>
{
    final T data;

    private TreeNode<T> parent;

    final List<TreeNode<T>> children;

    public TreeNode<T> find( final NodePath nodePath )
    {
        return doFindTreeNode( nodePath, this );
    }


    private TreeNode<T> doFindTreeNode( final NodePath nodePath, final TreeNode<T> treeNode )
    {
        if ( treeNode == null )
        {
            return null;
        }

        if ( nodePath.equals( treeNode.data ) )
        {
            return treeNode;
        }

        for ( final TreeNode child : treeNode.children )
        {
            final TreeNode foundNode = doFindTreeNode( nodePath, child );

            if ( foundNode != null )
            {
                return foundNode;
            }
        }

        return null;
    }


    public TreeNode( T data )
    {
        this.data = data;
        this.children = new LinkedList<>();
    }

    public TreeNode<T> addChild( T child )
    {
        TreeNode<T> childNode = new TreeNode<T>( child );
        childNode.parent = this;
        this.children.add( childNode );
        return childNode;
    }

    @Override
    public Iterator<TreeNode<T>> iterator()
    {
        return children.iterator();
    }

    // other features ...

}

