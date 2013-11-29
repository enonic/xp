package com.enonic.wem.core.content.dao;


import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentIds;
import com.enonic.wem.api.support.tree.Tree;
import com.enonic.wem.api.support.tree.TreeNode;
import com.enonic.wem.core.index.IndexService;

final class ContentDaoHandlerGetContentTree
    extends AbstractContentDaoHandler
{
    ContentDaoHandlerGetContentTree( final Session session, final IndexService indexService )
    {
        super( session, indexService );
    }

    Tree<Content> handle()
        throws RepositoryException
    {
        return doBuildContentTree( doGetTopContentNodes() );
    }

    Tree<Content> handle( final ContentIds contentIds )
        throws RepositoryException
    {
        final List<Node> topLevelNodes = Lists.newArrayList();

        final Iterator<ContentId> contentIdIterator = contentIds.iterator();
        while ( contentIdIterator.hasNext() )
        {
            final Node foundNode = doGetContentNode( contentIdIterator.next() );

            if ( foundNode != null )
            {
                topLevelNodes.add( foundNode );
            }
        }

        return doBuildContentTree( topLevelNodes.iterator() );
    }

    private Tree<Content> doBuildContentTree( final Iterator<Node> topNodes )
        throws RepositoryException
    {
        final Tree<Content> tree = new Tree<>();

        final List<ContentAndNode> topContent = doContentNodesToContentAndNodes( topNodes );

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

