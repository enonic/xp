package com.enonic.wem.core.content.type.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.core.content.type.ContentTypeJsonSerializer;

class ContentTypeJcrMapper
{
    private static final String CONTENT_TYPE = "contentType";

    private ContentTypeJsonSerializer jsonSerializer = new ContentTypeJsonSerializer();

    void toJcr( final ContentType contentType, final Node contentTypeNode )
        throws RepositoryException
    {
        final String contentTypeJson = jsonSerializer.toString( contentType );
        contentTypeNode.setProperty( CONTENT_TYPE, contentTypeJson );
    }

    ContentType toContentType( final Node contentTypeNode )
        throws RepositoryException
    {
        final String contentTypeJson = contentTypeNode.getProperty( CONTENT_TYPE ).getString();
        final ContentType contentType = jsonSerializer.toObject( contentTypeJson );
        return contentType;
    }

}
