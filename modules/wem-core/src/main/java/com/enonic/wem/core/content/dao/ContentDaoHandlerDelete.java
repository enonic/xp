package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.UnableToDeleteContentException;

final class ContentDaoHandlerDelete
    extends AbstractContentDaoHandler
{
    private boolean ignoreContentNotFound = false;

    private boolean force = false;

    ContentDaoHandlerDelete( final Session session )
    {
        super( session );
    }

    ContentDaoHandlerDelete force( final boolean force )
    {
        this.force = force;
        return this;
    }

    ContentDaoHandlerDelete ignoreContentNotFound( final boolean ignoreContentNotFound )
    {
        this.ignoreContentNotFound = ignoreContentNotFound;
        return this;
    }

    void deleteContentByPath( final ContentPath pathToContent )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( pathToContent );

        if ( !ignoreContentNotFound && contentNode == null )
        {
            throw new ContentNotFoundException( pathToContent );
        }

        if ( !force && hasContentChildrenNodes( contentNode ) )
        {
            throw new UnableToDeleteContentException( pathToContent, "Content has child content." );
        }

        contentNode.remove();
    }

    public void deleteContentById( final ContentId contentId )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( contentId );
        if ( !ignoreContentNotFound && contentNode == null )
        {
            throw new ContentNotFoundException( contentId );
        }

        if ( !force && hasContentChildrenNodes( contentNode ) )
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
                if ( !isNonContentNode( child ) )
                {
                    final Content content = nodeToContent( child );
                    if ( !content.isEmbedded() )
                    {
                        // allow deletion if child content is embedded
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
