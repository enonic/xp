package com.enonic.wem.api.content.type.component.inputtype;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public abstract class AbstractInputTypeConfigJsonSerializer
{
    public JsonNode serialize( final InputTypeConfig config, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put( config.getClass().getName(), serializeConfig( config, objectMapper ) );
        return jsonObject;
    }

    public abstract JsonNode serializeConfig( final InputTypeConfig config, final ObjectMapper objectMapper );

    public abstract InputTypeConfig parseConfig( final JsonNode inputTypeConfigNode );
}
