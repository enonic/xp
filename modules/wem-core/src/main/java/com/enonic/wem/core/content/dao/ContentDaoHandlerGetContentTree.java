package com.enonic.wem.core.content.dao;


import java.util.List;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.api.support.tree.TreeNode;

final class ContentDaoHandlerGetContentTree
    extends AbstractContentDaoHandler
{
    ContentDaoHandlerGetContentTree( final Session session )
    {
        super( session );
    }

    Tree<Content> handle()
        throws RepositoryException
    {
        final Tree<Content> tree = new Tree<>();

        final List<ContentAndNode> topContent = doContentNodesToContentAndNodes( doGetTopContentNodes() );

        for ( ContentAndNode parentContentAndNode : topContent )
        {
            tree.addNode( doCreateNode( parentContentAndNode ) );
        }
        return tree;
    }

    private TreeNode<Content> doCreateNode( final ContentAndNode parentContentAndNode )
        throws RepositoryException
    {
        final NodeIterator childContentNodes = doGetChildContentNodes( parentContentAndNode.contentNode );
        final List<ContentAndNode> contentAndNodes = doContentNodesToContentAndNodes( childContentNodes );

        final TreeNode<Content> node = new TreeNode<>( parentContentAndNode.content );
        for ( ContentAndNode childContentAndNode : contentAndNodes )
        {
            node.addChild( doCreateNode( childContentAndNode ) );
        }
        return node;
    }
}

