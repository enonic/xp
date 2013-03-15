package com.enonic.wem.api.content.schema.content.form.inputtype;


import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;

import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;

public class RelationshipConfigXmlSerializer
    extends AbstractInputTypeConfigXmlSerializer<RelationshipConfig>
{
    public static final RelationshipConfigXmlSerializer DEFAULT = new RelationshipConfigXmlSerializer();

    public void generateConfig( final RelationshipConfig relationshipConfig, final Element inputTypeConfigEl )
    {
        final Element contentTypeFilterEl = new Element( "content-type-filter" );
        inputTypeConfigEl.addContent( contentTypeFilterEl );

        for ( QualifiedContentTypeName contentType : relationshipConfig.getAllowedContentTypes() )
        {
            contentTypeFilterEl.addContent( new Element( "allow" ).setText( contentType.toString() ) );
        }
        inputTypeConfigEl.addContent( new Element( "relationship-type" ).setText( relationshipConfig.getRelationshipType().toString() ) );
    }

    @Override
    public RelationshipConfig parseConfig( final Element inputTypeConfigEl )
    {
        final RelationshipConfig.Builder builder = RelationshipConfig.newRelationshipConfig();
        final Element contentTypeFilterEl = inputTypeConfigEl.getChild( "content-type-filter" );
        if ( contentTypeFilterEl != null )
        {
            final List<Element> allowEls = contentTypeFilterEl.getChildren( "allow" );
            for ( Element allowEl : allowEls )
            {
                builder.allowedContentType( QualifiedContentTypeName.from( allowEl.getText() ) );
            }
        }
        final Element relationshipTypeEl = inputTypeConfigEl.getChild( "relationship-type" );
        if ( relationshipTypeEl != null && StringUtils.isNotBlank( relationshipTypeEl.getText() ) )
        {
            builder.relationshipType( QualifiedRelationshipTypeName.from( relationshipTypeEl.getText() ) );
        }
        return builder.build();
    }
}
