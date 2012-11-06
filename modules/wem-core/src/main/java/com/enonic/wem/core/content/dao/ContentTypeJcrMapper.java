package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.core.content.type.ContentTypeSerializerJson;

import static com.enonic.wem.core.jcr.JcrHelper.getPropertyBoolean;
import static com.enonic.wem.core.jcr.JcrHelper.getPropertyString;

class ContentTypeJcrMapper
{
    private static final String NAME = "name";

    private static final String DISPLAY_NAME = "displayName";

    private static final String MODULE_NAME = "moduleName";

    private static final String IS_ABSTRACT = "isAbstract";

    private static final String DATA = "data";


    private ContentTypeSerializerJson contentTypeSerializer = new ContentTypeSerializerJson();


    void toJcr( final ContentType contentType, final Node contentTypeNode )
        throws RepositoryException
    {
        contentTypeNode.setProperty( NAME, contentType.getName() );
        contentTypeNode.setProperty( DISPLAY_NAME, contentType.getDisplayName() );
        contentTypeNode.setProperty( MODULE_NAME, contentType.getModule().getName() );
        contentTypeNode.setProperty( IS_ABSTRACT, contentType.isAbstract() );

        // TODO serialize content type as json
//        final String contentTypeJson = contentTypeSerializer.toString( contentType);
//        contentTypeNode.setProperty( DATA, contentTypeJson );
    }

    void toContentType( final Node contentTypeNode, final ContentType contentType )
        throws RepositoryException
    {
        contentType.setName( getPropertyString( contentTypeNode, NAME ) );
        contentType.setDisplayName( getPropertyString( contentTypeNode, DISPLAY_NAME ) );
        contentType.setModule( new Module( getPropertyString( contentTypeNode, MODULE_NAME ) ) );
        contentType.setAbstract( getPropertyBoolean( contentTypeNode, IS_ABSTRACT ) );

        // TODO deserialize content type from json
//        final String contentTypeJson = contentTypeNode.getProperty( "data" ).getString();
//        final ContentType contentType1 = contentTypeSerializer.toContentType( contentTypeJson );
    }

}
