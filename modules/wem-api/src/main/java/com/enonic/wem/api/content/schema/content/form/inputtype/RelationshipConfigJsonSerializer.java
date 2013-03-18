package com.enonic.wem.api.content.schema.content.form.inputtype;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;

import static com.enonic.wem.api.content.schema.content.form.inputtype.RelationshipConfig.newRelationshipConfig;


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
            builder.relationshipType( QualifiedRelationshipTypeName.from( relationshipTypeNode.getTextValue() ) );
        }
        return builder.build();
    }
}
