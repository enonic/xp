package com.enonic.wem.api.form.inputtype;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.xml.DomBuilder;
import com.enonic.wem.api.xml.DomHelper;

import static com.enonic.wem.api.form.inputtype.ImageSelectorConfig.newImageSelectorConfig;

final class ImageSelectorConfigXmlSerializer
    extends AbstractInputTypeConfigXmlSerializer<ImageSelectorConfig>
{
    public static final ImageSelectorConfigXmlSerializer DEFAULT = new ImageSelectorConfigXmlSerializer();

    @Override
    protected void serializeConfig( final ImageSelectorConfig relationshipConfig, final DomBuilder builder )
    {
        builder.start( "relationship-type" );
        if ( relationshipConfig.getRelationshipType() != null )
        {
            builder.text( relationshipConfig.getRelationshipType().toString() );
        }

        builder.end();
    }

    @Override
    public ImageSelectorConfig parseConfig( final Element elem )
    {
        final ImageSelectorConfig.Builder builder = newImageSelectorConfig();
        final Element relationshipTypeEl = DomHelper.getChildElementByTagName( elem, "relationship-type" );

        final String text = DomHelper.getTextValue( relationshipTypeEl );
        if ( text != null && StringUtils.isNotBlank( text ) )
        {
            builder.relationshipType( RelationshipTypeName.from( text ) );
        }

        return builder.build();
    }
}
