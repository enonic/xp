package com.enonic.wem.core.schema.content.dao;


import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.collect.Lists;

import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypes;
import com.enonic.wem.api.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.schema.content.QualifiedContentTypeNames;
import com.enonic.wem.core.jcr.JcrHelper;


final class ContentTypeDaoHandlerSelect
    extends AbstractContentTypeDaoHandler
{
    ContentTypeDaoHandlerSelect( final Session session )
    {
        super( session );
    }

    ContentTypes select( final QualifiedContentTypeNames contentTypeNames )
        throws RepositoryException
    {
        final List<ContentType> contentTypeList = Lists.newArrayList();
        for ( QualifiedContentTypeName contentTypeName : contentTypeNames )
        {
            final ContentType contentType = doSelect( contentTypeName );
            if ( contentType != null )
            {
                contentTypeList.add( contentType );
            }
        }
        return ContentTypes.from( contentTypeList );
    }

    private ContentType doSelect( final QualifiedContentTypeName contentTypeName )
        throws RepositoryException
    {
        final Node contentTypeNode = this.getContentTypeNode( contentTypeName );
        if ( contentTypeNode == null )
        {
            return null;
        }

        return this.contentTypeJcrMapper.toContentType( contentTypeNode );
    }

    ContentTypes selectAll()
        throws RepositoryException
    {
        final Node rootNode = session.getRootNode();
        final Node contentTypesNode = JcrHelper.getNodeOrNull( rootNode, ContentTypeDao.CONTENT_TYPES_PATH );

        final List<ContentType> contentTypeList = Lists.newArrayList();

        final NodeIterator contentTypeNodes = contentTypesNode.getNodes();
        while ( contentTypeNodes.hasNext() )
        {
            final Node contentTypeNode = contentTypeNodes.nextNode();
            final ContentType contentType = this.contentTypeJcrMapper.toContentType( contentTypeNode );
            contentTypeList.add( contentType );
        }

        return ContentTypes.from( contentTypeList );
    }
}
