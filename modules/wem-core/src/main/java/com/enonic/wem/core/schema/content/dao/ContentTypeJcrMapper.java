package com.enonic.wem.core.schema.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.schema.SchemaId;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.core.jcr.JcrHelper;
import com.enonic.wem.core.schema.content.serializer.ContentTypeJsonSerializer;
import com.enonic.wem.core.support.dao.IconJcrMapper;

import static com.enonic.wem.api.schema.content.ContentType.newContentType;

class ContentTypeJcrMapper
{
    static final String CONTENT_TYPE = "contentType";

    private final ContentTypeJsonSerializer jsonSerializer = new ContentTypeJsonSerializer().
        includeCreatedTime( false ).
        includeModifiedTime( false ).
        includeQualifiedName( false );

    private final IconJcrMapper iconJcrMapper = new IconJcrMapper();

    void toJcr( final ContentType contentType, final Node contentTypeNode )
        throws RepositoryException
    {
        final String contentTypeJson = jsonSerializer.toString( contentType );
        JcrHelper.setPropertyDateTime( contentTypeNode, "createdTime", contentType.getCreatedTime() );
        JcrHelper.setPropertyDateTime( contentTypeNode, "modifiedTime", contentType.getModifiedTime() );
        contentTypeNode.setProperty( CONTENT_TYPE, contentTypeJson );
        iconJcrMapper.toJcr( contentType.getIcon(), contentTypeNode );
    }

    ContentType toContentType( final Node contentTypeNode )
        throws RepositoryException
    {
        return toContentType( contentTypeNode, null );
    }

    ContentType toContentType( final Node contentTypeNode, final ContentTypeInheritorResolver contentTypeInheritorResolver )
        throws RepositoryException
    {
        final String contentTypeJson = contentTypeNode.getProperty( CONTENT_TYPE ).getString();
        ContentType contentType = jsonSerializer.toObject( contentTypeJson );
        if ( contentTypeInheritorResolver != null )
        {
            contentType = ContentType.newContentType( contentType ).
                addInheritor( contentTypeInheritorResolver.resolveInheritors( contentType ) ).
                build();
        }

        final Icon icon = iconJcrMapper.toIcon( contentTypeNode );
        return newContentType( contentType ).
            id( new SchemaId( contentTypeNode.getIdentifier() ) ).
            icon( icon ).
            createdTime( JcrHelper.getPropertyDateTime( contentTypeNode, "createdTime" ) ).
            modifiedTime( JcrHelper.getPropertyDateTime( contentTypeNode, "modifiedTime" ) ).
            build();
    }

}
