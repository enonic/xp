package com.enonic.wem.core.content.dao;


import javax.jcr.Node;
import javax.jcr.RepositoryException;

import com.enonic.wem.api.content.type.ContentType;
import com.enonic.wem.api.content.type.component.Components;
import com.enonic.wem.api.module.Module;
import com.enonic.wem.core.content.type.component.ComponentsJsonSerializer;

import static com.enonic.wem.api.content.type.ContentType.Builder;
import static com.enonic.wem.api.content.type.ContentType.newComponentType;
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

        final String componentsJson = componentsSerializer.toString( contentType.getComponents() );
        contentTypeNode.setProperty( COMPONENTS, componentsJson );
    }

    ContentType toContentType( final Node contentTypeNode )
        throws RepositoryException
    {
        final Builder builder = newComponentType();
        builder.name( getPropertyString( contentTypeNode, NAME ) );
        builder.module( new Module( getPropertyString( contentTypeNode, MODULE_NAME ) ) );
        builder.setAbstract( getPropertyBoolean( contentTypeNode, IS_ABSTRACT ) );
        builder.displayName( getPropertyString( contentTypeNode, DISPLAY_NAME ) );

        final String componentsJson = contentTypeNode.getProperty( COMPONENTS ).getString();
        final Components components = componentsSerializer.toObject( componentsJson );
        builder.components( components );

        return builder.build();
    }

}
