package com.enonic.wem.core.content.dao;


import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;

final class ContentDaoHandlerCountContentTypeUsage
    extends AbstractContentDaoHandler
{
    ContentDaoHandlerCountContentTypeUsage( final Session session )
    {
        super( session );
    }

    int handle( QualifiedContentTypeName qualifiedContentTypeName )
        throws RepositoryException
    {
        if ( qualifiedContentTypeName == null )
        {
            return 0;
        }

        final List<ContentAndNode> topContentAndNodes = doContentNodesToContentAndNodes( doGetTopContentNodes() );

        int count = 0;
        for ( ContentAndNode topContentAndNode : topContentAndNodes )
        {
            count += countContentTypeUsageInNode( qualifiedContentTypeName, topContentAndNode );
        }
        return count;
    }


    private int countContentTypeUsageInNode( final QualifiedContentTypeName qualifiedContentTypeName, ContentAndNode parentContentAndNode )
        throws RepositoryException
    {
        int count = 0;
        if ( qualifiedContentTypeName.equals( parentContentAndNode.content.getType() ) )
        {
            count++;
        }
        for ( ContentAndNode childContentAndNode : doContentNodesToContentAndNodes(
            doGetChildContentNodes( parentContentAndNode.contentNode ) ) )
        {
            count += countContentTypeUsageInNode( qualifiedContentTypeName, childContentAndNode );
        }
        return count;
    }

}

