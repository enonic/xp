package com.enonic.wem.core.content.dao;


import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.ContentBranch;
import com.enonic.wem.api.content.ContentTree;

class GetContentTreeContentDaoHandler
    extends AbstractContentDaoHandler
{
    GetContentTreeContentDaoHandler( final Session session )
    {
        super( session );
    }

    ContentTree handle()
        throws RepositoryException
    {
        ContentTree.Builder contentTreeBuilder = ContentTree.newContentTree();

        final List<ContentAndNode> topContent = doContentNodesToContentAndNodes( doGetTopContentNodes( session ) );

        for ( ContentAndNode parentContentAndNode : topContent )
        {
            contentTreeBuilder.addBranch( doCreateBranch( parentContentAndNode ) );
        }
        return contentTreeBuilder.build();
    }

    private ContentBranch doCreateBranch( final ContentAndNode parentContentAndNode )
        throws RepositoryException
    {
        final ContentBranch.Builder branchBuilder = ContentBranch.newContentBranch().parent( parentContentAndNode.content );
        for ( ContentAndNode childContentAndNode : doContentNodesToContentAndNodes(
            doGetChildContentNodes( parentContentAndNode.contentNode ) ) )
        {
            branchBuilder.addChild( doCreateBranch( childContentAndNode ) );
        }
        return branchBuilder.build();
    }
}

