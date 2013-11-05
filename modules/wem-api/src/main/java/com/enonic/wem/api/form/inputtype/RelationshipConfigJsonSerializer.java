package com.enonic.wem.api.form.inputtype;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.wem.api.schema.relationship.RelationshipTypeName;

import static com.enonic.wem.api.form.inputtype.RelationshipConfig.newRelationshipConfig;


public class RelationshipConfigJsonSerializer
    extends AbstractInputTypeConfigJsonSerializer<RelationshipConfig>
{
    public static final RelationshipConfigJsonSerializer DEFAULT = new RelationshipConfigJsonSerializer();

    @Override
    public JsonNode serializeConfig( final RelationshipConfig config, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonConfig = objectMapper.createObjectNode();
        if ( config.getRelationshipType() != null )
        {
            jsonConfig.put( "relationshipType", config.getRelationshipType().toString() );
        }
        else
        {
            jsonConfig.putNull( "relationshipType" );
        }
        return jsonConfig;
    }

    @Override
    public RelationshipConfig parseConfig( final JsonNode inputTypeConfigNode )
    {
        final RelationshipConfig.Builder builder = newRelationshipConfig();
        final JsonNode relationshipTypeNode = inputTypeConfigNode.get( "relationshipType" );
        if ( relationshipTypeNode != null && !relationshipTypeNode.isNull() )
        {
            builder.relationshipType( RelationshipTypeName.from( relationshipTypeNode.textValue() ) );
        }
        return builder.build();
    }
}
