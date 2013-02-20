package com.enonic.wem.core.content.schema.type.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.schema.type.QualifiedContentTypeName;
import com.enonic.wem.core.jcr.JcrHelper;

abstract class AbstractContentTypeDaoHandler
{
    protected final Session session;

    protected final ContentTypeJcrMapper contentTypeJcrMapper = new ContentTypeJcrMapper();

    AbstractContentTypeDaoHandler( final Session session )
    {
        this.session = session;
    }

    protected final Node getContentTypeNode( final QualifiedContentTypeName contentTypeName )
        throws RepositoryException
    {
        final String path = getNodePath( contentTypeName );
        final Node rootNode = session.getRootNode();
        return JcrHelper.getNodeOrNull( rootNode, path );
    }

    protected final String getNodePath( final QualifiedContentTypeName contentTypeName )
    {
        return ContentTypeDao.CONTENT_TYPES_PATH + contentTypeName.getModuleName() + "/" + contentTypeName.getLocalName();
    }

    protected final boolean contentTypeExists( final QualifiedContentTypeName contentTypeName )
        throws RepositoryException
    {
        final String contentTypePath = getNodePath( contentTypeName );
        return session.getRootNode().hasNode( contentTypePath );
    }

}
