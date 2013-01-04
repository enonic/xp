package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.exception.ContentNotFoundException;
import com.enonic.wem.api.exception.UnableToDeleteContentException;

import static com.enonic.wem.core.content.dao.ContentDaoConstants.CONTENTS_PATH;
import static com.enonic.wem.core.content.dao.ContentDaoConstants.CONTENT_VERSION_HISTORY_PATH;
import static org.apache.commons.lang.StringUtils.substringAfter;

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

        if ( contentNode.hasNodes() )
        {
            throw new UnableToDeleteContentException( pathToContent, "Content has child content." );
        }

        deleteContentVersions( contentNode );
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

        if ( contentNode.hasNodes() )
        {
            throw new UnableToDeleteContentException( contentId, "Content has child content." );
        }

        deleteContentVersions( contentNode );
        contentNode.remove();
    }

    private void deleteContentVersions( final Node contentNode )
        throws RepositoryException
    {
        final String contentVersionPath = "/" + CONTENT_VERSION_HISTORY_PATH + substringAfter( contentNode.getPath(), CONTENTS_PATH );
        if ( session.itemExists( contentVersionPath ) )
        {
            final Node contentVersionHistoryNode = session.getNode( contentVersionPath );
            contentVersionHistoryNode.remove();
        }
    }
}
