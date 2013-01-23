package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.exception.ContentNotFoundException;
import com.enonic.wem.api.exception.UnableToDeleteContentException;

import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_VERSION_HISTORY_NODE;

final class DeleteContentDaoHandler
    extends AbstractContentDaoHandler
{
    DeleteContentDaoHandler( final Session session )
    {
        super( session );
    }

    void deleteContentByPath( final ContentPath pathToContent )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( session, pathToContent );
        if ( contentNode == null )
        {
            throw new ContentNotFoundException( pathToContent );
        }

        if ( hasContentChildrenNodes( contentNode ) )
        {
            throw new UnableToDeleteContentException( pathToContent, "Content has child content." );
        }

        contentNode.remove();
    }

    public void deleteContentById( final ContentId contentId )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( session, contentId );
        if ( contentNode == null )
        {
            throw new ContentNotFoundException( contentId );
        }

        if ( hasContentChildrenNodes( contentNode ) )
        {
            throw new UnableToDeleteContentException( contentId, "Content has child content." );
        }

        contentNode.remove();
    }

    private boolean hasContentChildrenNodes( final Node contentNode )
        throws RepositoryException
    {
        if ( contentNode.hasNodes() )
        {
            final NodeIterator nodeIte = contentNode.getNodes();
            while ( nodeIte.hasNext() )
            {
                final Node child = nodeIte.nextNode();
                if ( !child.getName().equals( CONTENT_VERSION_HISTORY_NODE ) )
                {
                    return true;
                }
            }
        }
        return false;
    }
}
