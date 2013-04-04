package com.enonic.wem.api.content.schema.content.form.inputtype;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;

import static com.enonic.wem.api.content.schema.content.form.inputtype.ImageConfig.newImageConfig;


public class ImageConfigJsonSerializer
    extends AbstractInputTypeConfigJsonSerializer<ImageConfig>
{
    public static final ImageConfigJsonSerializer DEFAULT = new ImageConfigJsonSerializer();

    @Override
    public JsonNode serializeConfig( final ImageConfig config, final ObjectMapper objectMapper )
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
    public ImageConfig parseConfig( final JsonNode inputTypeConfigNode )
    {
        final ImageConfig.Builder builder = newImageConfig();
        final JsonNode relationshipTypeNode = inputTypeConfigNode.get( "relationshipType" );
        if ( relationshipTypeNode != null && !relationshipTypeNode.isNull() )
        {
            builder.relationshipType( QualifiedRelationshipTypeName.from( relationshipTypeNode.getTextValue() ) );
        }
        return builder.build();
    }
}
