package com.enonic.wem.api.form.inputtype;


import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;

import static com.enonic.wem.api.form.inputtype.ImageSelectorConfig.newImageSelectorConfig;

public class ImageSelectorConfigXmlSerializer
    extends AbstractInputTypeConfigXmlSerializer<ImageSelectorConfig>
{
    public static final ImageSelectorConfigXmlSerializer DEFAULT = new ImageSelectorConfigXmlSerializer();

    public void serializeConfig( final ImageSelectorConfig relationshipConfig, final Element inputTypeConfigEl )
    {
        if ( relationshipConfig.getRelationshipType() != null )
        {
            inputTypeConfigEl.addContent(
                new Element( "relationship-type" ).setText( relationshipConfig.getRelationshipType().toString() ) );
        }
        else
        {
            inputTypeConfigEl.addContent( new Element( "relationship-type" ) );
        }
    }

    @Override
    public ImageSelectorConfig parseConfig( final Element inputTypeConfigEl )
    {
        final ImageSelectorConfig.Builder builder = newImageSelectorConfig();
        final Element relationshipTypeEl = inputTypeConfigEl.getChild( "relationship-type" );
        if ( relationshipTypeEl != null && StringUtils.isNotBlank( relationshipTypeEl.getText() ) )
        {
            builder.relationshipType( QualifiedRelationshipTypeName.from( relationshipTypeEl.getText() ) );
        }
        return builder.build();
    }
}
