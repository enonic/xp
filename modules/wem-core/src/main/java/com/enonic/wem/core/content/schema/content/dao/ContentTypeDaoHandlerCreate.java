package com.enonic.wem.core.content.schema.content.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.core.jcr.JcrHelper;


final class ContentTypeDaoHandlerCreate
    extends AbstractContentTypeDaoHandler
{
    ContentTypeDaoHandlerCreate( final Session session )
    {
        super( session );
    }

    void create( final ContentType contentType )
        throws RepositoryException
    {
        final QualifiedContentTypeName contentTypeName = contentType.getQualifiedName();
        if ( contentTypeExists( contentTypeName ) )
        {
            throw new SystemException( "Content type already exists: {0}", contentTypeName.toString() );
        }

        final Node contentTypeNode = createContentTypeNode( contentTypeName );
        this.contentTypeJcrMapper.toJcr( contentType, contentTypeNode );
    }

    private Node createContentTypeNode( final QualifiedContentTypeName contentTypeName )
        throws RepositoryException
    {
        final Node rootNode = session.getRootNode();
        final Node contentTypesNode = rootNode.getNode( ContentTypeDao.CONTENT_TYPES_PATH );
        final Node moduleNode = JcrHelper.getOrAddNode( contentTypesNode, contentTypeName.getModuleName().toString() );
        return JcrHelper.getOrAddNode( moduleNode, contentTypeName.getLocalName() );
    }

}
