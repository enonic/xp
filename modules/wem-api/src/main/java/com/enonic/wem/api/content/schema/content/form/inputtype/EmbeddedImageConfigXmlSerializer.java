package com.enonic.wem.api.content.schema.content.form.inputtype;


import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;

import static com.enonic.wem.api.content.schema.content.form.inputtype.EmbeddedImageConfig.newEmbeddedImageConfig;

public class EmbeddedImageConfigXmlSerializer
    extends AbstractInputTypeConfigXmlSerializer<EmbeddedImageConfig>
{
    public static final EmbeddedImageConfigXmlSerializer DEFAULT = new EmbeddedImageConfigXmlSerializer();

    public void serializeConfig( final EmbeddedImageConfig relationshipConfig, final Element inputTypeConfigEl )
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
    public EmbeddedImageConfig parseConfig( final Element inputTypeConfigEl )
    {
        final EmbeddedImageConfig.Builder builder = newEmbeddedImageConfig();
        final Element relationshipTypeEl = inputTypeConfigEl.getChild( "relationship-type" );
        if ( relationshipTypeEl != null && StringUtils.isNotBlank( relationshipTypeEl.getText() ) )
        {
            builder.relationshipType( QualifiedRelationshipTypeName.from( relationshipTypeEl.getText() ) );
        }
        return builder.build();
    }
}
