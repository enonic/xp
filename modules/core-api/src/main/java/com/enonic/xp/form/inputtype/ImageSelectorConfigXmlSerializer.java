package com.enonic.xp.form.inputtype;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleRelativeResolver;
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
    public ImageSelectorConfig parseConfig( final ModuleKey currentModule, final Element elem )
    {
        final ModuleRelativeResolver resolver = new ModuleRelativeResolver( currentModule );

        final ImageSelectorConfig.Builder builder = ImageSelectorConfig.newImageSelectorConfig();
        final Element relationshipTypeEl = DomHelper.getChildElementByTagName( elem, "relationship-type" );

        final String text = DomHelper.getTextValue( relationshipTypeEl );
        if ( text != null && StringUtils.isNotBlank( text ) )
        {
            builder.relationshipType( resolver.toRelationshipTypeName( text ) );
        }

        return builder.build();
    }

}
