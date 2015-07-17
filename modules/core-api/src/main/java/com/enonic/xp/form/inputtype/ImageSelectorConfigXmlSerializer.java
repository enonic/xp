package com.enonic.xp.form.inputtype;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.xml.DomBuilder;
import com.enonic.xp.xml.DomHelper;

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
    public ImageSelectorConfig parseConfig( final ApplicationKey currentModule, final Element elem )
    {
        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( currentModule );

        final ImageSelectorConfig.Builder builder = ImageSelectorConfig.create();
        final Element relationshipTypeEl = DomHelper.getChildElementByTagName( elem, "relationship-type" );

        final String text = DomHelper.getTextValue( relationshipTypeEl );
        if ( text != null && StringUtils.isNotBlank( text ) )
        {
            builder.relationshipType( resolver.toRelationshipTypeName( text ) );
        }

        return builder.build();
    }

}
