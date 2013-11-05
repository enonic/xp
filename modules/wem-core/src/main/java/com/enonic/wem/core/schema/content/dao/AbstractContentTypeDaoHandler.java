package com.enonic.wem.core.schema.content.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.jcr.JcrHelper;

abstract class AbstractContentTypeDaoHandler
{
    protected final Session session;

    protected final ContentTypeJcrMapper contentTypeJcrMapper = new ContentTypeJcrMapper();

    AbstractContentTypeDaoHandler( final Session session )
    {
        this.session = session;
    }

    protected final Node getContentTypeNode( final ContentTypeName contentTypeName )
        throws RepositoryException
    {
        final String path = getNodePath( contentTypeName );
        final Node rootNode = session.getRootNode();
        return JcrHelper.getNodeOrNull( rootNode, path );
    }

    protected final String getNodePath( final ContentTypeName contentTypeName )
    {
        return ContentTypeDao.CONTENT_TYPES_PATH + contentTypeName.toString();
    }

    protected final boolean contentTypeExists( final ContentTypeName contentTypeName )
        throws RepositoryException
    {
        final String contentTypePath = getNodePath( contentTypeName );
        return session.getRootNode().hasNode( contentTypePath );
    }

}
