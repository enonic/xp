package com.enonic.wem.core.content.dao;


import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.index.IndexService;

final class ContentDaoHandlerCountContentTypeUsage
    extends AbstractContentDaoHandler
{
    ContentDaoHandlerCountContentTypeUsage( final Session session, final IndexService indexService )
    {
        super( session, indexService );
    }

    int handle( ContentTypeName contentTypeName )
        throws RepositoryException
    {
        if ( contentTypeName == null )
        {
            return 0;
        }

        final List<ContentAndNode> topContentAndNodes = doContentNodesToContentAndNodes( doGetTopContentNodes() );

        int count = 0;
        for ( ContentAndNode topContentAndNode : topContentAndNodes )
        {
            count += countContentTypeUsageInNode( contentTypeName, topContentAndNode );
        }
        return count;
    }


    private int countContentTypeUsageInNode( final ContentTypeName contentTypeName, ContentAndNode parentContentAndNode )
        throws RepositoryException
    {
        int count = 0;
        if ( contentTypeName.equals( parentContentAndNode.content.getType() ) )
        {
            count++;
        }
        for ( ContentAndNode childContentAndNode : doContentNodesToContentAndNodes(
            doGetChildContentNodes( parentContentAndNode.contentNode ) ) )
        {
            count += countContentTypeUsageInNode( contentTypeName, childContentAndNode );
        }
        return count;
    }

}

