package com.enonic.wem.api.schema.content.form.inputtype;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.schema.relationship.QualifiedRelationshipTypeName;

import static com.enonic.wem.api.schema.content.form.inputtype.ImageSelectorConfig.newImageSelectorConfig;


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
        final ImageSelectorConfig.Builder builder = newImageSelectorConfig();
        final JsonNode relationshipTypeNode = inputTypeConfigNode.get( "relationshipType" );
        if ( relationshipTypeNode != null && !relationshipTypeNode.isNull() )
        {
            builder.relationshipType( QualifiedRelationshipTypeName.from( relationshipTypeNode.getTextValue() ) );
        }
        return builder.build();
    }
}
