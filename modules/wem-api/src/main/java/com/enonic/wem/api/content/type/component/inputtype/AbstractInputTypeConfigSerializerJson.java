package com.enonic.wem.api.content.type.component.inputtype;


import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public abstract class AbstractInputTypeConfigSerializerJson
{
    public JsonNode generate( final InputTypeConfig config, final ObjectMapper objectMapper )
    {
        final ObjectNode jsonObject = objectMapper.createObjectNode();
        jsonObject.put( config.getClass().getName(), generateConfig( config, objectMapper ) );
        return jsonObject;
    }

    public abstract JsonNode generateConfig( final InputTypeConfig config, final ObjectMapper objectMapper );

    public abstract InputTypeConfig parseConfig( final JsonNode inputTypeConfigNode );
}
