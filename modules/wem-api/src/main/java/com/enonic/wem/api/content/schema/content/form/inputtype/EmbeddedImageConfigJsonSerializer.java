package com.enonic.wem.api.content.schema.content.form.inputtype;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.enonic.wem.api.content.schema.relationship.QualifiedRelationshipTypeName;

import static com.enonic.wem.api.content.schema.content.form.inputtype.EmbeddedImageConfig.newEmbeddedImageConfig;


public class EmbeddedImageConfigJsonSerializer
    extends AbstractInputTypeConfigJsonSerializer<EmbeddedImageConfig>
{
    public static final EmbeddedImageConfigJsonSerializer DEFAULT = new EmbeddedImageConfigJsonSerializer();

    @Override
    public JsonNode serializeConfig( final EmbeddedImageConfig config, final ObjectMapper objectMapper )
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
    public EmbeddedImageConfig parseConfig( final JsonNode inputTypeConfigNode )
    {
        final EmbeddedImageConfig.Builder builder = newEmbeddedImageConfig();
        final JsonNode relationshipTypeNode = inputTypeConfigNode.get( "relationshipType" );
        if ( relationshipTypeNode != null && !relationshipTypeNode.isNull() )
        {
            builder.relationshipType( QualifiedRelationshipTypeName.from( relationshipTypeNode.getTextValue() ) );
        }
        return builder.build();
    }
}
