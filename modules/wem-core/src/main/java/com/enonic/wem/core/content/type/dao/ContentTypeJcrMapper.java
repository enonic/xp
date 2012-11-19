package com.enonic.wem.core.content.type.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.form.FormItems;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.core.content.type.form.ComponentsJsonSerializer;

import static com.enonic.wem.api.content.type.ContentType.Builder;
import static com.enonic.wem.api.content.type.ContentType.newContentType;
import static com.enonic.wem.core.jcr.JcrHelper.getPropertyBoolean;
import static com.enonic.wem.core.jcr.JcrHelper.getPropertyString;

class ContentTypeJcrMapper
{
    private static final String NAME = "name";

    private static final String DISPLAY_NAME = "displayName";

    private static final String MODULE_NAME = "moduleName";

    private static final String IS_ABSTRACT = "isAbstract";

    private static final String COMPONENTS = "components";


    private ComponentsJsonSerializer componentsSerializer = new ComponentsJsonSerializer();


    void toJcr( final ContentType contentType, final Node contentTypeNode )
        throws RepositoryException
    {
        contentTypeNode.setProperty( NAME, contentType.getName() );
        contentTypeNode.setProperty( DISPLAY_NAME, contentType.getDisplayName() );
        contentTypeNode.setProperty( MODULE_NAME, contentType.getModule().getName() );
        contentTypeNode.setProperty( IS_ABSTRACT, contentType.isAbstract() );

        final String componentsJson = componentsSerializer.toString( contentType.getFormItems() );
        contentTypeNode.setProperty( COMPONENTS, componentsJson );
    }

    ContentType toContentType( final Node contentTypeNode )
        throws RepositoryException
    {
        final Builder builder = newContentType();
        builder.name( getPropertyString( contentTypeNode, NAME ) );
        builder.module( new Module( getPropertyString( contentTypeNode, MODULE_NAME ) ) );
        builder.setAbstract( getPropertyBoolean( contentTypeNode, IS_ABSTRACT ) );
        builder.displayName( getPropertyString( contentTypeNode, DISPLAY_NAME ) );

        final String componentsJson = contentTypeNode.getProperty( COMPONENTS ).getString();
        final FormItems formItems = componentsSerializer.toObject( componentsJson );
        builder.formItems( formItems );

        return builder.build();
    }

}
