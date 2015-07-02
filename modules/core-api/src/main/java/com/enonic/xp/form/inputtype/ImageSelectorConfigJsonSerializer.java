package com.enonic.xp.form.inputtype;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.annotations.Beta;

import com.enonic.xp.schema.relationship.RelationshipTypeName;


@Beta
public class ImageSelectorConfigJsonSerializer
    extends AbstractInputTypeConfigJsonSerializer<ImageSelectorConfig>
{
    public static final ImageSelectorConfigJsonSerializer DEFAULT = new ImageSelectorConfigJsonSerializer();

    @Override
    public JsonNode serializeConfig( final ImageSelectorConfig config, final ObjectMapper objectMapper )
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
    public ImageSelectorConfig parseConfig( final JsonNode inputTypeConfigNode )
    {
        final ImageSelectorConfig.Builder builder = ImageSelectorConfig.create();
        final JsonNode relationshipTypeNode = inputTypeConfigNode.get( "relationshipType" );
        if ( relationshipTypeNode != null && !relationshipTypeNode.isNull() )
        {
            builder.relationshipType( RelationshipTypeName.from( relationshipTypeNode.textValue() ) );
        }
        return builder.build();
    }
}
