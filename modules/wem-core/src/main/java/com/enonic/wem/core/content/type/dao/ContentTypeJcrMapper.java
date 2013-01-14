package com.enonic.wem.core.content.type.dao;


import java.io.IOException;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.core.content.type.ContentTypeJsonSerializer;
import com.enonic.wem.core.jcr.JcrHelper;

import static com.enonic.wem.api.content.type.ContentType.newContentType;

class ContentTypeJcrMapper
{
    static final String CONTENT_TYPE = "contentType";

    static final String ICON = "icon";

    private ContentTypeJsonSerializer jsonSerializer = new ContentTypeJsonSerializer();

    void toJcr( final ContentType contentType, final Node contentTypeNode )
        throws RepositoryException
    {
        final String contentTypeJson = jsonSerializer.toString( contentType );
        contentTypeNode.setProperty( CONTENT_TYPE, contentTypeJson );
        final byte[] icon = contentType.getIcon();
        if ( icon != null )
        {
            JcrHelper.setPropertyBinary( contentTypeNode, ICON, icon );
        }
    }

    ContentType toContentType( final Node contentTypeNode )
        throws RepositoryException
    {
        final String contentTypeJson = contentTypeNode.getProperty( CONTENT_TYPE ).getString();
        final ContentType contentType = jsonSerializer.toObject( contentTypeJson );
        final byte[] icon = getIcon( contentTypeNode );
        return icon == null ? contentType : newContentType( contentType ).icon( icon ).build();
    }

    private byte[] getIcon( final Node contentTypeNode )
        throws RepositoryException
    {
        try
        {
            return JcrHelper.getPropertyBinary( contentTypeNode, ICON );
        }
        catch ( IOException e )
        {
            throw new RepositoryException( e.getMessage(), e );
        }
    }

}
