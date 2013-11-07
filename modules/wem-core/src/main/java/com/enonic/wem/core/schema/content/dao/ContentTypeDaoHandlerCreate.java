package com.enonic.wem.core.schema.content.dao;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.exception.SystemException;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;
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
        final ContentTypeName contentTypeName = contentType.getContentTypeName();
        if ( contentTypeExists( contentTypeName ) )
        {
            throw new SystemException( "Content type already exists: {0}", contentTypeName.toString() );
        }

        final Node contentTypeNode = createContentTypeNode( contentTypeName );
        this.contentTypeJcrMapper.toJcr( contentType, contentTypeNode );
    }

    private Node createContentTypeNode( final ContentTypeName contentTypeName )
        throws RepositoryException
    {
        final Node rootNode = session.getRootNode();
        final Node contentTypesNode = rootNode.getNode( "/content-types/" );
        return JcrHelper.getOrAddNode( contentTypesNode, contentTypeName.toString() );
    }

}
