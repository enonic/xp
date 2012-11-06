package com.enonic.wem.core.content.dao;


import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.google.common.collect.Lists;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.ContentTypeNames;
import com.enonic.wem.api.content.type.ContentTypes;
import com.enonic.wem.api.content.type.QualifiedContentTypeName;


final class RetrieveContentTypeDaoHandler
    extends AbstractContentTypeDaoHandler
{
    RetrieveContentTypeDaoHandler( final Session session )
    {
        super( session );
    }

    ContentTypes retrieve( final ContentTypeNames contentTypeNames )
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

        final ContentType contentType = new ContentType();
        this.contentTypeJcrMapper.toContentType( contentTypeNode, contentType );
        return contentType;
    }
}
