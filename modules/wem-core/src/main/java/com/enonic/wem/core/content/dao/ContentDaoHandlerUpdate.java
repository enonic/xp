package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentNotFoundException;
import com.enonic.wem.api.content.versioning.ContentVersionId;
import com.enonic.wem.core.index.IndexService;

import static com.enonic.wem.core.content.dao.ContentDao.CONTENT_NEXT_VERSION_PROPERTY;
import static com.enonic.wem.core.jcr.JcrHelper.getPropertyLong;

final class ContentDaoHandlerUpdate
    extends AbstractContentDaoHandler
{
    ContentDaoHandlerUpdate( final Session session, final IndexService indexService )
    {
        super( session, indexService );
    }

    void handle( Content content, final boolean createNewVersion )
        throws RepositoryException
    {
        final Node contentNode = doGetContentNode( content.getPath() );
        if ( contentNode == null )
        {
            throw new ContentNotFoundException( content.getPath() );
        }

        content = increaseContentVersion( content, contentNode );
        contentJcrMapper.toJcr( content, contentNode );

        if ( createNewVersion )
        {
            final Node contentVersionHistoryParent = getContentVersionHistoryNode( contentNode );
            addContentVersion( content, contentVersionHistoryParent );
        }
    }

    private Content increaseContentVersion( final Content content, final Node contentNode )
        throws RepositoryException
    {
        final Node contentVersionParent = getContentVersionHistoryNode( contentNode );
        final ContentVersionId versionId = ContentVersionId.of( nextContentVersion( contentVersionParent ) );
        return Content.newContent( content ).version( versionId ).build();
    }

    private long nextContentVersion( final Node contentVersionParent )
        throws RepositoryException
    {
        final long versionNumber = getPropertyLong( contentVersionParent, CONTENT_NEXT_VERSION_PROPERTY, 0l );
        contentVersionParent.setProperty( CONTENT_NEXT_VERSION_PROPERTY, versionNumber + 1 );
        return versionNumber;
    }
}
