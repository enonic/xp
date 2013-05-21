package com.enonic.wem.api.schema.content.form.inputtype;


import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;

import static com.enonic.wem.api.schema.content.form.inputtype.ImageConfig.newImageConfig;

public class ImageConfigXmlSerializer
    extends AbstractInputTypeConfigXmlSerializer<ImageConfig>
{
    public static final ImageConfigXmlSerializer DEFAULT = new ImageConfigXmlSerializer();

    public void serializeConfig( final ImageConfig relationshipConfig, final Element inputTypeConfigEl )
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
    public ImageConfig parseConfig( final Element inputTypeConfigEl )
    {
        final ImageConfig.Builder builder = newImageConfig();
        final Element relationshipTypeEl = inputTypeConfigEl.getChild( "relationship-type" );
        if ( relationshipTypeEl != null && StringUtils.isNotBlank( relationshipTypeEl.getText() ) )
        {
            builder.relationshipType( QualifiedRelationshipTypeName.from( relationshipTypeEl.getText() ) );
        }
        return builder.build();
    }
}
