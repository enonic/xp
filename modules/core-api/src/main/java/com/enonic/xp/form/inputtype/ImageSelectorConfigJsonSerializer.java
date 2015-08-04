package com.enonic.xp.form.inputtype;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.annotations.Beta;

@Beta
public class ImageSelectorConfigJsonSerializer
    implements InputTypeConfigJsonSerializer<ImageSelectorConfig>
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
}
