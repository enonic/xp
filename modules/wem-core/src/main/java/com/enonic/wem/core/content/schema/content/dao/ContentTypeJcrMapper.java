package com.enonic.wem.core.content.schema.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.schema.content.ContentType;
import com.enonic.wem.core.content.schema.content.ContentTypeJsonSerializer;
import com.enonic.wem.core.support.dao.IconJcrMapper;

class ContentTypeJcrMapper
{
    static final String CONTENT_TYPE = "contentType";

    private final ContentTypeJsonSerializer jsonSerializer =
        new ContentTypeJsonSerializer().includeCreatedTime( true ).includeModifiedTime( true );

    private final IconJcrMapper iconJcrMapper = new IconJcrMapper();

    void toJcr( final ContentType contentType, final Node contentTypeNode )
        throws RepositoryException
    {
        final String contentTypeJson = jsonSerializer.toString( contentType );
        contentTypeNode.setProperty( CONTENT_TYPE, contentTypeJson );
        iconJcrMapper.toJcr( contentType.getIcon(), contentTypeNode );
    }

    ContentType toContentType( final Node contentTypeNode )
        throws RepositoryException
    {
        final String contentTypeJson = contentTypeNode.getProperty( CONTENT_TYPE ).getString();
        final ContentType contentType = jsonSerializer.toObject( contentTypeJson );
        final Icon icon = iconJcrMapper.toIcon( contentTypeNode );
        return icon == null ? contentType : ContentType.newContentType( contentType ).icon( icon ).build();
    }

}
