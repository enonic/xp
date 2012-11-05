package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.Contents;

class FindChildContentDaoHandler
    extends AbstractContentDaoHandler
{
    FindChildContentDaoHandler( final Session session )
    {
        super( session );
    }

    Contents handle( final ContentPath parentPath )
        throws RepositoryException
    {
        final Contents.Builder contentsBuilder = Contents.builder();
        final Node parentContentNode = doGetContentNode( session, parentPath );
        final NodeIterator nodeIterator = parentContentNode.getNodes();
        while ( nodeIterator.hasNext() )
        {
            final Node contentNode = nodeIterator.nextNode();
            final ContentPath childPath = ContentPath.from( parentPath, contentNode.getName() );
            final Content content = Content.create( childPath );
            contentJcrMapper.toContent( contentNode, content );
            contentsBuilder.add( content );
        }

        return contentsBuilder.build();
    }
}

