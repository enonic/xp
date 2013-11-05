package com.enonic.wem.api.form.inputtype;


import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

public class RelationshipConfigXmlSerializer
    extends AbstractInputTypeConfigXmlSerializer<RelationshipConfig>
{
    public static final RelationshipConfigXmlSerializer DEFAULT = new RelationshipConfigXmlSerializer();

    public void serializeConfig( final RelationshipConfig relationshipConfig, final Element inputTypeConfigEl )
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
    public RelationshipConfig parseConfig( final Element inputTypeConfigEl )
    {
        final RelationshipConfig.Builder builder = RelationshipConfig.newRelationshipConfig();
        final Element relationshipTypeEl = inputTypeConfigEl.getChild( "relationship-type" );
        if ( relationshipTypeEl != null && StringUtils.isNotBlank( relationshipTypeEl.getText() ) )
        {
            builder.relationshipType( RelationshipTypeName.from( relationshipTypeEl.getText() ) );
        }
        return builder.build();
    }
}
