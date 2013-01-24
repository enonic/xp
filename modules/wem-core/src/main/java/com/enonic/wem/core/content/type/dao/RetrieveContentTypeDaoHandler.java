package com.enonic.wem.core.content.type.dao;


import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;
import com.enonic.wem.api.content.type.QualifiedContentTypeNames;
import com.enonic.wem.core.jcr.JcrHelper;


final class RetrieveContentTypeDaoHandler
    extends AbstractContentTypeDaoHandler
{
    RetrieveContentTypeDaoHandler( final Session session )
    {
        super( session );
    }

    ContentTypes retrieve( final QualifiedContentTypeNames contentTypeNames )
        throws RepositoryException
    {
        final List<ContentType> contentTypeList = Lists.newArrayList();
        for ( QualifiedContentTypeName contentTypeName : contentTypeNames )
        {
            final ContentType contentType = retrieveContentType( contentTypeName );
            if ( contentType != null )
            {
                contentTypeList.add( contentType );
            }
        }
        return ContentTypes.from( contentTypeList );
    }

    private ContentType retrieveContentType( final QualifiedContentTypeName contentTypeName )
        throws RepositoryException
    {
        final Node contentTypeNode = this.getContentTypeNode( contentTypeName );
        if ( contentTypeNode == null )
        {
            return null;
        }

        return this.contentTypeJcrMapper.toContentType( contentTypeNode );
    }

    ContentTypes retrieveAll()
        throws RepositoryException
    {
        final Node rootNode = session.getRootNode();
        final Node contentTypesNode = JcrHelper.getNodeOrNull( rootNode, ContentTypeDao.CONTENT_TYPES_PATH );

        final List<ContentType> contentTypeList = Lists.newArrayList();
        final NodeIterator contentTypeModuleNodes = contentTypesNode.getNodes();
        while ( contentTypeModuleNodes.hasNext() )
        {
            final Node contentTypeModuleNode = contentTypeModuleNodes.nextNode();

            final NodeIterator contentTypeNodes = contentTypeModuleNode.getNodes();
            while ( contentTypeNodes.hasNext() )
            {
                final Node contentTypeNode = contentTypeNodes.nextNode();
                final ContentType contentType = this.contentTypeJcrMapper.toContentType( contentTypeNode );
                contentTypeList.add( contentType );
            }
        }

        return ContentTypes.from( contentTypeList );
    }
}
