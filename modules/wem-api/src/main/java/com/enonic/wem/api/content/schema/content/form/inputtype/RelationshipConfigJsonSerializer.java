package com.enonic.wem.api.content.schema.content.form.inputtype;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.schema.content.QualifiedContentTypeName;
import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;


public class RelationshipConfigJsonSerializer
    extends AbstractInputTypeConfigJsonSerializer<RelationshipConfig>
{
    public static final RelationshipConfigJsonSerializer DEFAULT = new RelationshipConfigJsonSerializer();

    @Override
    public JsonNode serializeConfig( final RelationshipConfig config, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonConfig = objectMapper.createObjectNode();
        final ArrayNode allowedContentTypesArray = jsonConfig.putArray( "allowContentTypes" );
        for ( QualifiedContentTypeName allowedContentType : config.getAllowedContentTypes() )
        {
            allowedContentTypesArray.add( allowedContentType.toString() );
        }
        jsonConfig.put( "relationshipType", config.getRelationshipType().toString() );
        return jsonConfig;
    }

    @Override
    public RelationshipConfig parseConfig( final JsonNode inputTypeConfigNode )
    {
        final RelationshipConfig.Builder builder = RelationshipConfig.newRelationshipConfig();
        final JsonNode relationshipTypeNode = inputTypeConfigNode.get( "relationshipType" );
        if ( relationshipTypeNode != null && !relationshipTypeNode.isNull() )
        {
            builder.relationshipType( QualifiedRelationshipTypeName.from( relationshipTypeNode.getTextValue() ) );
        }
        final JsonNode allowedContentTypesNode = inputTypeConfigNode.get( "allowContentTypes" );
        if ( allowedContentTypesNode != null )
        {
            for ( JsonNode contentTypeNode : allowedContentTypesNode )
            {
                builder.allowedContentType( QualifiedContentTypeName.from( contentTypeNode.asText() ) );
            }
        }

        return builder.build();
    }
}
