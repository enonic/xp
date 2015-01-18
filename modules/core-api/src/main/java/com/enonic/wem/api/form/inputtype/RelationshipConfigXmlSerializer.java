package com.enonic.wem.api.form.inputtype;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.xml.DomBuilder;
import com.enonic.wem.api.xml.DomHelper;

final class RelationshipConfigXmlSerializer
    extends AbstractInputTypeConfigXmlSerializer<RelationshipConfig>
{
    public static final RelationshipConfigXmlSerializer DEFAULT = new RelationshipConfigXmlSerializer();

    @Override
    protected void serializeConfig( final RelationshipConfig relationshipConfig, final DomBuilder builder )
    {
        builder.start( "relationship-type" );
        if ( relationshipConfig.getRelationshipType() != null )
        {
            builder.text( relationshipConfig.getRelationshipType().toString() );
        }

        builder.end();
    }

    @Override
    public RelationshipConfig parseConfig( final Element elem )
    {
        final RelationshipConfig.Builder builder = RelationshipConfig.newRelationshipConfig();
        final Element relationshipTypeEl = DomHelper.getChildElementByTagName( elem, "relationship-type" );

        final String text = DomHelper.getTextValue( relationshipTypeEl );
        if ( text != null && StringUtils.isNotBlank( text ) )
        {
            builder.relationshipType( RelationshipTypeName.from( text ) );
        }
        return builder.build();
    }
}
